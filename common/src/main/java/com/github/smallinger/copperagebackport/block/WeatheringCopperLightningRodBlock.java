package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.util.WeatheringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Lightning Rod block - oxidizes over time.
 * Uses own oxidation chain to work on both Fabric and Forge.
 * Note: Base lightning_rod is vanilla, extended via Mixin.
 */
public class WeatheringCopperLightningRodBlock extends CopperLightningRodBlock implements WeatheringCopper {
    
    private final WeatherState weatherState;

    public WeatheringCopperLightningRodBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    public static Optional<Block> getNextBlock(Block block) {
        // Vanilla lightning_rod is handled by Mixin
        if (block == Blocks.LIGHTNING_ROD) {
            return Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
        } else if (block == ModBlocks.EXPOSED_LIGHTNING_ROD.get()) {
            return Optional.of(ModBlocks.WEATHERED_LIGHTNING_ROD.get());
        } else if (block == ModBlocks.WEATHERED_LIGHTNING_ROD.get()) {
            return Optional.of(ModBlocks.OXIDIZED_LIGHTNING_ROD.get());
        }
        return Optional.empty();
    }

    public Optional<Block> getPreviousBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.empty();
            case EXPOSED -> Optional.of(Blocks.LIGHTNING_ROD); // Vanilla lightning rod
            case WEATHERED -> Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
            case OXIDIZED -> Optional.of(ModBlocks.WEATHERED_LIGHTNING_ROD.get());
        };
    }

    public Optional<Block> getWaxedBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.WAXED_LIGHTNING_ROD.get());
            case EXPOSED -> Optional.of(ModBlocks.WAXED_EXPOSED_LIGHTNING_ROD.get());
            case WEATHERED -> Optional.of(ModBlocks.WAXED_WEATHERED_LIGHTNING_ROD.get());
            case OXIDIZED -> Optional.of(ModBlocks.WAXED_OXIDIZED_LIGHTNING_ROD.get());
        };
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        WeatheringHelper.tryWeather(state, level, pos, random, WeatheringCopperLightningRodBlock::getNextBlock);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return WeatheringHelper.canWeather(state, WeatheringCopperLightningRodBlock::getNextBlock);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                  InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle honeycomb waxing
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock();
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // Wax on particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, waxedBlock.get().withPropertiesOf(state), 11);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // Handle axe scraping (remove oxidation)
        if (stack.is(ItemTags.AXES)) {
            Optional<Block> previousBlock = getPreviousBlock();
            if (previousBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos, 0); // Scrape particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, previousBlock.get().withPropertiesOf(state), 11);
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
