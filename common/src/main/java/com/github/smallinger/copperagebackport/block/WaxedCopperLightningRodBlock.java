package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Waxed Copper Lightning Rod block - doesn't oxidize.
 * Wax removal handled by use method.
 * Note: UNAFFECTED state returns vanilla lightning_rod.
 */
public class WaxedCopperLightningRodBlock extends CopperLightningRodBlock {
    
    private final WeatheringCopper.WeatherState weatheringState;

    public WaxedCopperLightningRodBlock(WeatheringCopper.WeatherState weatheringState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    public Optional<Block> getUnwaxedBlock() {
        return switch (this.weatheringState) {
            case UNAFFECTED -> Optional.of(Blocks.LIGHTNING_ROD); // Vanilla lightning rod
            case EXPOSED -> Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
            case WEATHERED -> Optional.of(ModBlocks.WEATHERED_LIGHTNING_ROD.get());
            case OXIDIZED -> Optional.of(ModBlocks.OXIDIZED_LIGHTNING_ROD.get());
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                  InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle axe scraping to remove wax
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> unwaxed = getUnwaxedBlock();
            if (unwaxed.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0); // WAX_OFF particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, unwaxed.get().withPropertiesOf(state), 11);
                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }
}
