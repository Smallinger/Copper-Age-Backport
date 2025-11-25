package com.github.smallinger.copperagebackport.entity.ai.behavior;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.block.CopperButtonBlock;
import com.github.smallinger.copperagebackport.block.WaxedCopperButtonBlock;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemState;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * AI Behavior for Copper Golem to randomly press copper buttons
 * The golem will search for nearby copper buttons and walk to them to press them
 */
public class PressRandomCopperButton extends Behavior<CopperGolemEntity> {
    private final float speedModifier;
    private final int horizontalSearchDistance;
    private final int verticalSearchDistance;
    private final int pressInterval; // Minimum ticks between button presses
    private final Random random = new Random();
    
    @Nullable
    private BlockPos targetButton;
    private int ticksSinceReached = 0;
    
    public PressRandomCopperButton(float speedModifier, int horizontalSearchDistance, int verticalSearchDistance, int pressInterval) {
        super(
            ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), MemoryStatus.REGISTERED,
                ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), MemoryStatus.VALUE_PRESENT
            ),
            pressInterval // Run every pressInterval ticks minimum
        );
        this.speedModifier = speedModifier;
        this.horizontalSearchDistance = horizontalSearchDistance;
        this.verticalSearchDistance = verticalSearchDistance;
        this.pressInterval = pressInterval;
    }
    
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, CopperGolemEntity golem) {
        // Don't look for buttons if we're already targeting one
        if (this.targetButton != null) {
            return false;
        }
        
        // Nur starten wenn Golem im IDLE-State ist
        if (golem.getState() != CopperGolemState.IDLE) {
            return false;
        }
        
        // Nur starten wenn LAST_CONTAINER_EMPTY Memory vorhanden ist (Copper Chest war leer)
        Optional<Long> lastEmptyTime = golem.getBrain().getMemory(ModMemoryTypes.LAST_CONTAINER_EMPTY.get());
        if (lastEmptyTime.isEmpty()) {
            return false; // Keine leere Copper Chest gefunden, kein ButtonPress
        }
        
        // Config-basierte Chance für ButtonPress nach leerem Container (0-100%)
        int chancePercent = com.github.smallinger.copperagebackport.config.CommonConfig.buttonPressChancePercent();
        float chanceFloat = chancePercent / 100.0F;
        if (random.nextFloat() >= chanceFloat) {
            // Kein Glück, lösche Memory für nächsten Versuch
            golem.getBrain().eraseMemory(ModMemoryTypes.LAST_CONTAINER_EMPTY.get());
            return false;
        }
        
        // Lösche LAST_CONTAINER_EMPTY Memory nach erfolgreichem Trigger
        golem.getBrain().eraseMemory(ModMemoryTypes.LAST_CONTAINER_EMPTY.get());
        
        // Finde einen komplett zufälligen Button in Reichweite (egal ob nah oder fern)
        BlockPos buttonPos = findRandomCopperButton(level, golem);
        if (buttonPos != null) {
            this.targetButton = buttonPos;
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void start(ServerLevel level, CopperGolemEntity golem, long gameTime) {
        if (this.targetButton != null) {
            // Set flag to prevent other behaviors from interrupting
            golem.getBrain().setMemory(ModMemoryTypes.IS_PRESSING_BUTTON.get(), true);
            
            // Set walk target to the button - must be at distance 0 (right next to it)
            golem.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetButton, this.speedModifier, 0));
            golem.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.targetButton));
        }
    }
    
    @Override
    protected boolean canStillUse(ServerLevel level, CopperGolemEntity golem, long gameTime) {
        if (this.targetButton == null) {
            return false;
        }
        
        // Check if we've reached the button - must be within 0.8 blocks (very close)
        double distanceSqr = golem.blockPosition().distSqr(this.targetButton);
        if (distanceSqr <= 0.5) {
            // Stop the golem completely when reaching the button
            if (this.ticksSinceReached == 0) {
                this.stopInPlace(golem);
            }
            
            this.ticksSinceReached++;
            
            // Keep stopping the golem and rotating to face button for first 10 ticks
            if (this.ticksSinceReached <= 10) {
                this.stopInPlace(golem);
                // Actively rotate golem to face the button
                this.rotateTowardsButton(golem, this.targetButton);
            }
            
            // Start animation after stopping and rotating (give 10 ticks to stop and face button)
            if (this.ticksSinceReached == 11) {
                golem.setState(CopperGolemState.PRESSING_BUTTON);
            }
            
            // Press the button at peak of animation (tick 19: 10 stop+rotate + 1 start + 8 animation peak)
            // Animation peaks at 0.375 seconds (7.5 ticks), so we press at tick 19
            if (this.ticksSinceReached == 19) {
                pressButton(level, golem, this.targetButton);
            }
            
            // Keep animation running until complete (35 ticks: 10 stop+rotate + 1 start + 20 animation + 4 buffer)
            if (this.ticksSinceReached >= 35) {
                return false; // Done with this button
            }
            return true;

        }
        
        // Check if button still exists and is valid
        BlockState state = level.getBlockState(this.targetButton);
        if (!isCopperButton(state.getBlock())) {
            return false;
        }
        
        // Keep going if we're still walking
        return this.ticksSinceReached < 200; // Give up after 10 seconds
    }
    
    @Override
    protected void stop(ServerLevel level, CopperGolemEntity golem, long gameTime) {
        // Clear flag to allow other behaviors
        golem.getBrain().eraseMemory(ModMemoryTypes.IS_PRESSING_BUTTON.get());
        
        // Clear memory
        golem.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        golem.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        
        // Reset state to idle
        golem.setState(CopperGolemState.IDLE);
        
        this.targetButton = null;
        this.ticksSinceReached = 0;
    }
    
    /**
     * Press the button at the given position
     */
    private void pressButton(ServerLevel level, CopperGolemEntity golem, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        
        if (block instanceof ButtonBlock buttonBlock) {
            // Check if button is already pressed
            if (state.getValue(BlockStateProperties.POWERED)) {
                return; // Button is already pressed
            }
            
            // Check if it's an oxidized copper button (non-waxed)
            if (block instanceof CopperButtonBlock copperButton) {
                if (copperButton.getAge() == net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED) {
                    // Oxidized buttons can't be pressed
                    level.playSound(null, pos, SoundEvents.COPPER_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return;
                }
            }
            
            // Press the button (manually set powered state and schedule tick)
            BlockState poweredState = state.setValue(BlockStateProperties.POWERED, true);
            level.setBlock(pos, poweredState, 3);
            
            // Play button click sound
            level.playSound(null, pos, SoundEvents.COPPER_HIT, SoundSource.BLOCKS, 0.3F, 0.6F);
            
            // Schedule unpressing (15 ticks for copper buttons)
            level.scheduleTick(pos, buttonBlock, 15);
            
            // Update redstone neighbors
            level.updateNeighborsAt(pos, buttonBlock);
            // Get the attached direction from the block state properties
            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            net.minecraft.core.Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            net.minecraft.core.Direction attachedDirection;
            if (face == AttachFace.FLOOR) {
                attachedDirection = net.minecraft.core.Direction.DOWN;
            } else if (face == AttachFace.CEILING) {
                attachedDirection = net.minecraft.core.Direction.UP;
            } else {
                attachedDirection = facing.getOpposite();
            }
            BlockPos attachedPos = pos.relative(attachedDirection);
            level.updateNeighborsAt(attachedPos, buttonBlock);
        }
    }
    
    /**
     * Findet einen komplett zufälligen Copper Button in Reichweite
     * Sammelt ALLE verfügbaren Buttons und wählt einen zufällig aus
     * Keine Cooldown-Checks - jedes Mal komplett zufällige Auswahl
     */
    @Nullable
    private BlockPos findRandomCopperButton(ServerLevel level, CopperGolemEntity golem) {
        BlockPos golemPos = golem.blockPosition();
        List<BlockPos> allAvailableButtons = new ArrayList<>();
        
        // Suche in einem Würfel um den Golem herum
        for (int x = -horizontalSearchDistance; x <= horizontalSearchDistance; x++) {
            for (int y = -verticalSearchDistance; y <= verticalSearchDistance; y++) {
                for (int z = -horizontalSearchDistance; z <= horizontalSearchDistance; z++) {
                    BlockPos checkPos = golemPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    // Prüfe ob es ein Copper Button ist
                    if (isCopperButton(state.getBlock())) {
                        // Prüfe ob er nicht bereits gedrückt ist
                        if (!state.getValue(BlockStateProperties.POWERED)) {
                            // Füge zu Liste hinzu - ALLE Buttons sind Kandidaten
                            allAvailableButtons.add(checkPos);
                        }
                    }
                }
            }
        }
        
        // Wähle komplett zufällig einen Button aus der Liste
        // Kann mal der näheste, mal der entfernteste, mal irgendein anderer sein
        if (!allAvailableButtons.isEmpty()) {
            return allAvailableButtons.get(golem.getRandom().nextInt(allAvailableButtons.size()));
        }
        
        return null;
    }
    
    /**
     * Check if a block is a copper button
     */
    private boolean isCopperButton(Block block) {
        return block == ModBlocks.COPPER_BUTTON.get() ||
               block == ModBlocks.EXPOSED_COPPER_BUTTON.get() ||
               block == ModBlocks.WEATHERED_COPPER_BUTTON.get() ||
               block == ModBlocks.OXIDIZED_COPPER_BUTTON.get() ||
               block == ModBlocks.WAXED_COPPER_BUTTON.get() ||
               block == ModBlocks.WAXED_EXPOSED_COPPER_BUTTON.get() ||
               block == ModBlocks.WAXED_WEATHERED_COPPER_BUTTON.get() ||
               block == ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON.get();
    }
    
    /**
     * Stop the golem in place completely
     */
    private void stopInPlace(CopperGolemEntity golem) {
        golem.getNavigation().stop();
        golem.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        golem.setXxa(0.0F);
        golem.setYya(0.0F);
        golem.setZza(0.0F);
        golem.setSpeed(0.0F);
        golem.setDeltaMovement(0.0, golem.getDeltaMovement().y, 0.0);
        // Force stop any limb swing animation
        golem.walkAnimation.setSpeed(0.0F);
    }
    
    /**
     * Rotate golem to face the button
     */
    private void rotateTowardsButton(CopperGolemEntity golem, BlockPos buttonPos) {
        // Calculate the direction vector from golem to button
        double dx = buttonPos.getX() + 0.5 - golem.getX();
        double dz = buttonPos.getZ() + 0.5 - golem.getZ();
        
        // Calculate desired yaw angle
        float desiredYaw = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        
        // Normalize angles to -180 to 180 range
        desiredYaw = net.minecraft.util.Mth.wrapDegrees(desiredYaw);
        float currentYaw = net.minecraft.util.Mth.wrapDegrees(golem.getYRot());
        
        // Calculate shortest rotation direction
        float yawDiff = net.minecraft.util.Mth.wrapDegrees(desiredYaw - currentYaw);
        
        // Rotate smoothly (max 30 degrees per tick for smooth rotation)
        float rotationStep = net.minecraft.util.Mth.clamp(yawDiff, -30.0F, 30.0F);
        float newYaw = currentYaw + rotationStep;
        
        // Apply rotation
        golem.setYRot(newYaw);
        golem.yRotO = newYaw;
        golem.setYHeadRot(newYaw);
        golem.yHeadRotO = newYaw;
        golem.yBodyRot = newYaw;
        golem.yBodyRotO = newYaw;
    }
}
