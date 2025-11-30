package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

/**
 * Mixin to add weathering functionality to the vanilla Lightning Rod block.
 * This makes the vanilla lightning rod oxidize over time like other copper blocks.
 * Uses own oxidation chain to work on both Fabric and NeoForge.
 */
@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends RodBlock implements WeatheringCopper, ChangeOverTimeBlock<WeatheringCopper.WeatherState> {

    public LightningRodBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public WeatherState getAge() {
        return WeatherState.UNAFFECTED;
    }

    @Unique
    private Optional<Block> copperagebackport$getNextBlock() {
        return Optional.of(ModBlocks.EXPOSED_LIGHTNING_ROD.get());
    }

    @Unique
    private Optional<Block> copperagebackport$getWaxedBlock() {
        return Optional.of(ModBlocks.WAXED_LIGHTNING_ROD.get());
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return CommonConfig.lightningRodOxidation();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (CommonConfig.lightningRodOxidation()) {
            this.changeOverTime(state, level, pos, random);
        }
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        if (!CommonConfig.lightningRodOxidation()) {
            return Optional.empty();
        }
        return copperagebackport$getNextBlock().map(block -> block.withPropertiesOf(state));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Handle honeycomb waxing
        if (stack.is(Items.HONEYCOMB)) {
            Optional<Block> waxedBlock = copperagebackport$getWaxedBlock();
            if (waxedBlock.isPresent()) {
                level.playSound(player, pos, net.minecraft.sounds.SoundEvents.HONEYCOMB_WAX_ON, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
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
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public float getChanceModifier() {
        return 1.0F;
    }
}
