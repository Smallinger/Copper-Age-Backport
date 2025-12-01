package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/**
 * Base Copper Bulb block that responds to redstone signals.
 * When powered, it toggles its lit state (T flip-flop behavior).
 * Provides comparator output of 15 when lit, 0 when not lit.
 */
public class CopperBulbBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    
    protected final WeatheringCopper.WeatherState weatherState;
    protected Supplier<WaxedCopperBulbBlock> waxedBulb;

    public CopperBulbBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(POWERED, false));
    }
    
    public void setWaxedBulb(Supplier<WaxedCopperBulbBlock> waxedBulb) {
        this.waxedBulb = waxedBulb;
    }
    
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverLevel) {
            this.checkAndFlip(state, serverLevel, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel) {
            this.checkAndFlip(state, serverLevel, pos);
        }
    }

    /**
     * Checks redstone signal and toggles lit state on rising edge.
     * This creates the T flip-flop behavior that Copper Bulbs are known for.
     */
    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean hasPower = level.hasNeighborSignal(pos);
        if (hasPower != state.getValue(POWERED)) {
            BlockState newState = state;
            // Only toggle LIT on rising edge (when power turns ON)
            if (!state.getValue(POWERED)) {
                newState = state.cycle(LIT);
                level.playSound(
                    null, pos, 
                    newState.getValue(LIT) ? ModSounds.COPPER_BULB_TURN_ON.get() : ModSounds.COPPER_BULB_TURN_OFF.get(), 
                    SoundSource.BLOCKS
                );
            }
            level.setBlock(pos, newState.setValue(POWERED, hasPower), 3);
        }
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Check if player is using honeycomb to wax the bulb
        if (stack.is(Items.HONEYCOMB) && waxedBulb != null) {
            level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3003, pos, 0);
            
            if (!level.isClientSide) {
                // Replace with waxed version, preserving bulb state
                BlockState waxedState = waxedBulb.get().defaultBlockState()
                    .setValue(LIT, state.getValue(LIT))
                    .setValue(POWERED, state.getValue(POWERED));
                level.setBlock(pos, waxedState, 11);
                
                if (player != null && !player.isCreative()) {
                    stack.shrink(1);
                }
            }
            
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        
        // Check if player is using an axe to scrape oxidation
        if (stack.is(ItemTags.AXES)) {
            Block previousBlock = WeatheringCopperBulbBlock.getPreviousBlock(this);
            
            if (previousBlock != null) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0);
                
                if (!level.isClientSide) {
                    // Replace with previous oxidation state, preserving bulb state
                    BlockState newState = previousBlock.defaultBlockState()
                        .setValue(LIT, state.getValue(LIT))
                        .setValue(POWERED, state.getValue(POWERED));
                    level.setBlock(pos, newState, 11);
                    
                    if (player != null && !player.isCreative()) {
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    }
                }
                
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }
    
    /**
     * Creates a light level function for a specific max light value.
     * Returns the max value when lit, 0 when not lit.
     * 
     * Light levels by oxidation state (vanilla behavior):
     * - UNAFFECTED: 15
     * - EXPOSED: 12
     * - WEATHERED: 8
     * - OXIDIZED: 4
     */
    public static int getLightLevel(BlockState state, int maxLight) {
        return state.getValue(LIT) ? maxLight : 0;
    }
    
    // Light level functions for each oxidation stage
    public static int getLightLevelUnaffected(BlockState state) {
        return state.getValue(LIT) ? 15 : 0;
    }
    
    public static int getLightLevelExposed(BlockState state) {
        return state.getValue(LIT) ? 12 : 0;
    }
    
    public static int getLightLevelWeathered(BlockState state) {
        return state.getValue(LIT) ? 8 : 0;
    }
    
    public static int getLightLevelOxidized(BlockState state) {
        return state.getValue(LIT) ? 4 : 0;
    }
}
