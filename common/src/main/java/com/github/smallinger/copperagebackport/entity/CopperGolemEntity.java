package com.github.smallinger.copperagebackport.entity;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.block.CopperGolemStatueBlock;
import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.entity.ai.CopperGolemAi;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.level.block.entity.ContainerOpenersCounter;

import javax.annotation.Nullable;

public class CopperGolemEntity extends AbstractGolem implements Shearable, ContainerUser {
    private static final long IGNORE_WEATHERING_TICK = -2L;
    private static final long UNSET_WEATHERING_TICK = -1L;
    // Weathering tick values are now configurable via CommonConfig
    private static final int SPIN_ANIMATION_MIN_COOLDOWN = 200;
    private static final int SPIN_ANIMATION_MAX_COOLDOWN = 240;
    private static final float TURN_TO_STATUE_CHANCE = 0.0058F; // 0.58% chance per tick when oxidized
    private static final double CONTAINER_INTERACTION_RANGE = 3.0;
    // In vanilla 1.21.10, this is EquipmentSlot.SADDLE - a new slot added specifically for Copper Golem's antenna.
    // Since SADDLE doesn't exist in 1.20.1 (or 1.21.1), we use HEAD as a fallback slot for the antenna item.
    public static final EquipmentSlot EQUIPMENT_SLOT_ANTENNA = EquipmentSlot.HEAD;
    
