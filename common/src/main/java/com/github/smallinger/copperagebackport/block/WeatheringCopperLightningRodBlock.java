package com.github.smallinger.copperagebackport.block;

import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Weathering Copper Lightning Rod block - oxidizes over time.
 * Uses own oxidation chain to work on both Fabric and NeoForge.
 */
public class WeatheringCopperLightningRodBlock extends CopperLightningRodBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperLightningRodBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
            propertiesCodec()
        ).apply(instance, WeatheringCopperLightningRodBlock::new)
    );
    
    private final WeatherState weatherState;

    @Override
    public MapCodec<? extends CopperLightningRodBlock> codec() {
        return CODEC;
    }

    public WeatheringCopperLightningRodBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    public Optional<Block> getNextBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
            case EXPOSED -> Optional.of(ModBlocks.WEATHERED_LIGHTNING_ROD.get());
            case WEATHERED -> Optional.of(ModBlocks.OXIDIZED_LIGHTNING_ROD.get());
            case OXIDIZED -> Optional.empty();
        };
    }

    public Optional<Block> getPreviousBlock() {
        return switch (this.weatherState) {
            case UNAFFECTED -> Optional.empty();
            case EXPOSED -> Optional.of(Blocks.LIGHTNING_ROD);
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
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return getNextBlock().isPresent();
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        return getNextBlock().map(block -> block.withPropertiesOf(state));
    }

    @Override
    public float getChanceModifier() {
        return 1.0F;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Handle honeycomb waxing
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = getWaxedBlock();
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3003, pos, 0); // Wax on particles
                
                if (!level.isClientSide) {
                    level.setBlock(pos, waxedBlock.get().withPropertiesOf(state), 11);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
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
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND 
                            ? net.minecraft.world.entity.EquipmentSlot.MAINHAND 
                            : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
