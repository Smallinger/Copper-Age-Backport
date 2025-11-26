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
    private static final double CONTAINER_INTERACTION_RANGE = 4.0;
    
    private static final EntityDataAccessor<Integer> DATA_WEATHER_STATE = 
        SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COPPER_GOLEM_STATE = 
        SynchedEntityData.defineId(CopperGolemEntity.class, EntityDataSerializers.INT);
    
    private long nextWeatheringTick = UNSET_WEATHERING_TICK;
    @Nullable
    private BlockPos openedChestPos;
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
    
    // Animation states für Client
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState interactionGetItemAnimationState = new AnimationState();
    public final AnimationState interactionGetNoItemAnimationState = new AnimationState();
    public final AnimationState interactionDropItemAnimationState = new AnimationState();
    public final AnimationState interactionDropNoItemAnimationState = new AnimationState();
    public final AnimationState pressingButtonAnimationState = new AnimationState();

    public CopperGolemEntity(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
        // Navigation-Einstellungen für bessere Pfadsuche
        this.getNavigation().setMaxVisitedNodesMultiplier(3.0F);
        this.setPersistenceRequired();
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
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 12.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2F)
            .add(Attributes.ATTACK_DAMAGE, 1.0);  // Required for item pickup evaluation
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
    @Override
    public boolean hasContainerOpen(ContainerOpenersCounter openCounter, BlockPos pos) {
        return this.openedChestPos != null && this.openedChestPos.equals(pos);
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
            this.playSound(ModSounds.COPPER_GOLEM_BECOME_STATUE.get());
            level.playSound(null, blockPos, ModSounds.COPPER_GOLEM_BECOME_STATUE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            // Drop held item if any
            ItemStack heldItem = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!heldItem.isEmpty()) {
                this.spawnAtLocation(heldItem);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
            
            // Drop leash if leashed
            if (this.isLeashed()) {
                this.dropLeash(true, level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS));
            }
            
            this.discard();
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

        // Shears interaction
        if (itemstack.is(Items.SHEARS) && this.readyForShearing()) {
            if (level instanceof ServerLevel serverLevel) {
                this.shear(SoundSource.PLAYERS);
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

    @Override
    public void shear(SoundSource source) {
        this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.0F);
        // Remove antenna item if implemented
        ItemStack antennaItem = this.getItemBySlot(EquipmentSlot.HEAD);
        if (!antennaItem.isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            this.spawnAtLocation(antennaItem, 1.5F);
        }
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
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
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR); // Placeholder for spawn sound
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        super.thunderHit(level, lightning);
        // Lightning removes one oxidation level
        WeatheringCopper.WeatherState weatherState = this.getWeatherState();
        if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED) {
            this.nextWeatheringTick = UNSET_WEATHERING_TICK;
            this.setWeatherState(getPreviousWeatherState(weatherState));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result) {
            this.setState(CopperGolemState.IDLE);
        }
        return result;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean hitByPlayer) {
        super.dropCustomDeathLoot(damageSource, looting, hitByPlayer);
        // Drop 3 copper ingots when killed
        ItemStack copperIngots = new ItemStack(Items.COPPER_INGOT, 3);
        this.spawnAtLocation(copperIngots);
    }
}