    private static final EntityDataAccessor<Integer> DATA_WEATHER_STATE = 
        SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COPPER_GOLEM_STATE = 
        SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.INT);
    
    private long nextWeatheringTick = UNSET_WEATHERING_TICK;
    @Nullable
    private BlockPos openedChestPos;
    @Nullable
    private java.util.UUID lastLightningBoltUUID;
    private int idleAnimationStartTick = 0;
    
    // Helper methods for WeatherState navigation (not in 1.21.1 API)
    private static WeatheringCopper.WeatherState getNextWeatherState(WeatheringCopper.WeatherState current) {
        return switch (current) {
            case UNAFFECTED -> WeatheringCopper.WeatherState.EXPOSED;
            case EXPOSED -> WeatheringCopper.WeatherState.WEATHERED;
            case WEATHERED -> WeatheringCopper.WeatherState.OXIDIZED;
            case OXIDIZED -> WeatheringCopper.WeatherState.OXIDIZED;
        };
    }
    
    private static WeatheringCopper.WeatherState getPreviousWeatherState(WeatheringCopper.WeatherState current) {
        return switch (current) {
            case UNAFFECTED -> WeatheringCopper.WeatherState.UNAFFECTED;
            case EXPOSED -> WeatheringCopper.WeatherState.UNAFFECTED;
            case WEATHERED -> WeatheringCopper.WeatherState.EXPOSED;
            case OXIDIZED -> WeatheringCopper.WeatherState.WEATHERED;
        };
    }
    
    // Animation states for client-side rendering
    // In vanilla 1.21.10, these are private final with getter methods.
    // We use public final here because the client renderer (CopperGolemModel) accesses them directly.
    // This is functionally equivalent since they are final and cannot be reassigned.
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState interactionGetItemAnimationState = new AnimationState();
    public final AnimationState interactionGetNoItemAnimationState = new AnimationState();
    public final AnimationState interactionDropItemAnimationState = new AnimationState();
    public final AnimationState interactionDropNoItemAnimationState = new AnimationState();
    public final AnimationState pressingButtonAnimationState = new AnimationState();

    public CopperGolemEntity(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
        // Step height of 1.0 to match vanilla CopperGolem (1.21.10)
        // In 1.20.1 this is set via setMaxUpStep(), in 1.21+ it's an attribute
        this.setMaxUpStep(1.0F);
        // Navigation-Einstellungen für bessere Pfadsuche
        this.getNavigation().setMaxVisitedNodesMultiplier(3.0F);
        this.setPersistenceRequired();
        // Explicitly set initial state to IDLE (matches vanilla CopperGolem constructor)
        this.setState(CopperGolemState.IDLE);
        // Benötigt für Türöffnung mit Brain-based AI
        this.setCanPickUpLoot(true);
        // Pathfinding-Malus: Meidet Feuer-Gefahren
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DANGER_OTHER, 16.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DAMAGE_FIRE, -1.0F);
        // Türen-Pathfinding: Keine Malus für offene/geschlossene Türen
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DOOR_OPEN, 0.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.DOOR_IRON_CLOSED, -1.0F);
        // Initialize transport cooldown with random delay (60-100 ticks) to prevent immediate item transport after spawn
        // Matches vanilla CopperGolem (1.21.10) behavior
        this.getBrain().setMemory(com.github.smallinger.copperagebackport.ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), this.getRandom().nextInt(60, 100));
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Note: STEP_HEIGHT attribute doesn't exist in 1.20.1 vanilla (added in 1.21+)
        // The default step height for mobs is 0.6, which is fine for the Copper Golem
        // In 1.21.1+ we use Attributes.STEP_HEIGHT = 1.0 to match vanilla CopperGolem
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 12.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2F)
            .add(Attributes.ATTACK_DAMAGE, 1.0);  // Required for item pickup evaluation
    }

    // Eye height matching vanilla CopperGolem (1.21.10): 0.8125F
    // In 1.20.1 there's no .eyeHeight() in EntityType.Builder, so we override this method
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8125F;
    }

    // Brain-based AI statt Goal-based AI
    @Override
    protected Brain.Provider<CopperGolemEntity> brainProvider() {
        return CopperGolemAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CopperGolemAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<CopperGolemEntity> getBrain() {
        return (Brain<CopperGolemEntity>) super.getBrain();
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        com.github.smallinger.copperagebackport.entity.ai.navigation.CopperGolemNavigation navigation = 
            new com.github.smallinger.copperagebackport.entity.ai.navigation.CopperGolemNavigation(this, level);
        navigation.setCanOpenDoors(true);  // Kann Türen öffnen
        navigation.setCanPassDoors(true);  // Kann durch Türen gehen
        navigation.setRequiredPathLength(48.0F);  // Längere Pfade = bessere Navigation, weniger Blockieren
        return navigation;
    }

    // Prevent Copper Golem from picking up items from the ground
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_WEATHER_STATE, WeatheringCopper.WeatherState.UNAFFECTED.ordinal());
        this.entityData.define(COPPER_GOLEM_STATE, CopperGolemState.IDLE.ordinal());
    }

    public CopperGolemState getState() {
        int stateId = this.entityData.get(COPPER_GOLEM_STATE);
        CopperGolemState[] states = CopperGolemState.values();
        return stateId >= 0 && stateId < states.length ? states[stateId] : CopperGolemState.IDLE;
    }

    public void setState(CopperGolemState state) {
        this.entityData.set(COPPER_GOLEM_STATE, state.ordinal());
    }

    public WeatheringCopper.WeatherState getWeatherState() {
        int weatherId = this.entityData.get(DATA_WEATHER_STATE);
        WeatheringCopper.WeatherState[] states = WeatheringCopper.WeatherState.values();
        return weatherId >= 0 && weatherId < states.length ? states[weatherId] : WeatheringCopper.WeatherState.UNAFFECTED;
    }

    public void setWeatherState(WeatheringCopper.WeatherState weatherState) {
        this.entityData.set(DATA_WEATHER_STATE, weatherState.ordinal());
    }

    public void setOpenedChestPos(BlockPos openedChestPos) {
        this.openedChestPos = openedChestPos;
    }

    public void clearOpenedChestPos() {
        this.openedChestPos = null;
    }

    // ContainerUser implementation
    // Matches vanilla CopperGolem (1.21.10) behavior:
    // Supports double chests by checking both the opened chest position AND the connected chest position.
    // This prevents issues where opening one half of a double chest wouldn't properly track the other half.
    @Override
    public boolean hasContainerOpen(ContainerOpenersCounter openCounter, BlockPos pos) {
        if (this.openedChestPos == null) {
            return false;
        }
        // Check if it's the same position or a connected double chest
        if (this.openedChestPos.equals(pos)) {
            return true;
        }
        // Double chest support
        net.minecraft.world.level.block.state.BlockState blockstate = this.level().getBlockState(this.openedChestPos);
        if (blockstate.getBlock() instanceof net.minecraft.world.level.block.ChestBlock
                && blockstate.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
            net.minecraft.core.Direction connectedDirection = net.minecraft.world.level.block.ChestBlock.getConnectedDirection(blockstate);
            return this.openedChestPos.relative(connectedDirection).equals(pos);
        }
        return false;
    }

    @Override
    public double getContainerInteractionRange() {
        return CONTAINER_INTERACTION_RANGE;
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("copperGolemBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("copperGolemActivityUpdate");
        CopperGolemAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("next_weather_age", this.nextWeatheringTick);
        compound.putInt("weather_state", this.getWeatherState().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.nextWeatheringTick = compound.getLong("next_weather_age");
        if (compound.contains("weather_state")) {
            int weatherId = compound.getInt("weather_state");
            WeatheringCopper.WeatherState[] states = WeatheringCopper.WeatherState.values();
            if (weatherId >= 0 && weatherId < states.length) {
                this.setWeatherState(states[weatherId]);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            if (!this.isNoAi()) {
                this.setupAnimationStates();
            }
        } else {
            this.updateWeathering((ServerLevel)this.level(), this.level().getRandom(), this.level().getGameTime());
        }
    }

    private void updateWeathering(ServerLevel level, RandomSource random, long dayTime) {
        if (this.nextWeatheringTick != IGNORE_WEATHERING_TICK) {
            if (this.nextWeatheringTick == UNSET_WEATHERING_TICK) {
                this.nextWeatheringTick = dayTime + random.nextIntBetweenInclusive(
                    CommonConfig.weatheringTickFrom(), CommonConfig.weatheringTickTo());
            } else {
                WeatheringCopper.WeatherState weatherState = this.getWeatherState();
                boolean isOxidized = weatherState == WeatheringCopper.WeatherState.OXIDIZED;
                
                if (dayTime >= this.nextWeatheringTick && !isOxidized) {
                    WeatheringCopper.WeatherState nextState = getNextWeatherState(weatherState);
                    boolean willBeOxidized = nextState == WeatheringCopper.WeatherState.OXIDIZED;
                    this.setWeatherState(nextState);
                    this.nextWeatheringTick = willBeOxidized ? 0L : 
                        this.nextWeatheringTick + random.nextIntBetweenInclusive(
                            CommonConfig.weatheringTickFrom(), CommonConfig.weatheringTickTo());
                }
                
                // Check if golem should turn into statue when fully oxidized
                if (isOxidized && canTurnToStatue(level)) {
                    turnToStatue(level);
                }
            }
        }
    }
    
    private boolean canTurnToStatue(Level level) {
        return level.getBlockState(this.blockPosition()).is(Blocks.AIR) && 
               level.random.nextFloat() <= TURN_TO_STATUE_CHANCE;
    }
    
    private void turnToStatue(ServerLevel level) {
        BlockPos blockPos = this.blockPosition();
        CopperGolemStatueBlock.Pose randomPose = CopperGolemStatueBlock.Pose.values()[
            this.random.nextInt(0, CopperGolemStatueBlock.Pose.values().length)
        ];
        
        level.setBlock(
            blockPos,
            ModBlocks.OXIDIZED_COPPER_GOLEM_STATUE.get()
                .defaultBlockState()
                .setValue(CopperGolemStatueBlock.POSE, randomPose)
                .setValue(CopperGolemStatueBlock.FACING, net.minecraft.core.Direction.fromYRot(this.getYRot())),
            3
        );
        
        if (level.getBlockEntity(blockPos) instanceof CopperGolemStatueBlockEntity statueEntity) {
            statueEntity.createStatue(this);
            this.dropPreservedEquipment();
            // Matches vanilla order: discard entity first, then play sound
            this.discard();
            this.playSound(ModSounds.COPPER_GOLEM_BECOME_STATUE.get());
            
            // Drop leash if leashed
            // In vanilla 1.21.10, this uses dropLeash() and removeLeash() separately.
            // Since removeLeash() doesn't exist in 1.21.1/1.20.1, we use dropLeash(broadcast, dropItem) for both cases.
            if (this.isLeashed()) {
                if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.dropLeash(true, true);  // broadcast and drop item
                } else {
                    this.dropLeash(true, false); // broadcast but don't drop item
                }
            }
        }
    }

    private void setupAnimationStates() {
        switch (this.getState()) {
            case IDLE:
                this.interactionGetNoItemAnimationState.stop();
                this.interactionGetItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.pressingButtonAnimationState.stop();
                
                if (this.idleAnimationStartTick == this.tickCount) {
                    this.idleAnimationState.start(this.tickCount);
                } else if (this.idleAnimationStartTick == 0) {
                    this.idleAnimationStartTick = this.tickCount + this.random.nextInt(SPIN_ANIMATION_MIN_COOLDOWN, SPIN_ANIMATION_MAX_COOLDOWN);
                }

                if (this.tickCount == this.idleAnimationStartTick + 10) {
                    this.playHeadSpinSound();
                    this.idleAnimationStartTick = 0;
                }
                break;
            case GETTING_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.pressingButtonAnimationState.stop();
                this.interactionGetItemAnimationState.startIfStopped(this.tickCount);
                break;
            case GETTING_NO_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.pressingButtonAnimationState.stop();
                this.interactionGetNoItemAnimationState.startIfStopped(this.tickCount);
                break;
            case DROPPING_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.pressingButtonAnimationState.stop();
                this.interactionDropItemAnimationState.startIfStopped(this.tickCount);
                break;
            case DROPPING_NO_ITEM:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.pressingButtonAnimationState.stop();
                this.interactionDropNoItemAnimationState.startIfStopped(this.tickCount);
                break;
            case PRESSING_BUTTON:
                this.idleAnimationState.stop();
                this.idleAnimationStartTick = 0;
                this.interactionGetItemAnimationState.stop();
                this.interactionGetNoItemAnimationState.stop();
                this.interactionDropItemAnimationState.stop();
                this.interactionDropNoItemAnimationState.stop();
                this.pressingButtonAnimationState.startIfStopped(this.tickCount);
                break;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Level level = this.level();

        // Empty hand - throw held item to player
        if (itemstack.isEmpty() && !this.getMainHandItem().isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack heldItem = this.getMainHandItem();
                BehaviorUtils.throwItem(this, heldItem, player.position());
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }

        // Shears interaction
        if (itemstack.is(Items.SHEARS) && this.readyForShearing()) {
            if (level instanceof ServerLevel serverLevel) {
                this.shear(SoundSource.PLAYERS);
                this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.SHEAR, player);
                itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Honeycomb - stops weathering
        if (itemstack.is(Items.HONEYCOMB) && this.nextWeatheringTick != IGNORE_WEATHERING_TICK) {
            if (!level.isClientSide()) {
                level.levelEvent(null, 3003, this.blockPosition(), 0);
                this.nextWeatheringTick = IGNORE_WEATHERING_TICK;
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Axe - removes weathering levels
        if (itemstack.is(ItemTags.AXES)) {
            if (!level.isClientSide()) {
                WeatheringCopper.WeatherState weatherState = this.getWeatherState();
                
                // If honeycomb was applied, remove it first
                if (this.nextWeatheringTick == IGNORE_WEATHERING_TICK) {
                    level.playSound(null, this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
                    level.levelEvent(null, 3004, this.blockPosition(), 0);
                    this.nextWeatheringTick = UNSET_WEATHERING_TICK;
                    itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    return InteractionResult.SUCCESS;
                }
                
                // Otherwise reduce oxidation
                if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED) {
                    level.playSound(null, this, SoundEvents.AXE_SCRAPE, this.getSoundSource(), 1.0F, 1.0F);
                    level.levelEvent(null, 3005, this.blockPosition(), 0);
                    this.nextWeatheringTick = UNSET_WEATHERING_TICK;
                    this.setWeatherState(getPreviousWeatherState(weatherState));
                    itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    private void playHeadSpinSound() {
        if (!this.isSilent()) {
            this.level().playLocalSound(
                this.getX(), this.getY(), this.getZ(),
                this.getSpinHeadSound(),
                this.getSoundSource(),
                1.0F, 1.0F, false
            );
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).hurtSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).deathSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).stepSound(), 1.0F, 1.0F);
    }
    
    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }
    
    @Override
    protected float nextStep() {
        return this.moveDist + 0.6F;
    }

    private SoundEvent getSpinHeadSound() {
        return CopperGolemOxidationLevels.getOxidationLevel(this.getWeatherState()).spinHeadSound();
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75 * this.getEyeHeight(), 0.0);
    }

    // Custom implementation for 1.20.1 since dropPreservedEquipment() doesn't exist
    // This method drops all equipment items (main hand, off hand, armor) without random chance
    private void dropPreservedEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getItemBySlot(slot);
            if (!itemStack.isEmpty()) {
                this.spawnAtLocation(itemStack);
                this.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void shear(SoundSource source) {
        this.level().playSound(null, this, ModSounds.COPPER_GOLEM_SHEAR.get(), source, 1.0F, 1.0F);
        // Remove antenna item if implemented
        ItemStack antennaItem = this.getItemBySlot(EQUIPMENT_SLOT_ANTENNA);
        if (!antennaItem.isEmpty()) {
            this.setItemSlot(EQUIPMENT_SLOT_ANTENNA, ItemStack.EMPTY);
            this.spawnAtLocation(antennaItem, 1.5F);
        }
    }

    @Override
    public boolean readyForShearing() {
        // In vanilla 1.21.10, this checks ItemTags.SHEARABLE_FROM_COPPER_GOLEM which only contains poppy.
        // We check directly for poppy since the tag doesn't exist in older versions.
        return this.isAlive() && this.getItemBySlot(EQUIPMENT_SLOT_ANTENNA).is(Items.POPPY);
    }

    /**
     * Called when the Copper Golem is spawned from building the structure (pumpkin + copper block).
     * Sets the weather state based on the copper block used and plays the spawn sound.
     * Matches vanilla CopperGolem (1.21.10) behavior.
     */
    public void spawn(WeatheringCopper.WeatherState weatherState) {
        this.setWeatherState(weatherState);
        this.playSpawnSound();
    }

    /**
     * Plays the spawn sound for the Copper Golem.
     * Called both when spawning from structure and from spawn egg.
     */
    public void playSpawnSound() {
        this.playSound(ModSounds.COPPER_GOLEM_SPAWN.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
        ServerLevelAccessor level,
        DifficultyInstance difficulty,
        MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnData,
        @Nullable CompoundTag dataTag
    ) {
        this.playSpawnSound();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        super.thunderHit(level, lightning);
        java.util.UUID uuid = lightning.getUUID();
        // Only process once per lightning bolt
        if (!uuid.equals(this.lastLightningBoltUUID)) {
            this.lastLightningBoltUUID = uuid;
            // Lightning removes one oxidation level
            WeatheringCopper.WeatherState weatherState = this.getWeatherState();
            if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED) {
                this.nextWeatheringTick = UNSET_WEATHERING_TICK;
                this.setWeatherState(getPreviousWeatherState(weatherState));
            }
        }
    }

    // change hurt() to actuallyHurt() to reset state to IDLE
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, amount);
        this.setState(CopperGolemState.IDLE);
    }

    // Copper ingot drops are handled by the entity loot table at:
    // data/copperagebackport/loot_tables/entities/copper_golem.json
    // Drops 1-3 copper ingots with looting enchantment bonus (vanilla behavior)

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.dropPreservedEquipment();
    }
}

