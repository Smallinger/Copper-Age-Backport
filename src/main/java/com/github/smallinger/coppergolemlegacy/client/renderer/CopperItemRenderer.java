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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom item renderer for Copper Chest and Copper Golem Statue items.
 * Renders these items with full 3D models instead of flat textures.
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
        
        // Check if it's a copper chest
        if (isChestBlock(block)) {
            renderChestItem(block, poseStack, bufferSource, packedLight, packedOverlay);
        } 
        // Check if it's a golem statue
        else if (block instanceof CopperGolemStatueBlock) {
            renderStatueItem(block, stack, poseStack, bufferSource, packedLight, packedOverlay);
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

    private void renderChestItem(Block block, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        CopperChestBlockEntity blockEntity = new CopperChestBlockEntity(BlockPos.ZERO, block.defaultBlockState());
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
            .renderItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderStatueItem(Block block, ItemStack stack, PoseStack poseStack,
                                  MultiBufferSource bufferSource,
                                  int packedLight, int packedOverlay) {
        BlockState state = resolveBlockState(block, stack);
        CopperGolemStatueBlockEntity blockEntity = new CopperGolemStatueBlockEntity(BlockPos.ZERO, state);
        blockEntity.applyComponentsFromItemStack(stack);
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
            .renderItem(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private BlockState resolveBlockState(Block block, ItemStack stack) {
        BlockState state = block.defaultBlockState();
        BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
        if (properties != null && !properties.isEmpty()) {
            state = properties.apply(state);
        }
        return state;
    }

}
