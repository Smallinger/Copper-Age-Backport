package com.github.smallinger.coppergolemlegacy.client.renderer;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperGolemStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Custom item renderer for Copper Chest and Copper Golem Statue items.
 * Delegates rendering to the block entity dispatcher so display context transforms stay in sync with vanilla.
 */
public class CopperItemRenderer extends BlockEntityWithoutLevelRenderer {

    public CopperItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), 
              Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, 
                            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
        Block block = blockItem.getBlock();
        
        if (isChestBlock(block)) {
            renderChestItem(stack, block, poseStack, bufferSource, packedLight, packedOverlay);
        } else if (block instanceof CopperGolemStatueBlock statueBlock) {
            renderStatueItem(stack, statueBlock, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private boolean isChestBlock(Block block) {
        return block == CopperGolemLegacy.COPPER_CHEST.get() ||
               block == CopperGolemLegacy.EXPOSED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.WEATHERED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.WAXED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.WAXED_EXPOSED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.WAXED_WEATHERED_COPPER_CHEST.get() ||
               block == CopperGolemLegacy.WAXED_OXIDIZED_COPPER_CHEST.get();
    }

    private void renderChestItem(ItemStack stack, Block block, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = getBlockStateFromStack(stack, block);
        CopperChestBlockEntity blockEntity = new CopperChestBlockEntity(BlockPos.ZERO, state);
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderStatueItem(ItemStack stack, CopperGolemStatueBlock statueBlock, PoseStack poseStack,
                                  MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = getBlockStateFromStack(stack, statueBlock);
        CopperGolemStatueBlockEntity blockEntity = new CopperGolemStatueBlockEntity(BlockPos.ZERO, state);
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private BlockState getBlockStateFromStack(ItemStack stack, Block block) {
        BlockState state = block.defaultBlockState();
        CompoundTag stateTag = stack.getTagElement(BlockItem.BLOCK_STATE_TAG);
        if (stateTag == null) {
            return state;
        }

        StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
        for (String key : stateTag.getAllKeys()) {
            Property<?> property = stateDefinition.getProperty(key);
            if (property != null) {
                state = applyProperty(state, property, stateTag.getString(key));
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }
}
