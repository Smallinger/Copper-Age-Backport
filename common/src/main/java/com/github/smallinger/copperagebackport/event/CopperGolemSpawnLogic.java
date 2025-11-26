package com.github.smallinger.copperagebackport.event;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.block.CopperChestBlock;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared spawn logic that both Forge and Fabric callers can invoke when a carved
 * pumpkin is placed. All loader-specific event wiring should delegate here.
 */
public final class CopperGolemSpawnLogic {

    private CopperGolemSpawnLogic() {
    }

    /**
     * Attempt to spawn the Copper Golem if the placed block completes the
     * structure.
     */
    public static void handleBlockPlaced(Level level, BlockPos pos, BlockState placedState, Direction playerDirection) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Direction effectiveDirection = playerDirection != null ? playerDirection : Direction.NORTH;

        if (placedState.is(Blocks.CARVED_PUMPKIN)) {
            trySpawnCopperGolem(serverLevel, pos, effectiveDirection);
        }
    }
    
    private static void trySpawnCopperGolem(ServerLevel level, BlockPos pumpkinPos, Direction playerDirection) {
        // Check if there's a copper block below the pumpkin
        BlockPos copperPos = pumpkinPos.below();
        BlockState copperState = level.getBlockState(copperPos);
        
        // Check if it's a copper block (any oxidation state)
        if (copperState.is(ModTags.Blocks.COPPER)) {
            // Use opposite direction to face the player (180 degrees)
            Direction direction = playerDirection.getOpposite();
            
            // Play break particles for both blocks (matching vanilla CopperGolem 1.21.10 behavior)
            // This shows the block break particles before removing the blocks
            level.levelEvent(2001, pumpkinPos, Block.getId(Blocks.CARVED_PUMPKIN.defaultBlockState()));
            level.levelEvent(2001, copperPos, Block.getId(copperState));
            
            // Remove the pumpkin
            level.setBlock(pumpkinPos, Blocks.AIR.defaultBlockState(), 2);
            
            // Replace copper block with copper chest using the original logic
            Block copperBlock = copperState.getBlock();
            BlockState chestState = CopperChestBlock.getFromCopperBlock(copperBlock, direction, level, copperPos);
            level.setBlock(copperPos, chestState, 2);
            
            // Spawn the Copper Golem
            CopperGolemEntity copperGolem = ModEntities.COPPER_GOLEM.get().create(level);
            if (copperGolem != null) {
                // Position on top of the chest (Y + 1.0 to be on top of the chest collision box)
                // Set rotation: chest direction - 90 degrees clockwise
                float yaw = direction.toYRot();
                copperGolem.moveTo(
                    copperPos.getX() + 0.5,
                    copperPos.getY() + 1.0,
                    copperPos.getZ() + 0.5,
                    yaw,
                    0.0F
                );
                
                // Explicitly set all rotation values to ensure correct facing
                copperGolem.setYRot(yaw);
                copperGolem.yRotO = yaw;
                copperGolem.setYBodyRot(yaw);
                copperGolem.yBodyRotO = yaw;
                copperGolem.setYHeadRot(yaw);
                copperGolem.yHeadRotO = yaw;
                
                // Get the weather state from the copper block
                WeatheringCopper.WeatherState weatherState = getWeatherStateFromBlock(copperState.getBlock());
                
                // Use the spawn() method to set weather state and play spawn sound
                // This matches vanilla CopperGolem (1.21.10) behavior
                copperGolem.spawn(weatherState);
                
                // Set initial transport cooldown (140 ticks = 7 seconds)
                // This prevents the golem from immediately trying to interact with the spawn chest
                // and allows the idle walk animation to trigger naturally
                copperGolem.getBrain().setMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), 140);
                
                // Add to world
                level.addFreshEntity(copperGolem);
                
                // Trigger advancement for nearby players
                for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class,
                    copperGolem.getBoundingBox().inflate(5.0))) {
                    // Could trigger custom advancement here
                }
                
                // Update neighboring blocks
                level.updateNeighborsAt(copperPos, chestState.getBlock());
                level.updateNeighborsAt(pumpkinPos, Blocks.AIR);
            }
        }
    }
    
    private static WeatheringCopper.WeatherState getWeatherStateFromBlock(Block block) {
        if (block instanceof WeatheringCopper weatheringCopper) {
            return weatheringCopper.getAge();
        }
        
        // Check if it's a waxed variant
        if (block == Blocks.WAXED_COPPER_BLOCK) {
            return WeatheringCopper.WeatherState.UNAFFECTED;
        } else if (block == Blocks.WAXED_EXPOSED_COPPER) {
            return WeatheringCopper.WeatherState.EXPOSED;
        } else if (block == Blocks.WAXED_WEATHERED_COPPER) {
            return WeatheringCopper.WeatherState.WEATHERED;
        } else if (block == Blocks.WAXED_OXIDIZED_COPPER) {
            return WeatheringCopper.WeatherState.OXIDIZED;
        }
        
        // Default to unaffected
        return WeatheringCopper.WeatherState.UNAFFECTED;
    }
}

