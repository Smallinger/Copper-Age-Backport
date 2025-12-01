package com.github.smallinger.copperagebackport.client.renderer.layers;

import com.github.smallinger.copperagebackport.client.model.CopperGolemModel;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renders an item (like a Poppy flower) on top of the Copper Golem's antenna.
 * This is a backport of the vanilla 1.21.10 BlockDecorationLayer functionality
 * specifically for the Copper Golem.
 * 
 * When an Iron Golem gives a flower to a Copper Golem, the flower is placed
 * in the EQUIPMENT_SLOT_ANTENNA (mapped to HEAD slot in 1.21.1) and this layer
 * renders it on top of the Copper Golem's antenna.
 */
public class CopperGolemAntennaLayer extends RenderLayer<CopperGolemEntity, CopperGolemModel> {
    
    private final BlockRenderDispatcher blockRenderer;
    
    public CopperGolemAntennaLayer(RenderLayerParent<CopperGolemEntity, CopperGolemModel> renderer) {
        super(renderer);
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, 
                      CopperGolemEntity entity, float limbSwing, float limbSwingAmount, 
                      float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        
        // Get the item in the antenna slot
        ItemStack antennaItem = entity.getItemBySlot(CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA);
        
        if (antennaItem.isEmpty()) {
            return;
        }
        
        // Only render BlockItems (like Poppy flower)
        if (!(antennaItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
        BlockState blockState = blockItem.getBlock().defaultBlockState();
        
        poseStack.pushPose();
        
        // Apply antenna transform from the model
        this.getParentModel().applyBlockOnAntennaTransform(poseStack);
        
        // Flip the block upside down and adjust rotation (like vanilla does)
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        
        // Scale down the block to fit on the antenna
        poseStack.scale(0.5F, 0.5F, 0.5F);
        
        // Center the block
        poseStack.translate(-0.5, -0.5, -0.5);
        
        // Render the block
        this.blockRenderer.renderSingleBlock(
            blockState, 
            poseStack, 
            buffer, 
            packedLight, 
            OverlayTexture.NO_OVERLAY
        );
        
        poseStack.popPose();
    }
}
