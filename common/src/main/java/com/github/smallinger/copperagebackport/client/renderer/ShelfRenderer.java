package com.github.smallinger.copperagebackport.client.renderer;

import com.github.smallinger.copperagebackport.block.shelf.ShelfBlock;
import com.github.smallinger.copperagebackport.block.shelf.ShelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private static final float ITEM_SIZE = 0.25F;
    private static final float SLOT_OFFSET = 0.3125F;
    private static final float ALIGN_ITEMS_TO_BOTTOM_OFFSET = -0.25F;
    private final ItemRenderer itemRenderer;

    public ShelfRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ShelfBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction direction = blockEntity.getBlockState().getValue(ShelfBlock.FACING);
        NonNullList<ItemStack> items = blockEntity.getItems();
        int seed = (int) blockEntity.getBlockPos().asLong();
        boolean alignToBottom = blockEntity.getAlignItemsToBottom();
        
        // Calculate rotation based on facing direction
        float rotation = direction.getAxis().isHorizontal() ? -direction.toYRot() : 180.0F;
        
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) continue;
            
            poseStack.pushPose();
            
            // Start at center of block
            poseStack.translate(0.5F, 0.5F, 0.5F);
            
            // Rotate to face the right direction
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            
            // Calculate position offset for this slot
            // Horizontal offset based on slot index (0, 1, 2 -> -1, 0, 1)
            float horizontalOffset = (i - 1) * SLOT_OFFSET;
            // Vertical offset if aligning to bottom
            float verticalOffset = alignToBottom ? ALIGN_ITEMS_TO_BOTTOM_OFFSET : 0.0F;
            // Z offset to bring items forward from the shelf back
            float depthOffset = -0.25F;
            
            poseStack.translate(horizontalOffset, verticalOffset, depthOffset);
            
            // Rotate items 180° so they face the player (forward) instead of backward
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            
            // Scale items
            poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            
            // Render
            this.itemRenderer.renderStatic(
                itemStack, 
                ItemDisplayContext.FIXED, 
                packedLight, 
                packedOverlay, 
                poseStack, 
                bufferSource, 
                blockEntity.getLevel(), 
                seed + i
            );
            
            poseStack.popPose();
        }
    }
}
