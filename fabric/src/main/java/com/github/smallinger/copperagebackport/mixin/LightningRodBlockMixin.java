package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
 * 
 * IMPORTANT: We implement oxidation directly instead of using changeOverTime() because:
 * - Other mods (like Friends and Foes) register their own lightning rod variants in the
 *   WeatheringCopper static maps, which would cause our lightning rod to oxidize into
 *   THEIR blocks instead of ours.
 * - By directly setting the block state to our ModBlocks, we ensure the oxidation chain
 *   stays within our mod's blocks.
 * 
 * We use the minecraft: namespace for our blocks to improve compatibility with future
 * Minecraft versions where oxidized lightning rods may be added to vanilla.
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
    public boolean isRandomlyTicking(BlockState pState) {
        // Always tick - oxidation is always active
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Directly handle oxidation to ensure we use OUR blocks, not F&F's
        // We bypass onRandomTick/changeOverTime because it uses static maps that F&F can override
        float f = 0.05688889f;
        if (random.nextFloat() < f) {
            copperagebackport$tryOxidize(state, level, pos, random);
        }
    }

    @Unique
    private void copperagebackport$tryOxidize(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Use simplified oxidation logic that directly uses our blocks
        Optional<Block> nextBlock = copperagebackport$getNextBlock();
        if (nextBlock.isPresent()) {
            // Simple chance calculation (similar to vanilla but simplified)
            float chance = 1.0f * this.getChanceModifier();
            if (random.nextFloat() < chance) {
                level.setBlockAndUpdate(pos, nextBlock.get().withPropertiesOf(state));
            }
        }
    }

    @Override
    public Optional<BlockState> getNext(BlockState state) {
        // Always return next oxidation state
        return copperagebackport$getNextBlock().map(block -> block.withPropertiesOf(state));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
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
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public float getChanceModifier() {
        return 1.0F;
    }
}
