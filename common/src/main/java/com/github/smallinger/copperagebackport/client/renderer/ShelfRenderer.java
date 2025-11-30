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
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ShelfRenderer implements BlockEntityRenderer<ShelfBlockEntity> {
    private static final float ITEM_SIZE = 0.25F;
    private static final float SLOT_OFFSET = 0.3125F;
    private static final float ALIGN_ITEMS_TO_BOTTOM_OFFSET = -0.25F;
    
    // Special values for oversized items (to match 1.21.10 ON_SHELF behavior)
    private static final float BANNER_SCALE = 0.5F;      // Banners need larger display
    private static final float BANNER_Y_OFFSET = -0.1F;   // Y offset for banners
    
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
            
            // Get scale factor and Y offset for this item type
            // In 1.21.10, ON_SHELF display context handles this via model transforms
            // In 1.20.1, we need to manually adjust special items
            float scale = getItemScale(itemStack);
            float yOffset = getItemYOffset(itemStack);
            
            poseStack.translate(0.0F, yOffset, 0.0F);
            poseStack.scale(scale, scale, scale);
            
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
    
    /**
     * Get the scale factor for a specific item type.
     * Items with special models (banners) need larger scales
     * to match the 1.21.10 ON_SHELF display context behavior.
     */
    private float getItemScale(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BannerItem) {
            return BANNER_SCALE;
        }
        return ITEM_SIZE;
    }
    
    /**
     * Get the Y offset for a specific item type.
     * Items with special models (banners) need vertical adjustment
     * to match the 1.21.10 ON_SHELF display context behavior.
     */
    private float getItemYOffset(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BannerItem) {
            return BANNER_Y_OFFSET;
        }
        return 0.0F;
    }
}
