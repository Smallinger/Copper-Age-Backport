package com.github.smallinger.coppergolemlegacy.entity.ai.behavior;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.ModMemoryTypes;
import com.github.smallinger.coppergolemlegacy.block.CopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.entity.CopperGolemEntity;
import com.github.smallinger.coppergolemlegacy.entity.CopperGolemState;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<GlobalPos, Long> visitedButtonTimestamps = new HashMap<>(); // Button -> last visit time
    
    @Nullable
    private BlockPos targetButton;
    private int ticksSinceReached = 0;
    private long lastButtonPressTime = -1000; // Time when last button was pressed (any button)
    
    public PressRandomCopperButton(float speedModifier, int horizontalSearchDistance, int verticalSearchDistance, int pressInterval) {
        super(
            ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(), MemoryStatus.REGISTERED
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
        
        // Wait at least 5-15 seconds after pressing any button before looking for the next one
        long currentTime = level.getGameTime();
        int minWaitTime = 100 + random.nextInt(200); // 5-15 seconds
        if (lastButtonPressTime > 0 && currentTime - lastButtonPressTime < minWaitTime) {
            return false; // Still in cooldown after last button press
        }
        
        // Clean up old timestamps (buttons not visited in the last 40 seconds)
        int randomCooldown = 400 + random.nextInt(400); // 20-40 seconds random cooldown
        visitedButtonTimestamps.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() >= randomCooldown
        );
        
        // Find a random copper button nearby
        BlockPos buttonPos = findNearestCopperButton(level, golem, currentTime);
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
            
            // Keep stopping the golem for first 5 ticks to ensure complete stop
            if (this.ticksSinceReached <= 5) {
                this.stopInPlace(golem);
            }
            
            // Start animation after stopping (give 5 ticks to stop moving completely)
            if (this.ticksSinceReached == 6) {
                golem.setState(CopperGolemState.PRESSING_BUTTON);
            }
            
            // Press the button at peak of animation (tick 14: 5 stop + 1 start + 8 animation peak)
            // Animation peaks at 0.375 seconds (7.5 ticks), so we press at tick 14
            if (this.ticksSinceReached == 14) {
                pressButton(level, golem, this.targetButton);
            }
            
            // Keep animation running until complete (30 ticks: 5 stop + 1 start + 20 animation + 4 buffer)
            if (this.ticksSinceReached >= 30) {
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
            
            // Add to visited positions with current timestamp to avoid spam
            GlobalPos globalPos = GlobalPos.of(level.dimension(), pos);
            long currentTime = level.getGameTime();
            visitedButtonTimestamps.put(globalPos, currentTime);
            
            // Update last button press time for global cooldown
            this.lastButtonPressTime = currentTime;
        }
    }
    
    /**
     * Find the nearest copper button that hasn't been visited recently
     */
    @Nullable
    private BlockPos findNearestCopperButton(ServerLevel level, CopperGolemEntity golem, long currentTime) {
        BlockPos golemPos = golem.blockPosition();
        List<BlockPos> candidateButtons = new ArrayList<>();
        
        int randomCooldown = 400 + random.nextInt(400); // 20-40 seconds random cooldown per button
        
        // Search in a cube around the golem
        for (int x = -horizontalSearchDistance; x <= horizontalSearchDistance; x++) {
            for (int y = -verticalSearchDistance; y <= verticalSearchDistance; y++) {
                for (int z = -horizontalSearchDistance; z <= horizontalSearchDistance; z++) {
                    BlockPos checkPos = golemPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    // Check if it's a copper button
                    if (isCopperButton(state.getBlock())) {
                        // Check if it's not already pressed
                        if (!state.getValue(BlockStateProperties.POWERED)) {
                            // Check if we haven't visited it recently (within cooldown period)
                            GlobalPos globalPos = GlobalPos.of(level.dimension(), checkPos);
                            Long lastVisitTime = visitedButtonTimestamps.get(globalPos);
                            
                            if (lastVisitTime == null || currentTime - lastVisitTime >= randomCooldown) {
                                candidateButtons.add(checkPos);
                            }
                        }
                    }
                }
            }
        }
        
        // Return a random button from candidates
        if (!candidateButtons.isEmpty()) {
            return candidateButtons.get(golem.getRandom().nextInt(candidateButtons.size()));
        }
        
        return null;
    }
    
    /**
     * Check if a block is a copper button
     */
    private boolean isCopperButton(Block block) {
        return block == CopperGolemLegacy.COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.EXPOSED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.WEATHERED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.OXIDIZED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.WAXED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.WAXED_EXPOSED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.WAXED_WEATHERED_COPPER_BUTTON.get() ||
               block == CopperGolemLegacy.WAXED_OXIDIZED_COPPER_BUTTON.get();
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
}
