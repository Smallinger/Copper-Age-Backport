package com.github.smallinger.coppergolemlegacy.client.renderer;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.client.model.CopperGolemModel;
import com.github.smallinger.coppergolemlegacy.client.model.CopperGolemStatueModel;
import com.github.smallinger.coppergolemlegacy.entity.CopperGolemOxidationLevels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom item renderer for Copper Chest and Copper Golem Statue items.
 * Renders these items with full 3D models instead of flat textures.
 */
public class CopperItemRenderer extends BlockEntityWithoutLevelRenderer {
    
    // Chest model parts - lazy initialized
    private ModelPart chestLid;
    private ModelPart chestBottom;
    private ModelPart chestLock;
    
    // Statue models for each pose - lazy initialized
    private Map<CopperGolemStatueBlock.Pose, CopperGolemStatueModel> statueModels;
    
    // Flag to track initialization
    private boolean initialized = false;
    
    // Chest materials for different oxidation levels
    private static final Material COPPER_CHEST_MATERIAL = chestMaterial("copper");
    private static final Material EXPOSED_COPPER_CHEST_MATERIAL = chestMaterial("copper_exposed");
    private static final Material WEATHERED_COPPER_CHEST_MATERIAL = chestMaterial("copper_weathered");
    private static final Material OXIDIZED_COPPER_CHEST_MATERIAL = chestMaterial("copper_oxidized");

    public CopperItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), 
              Minecraft.getInstance().getEntityModels());
    }
    
    private void ensureInitialized() {
        if (initialized) {
            return;
        }
        
        // Initialize chest model
        ModelPart singleChest = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.CHEST);
        this.chestBottom = singleChest.getChild("bottom");
        this.chestLid = singleChest.getChild("lid");
        this.chestLock = singleChest.getChild("lock");
        
        // Initialize statue models
        this.statueModels = new HashMap<>();
        var modelSet = Minecraft.getInstance().getEntityModels();
        this.statueModels.put(CopperGolemStatueBlock.Pose.STANDING, 
            new CopperGolemStatueModel(modelSet.bakeLayer(CopperGolemModel.STATUE_STANDING)));
        this.statueModels.put(CopperGolemStatueBlock.Pose.RUNNING, 
            new CopperGolemStatueModel(modelSet.bakeLayer(CopperGolemModel.STATUE_RUNNING)));
        this.statueModels.put(CopperGolemStatueBlock.Pose.SITTING, 
            new CopperGolemStatueModel(modelSet.bakeLayer(CopperGolemModel.STATUE_SITTING)));
        this.statueModels.put(CopperGolemStatueBlock.Pose.STAR, 
            new CopperGolemStatueModel(modelSet.bakeLayer(CopperGolemModel.STATUE_STAR)));
        
        initialized = true;
    }

    private static Material chestMaterial(String name) {
        return new Material(Sheets.CHEST_SHEET, 
            ResourceLocation.fromNamespaceAndPath(CopperGolemLegacy.MODID, "entity/chest/" + name));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, 
                            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Ensure models are initialized before rendering
        ensureInitialized();
        
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        
        Block block = blockItem.getBlock();
        
        // Check if it's a copper chest
        if (isChestBlock(block)) {
            renderChestItem(block, poseStack, bufferSource, packedLight, packedOverlay);
        } 
        // Check if it's a golem statue
        else if (block instanceof CopperGolemStatueBlock statueBlock) {
            renderStatueItem(statueBlock, poseStack, bufferSource, packedLight, packedOverlay);
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
        poseStack.pushPose();
        
        // Center the chest model at origin after applying context transforms
        poseStack.translate(0.5F, 0.5F, 0.5F);
        
        // Get the appropriate material based on oxidation
        Material material = getChestMaterial(block);
        VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityCutout);
        
        // Render chest with closed lid
        float openness = 0.0F;
        chestLid.xRot = -(openness * ((float)Math.PI / 2F));
        chestLock.xRot = chestLid.xRot;
        
        chestLid.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        chestLock.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        chestBottom.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        
        poseStack.popPose();
    }

    private void renderStatueItem(CopperGolemStatueBlock statueBlock, PoseStack poseStack, 
                                  MultiBufferSource bufferSource, 
                                  int packedLight, int packedOverlay) {
        poseStack.pushPose();
        
        // Center the statue model horizontally at ground level
        poseStack.translate(0.5F, 0.0F, 0.5F);
        
        // Use standing pose for item display
        CopperGolemStatueModel model = statueModels.get(CopperGolemStatueBlock.Pose.STANDING);
        if (model == null) {
            poseStack.popPose();
            return;
        }
        
        // Get texture based on oxidation level
        ResourceLocation texture = CopperGolemOxidationLevels.getOxidationLevel(
            statueBlock.getWeatheringState()
        ).texture();
        
        // Setup model with default facing (south)
        model.setupAnim(Direction.SOUTH);
        
        // Render the model
        RenderType renderType = RenderType.entityCutoutNoCull(texture);
        model.renderToBuffer(
            poseStack,
            bufferSource.getBuffer(renderType),
            packedLight,
            packedOverlay,
            -1 // White color
        );
        
        poseStack.popPose();
    }

    private Material getChestMaterial(Block block) {
        if (block == CopperGolemLegacy.COPPER_CHEST.get() || block == CopperGolemLegacy.WAXED_COPPER_CHEST.get()) {
            return COPPER_CHEST_MATERIAL;
        } else if (block == CopperGolemLegacy.EXPOSED_COPPER_CHEST.get() || block == CopperGolemLegacy.WAXED_EXPOSED_COPPER_CHEST.get()) {
            return EXPOSED_COPPER_CHEST_MATERIAL;
        } else if (block == CopperGolemLegacy.WEATHERED_COPPER_CHEST.get() || block == CopperGolemLegacy.WAXED_WEATHERED_COPPER_CHEST.get()) {
            return WEATHERED_COPPER_CHEST_MATERIAL;
        } else if (block == CopperGolemLegacy.OXIDIZED_COPPER_CHEST.get() || block == CopperGolemLegacy.WAXED_OXIDIZED_COPPER_CHEST.get()) {
            return OXIDIZED_COPPER_CHEST_MATERIAL;
        }
        return COPPER_CHEST_MATERIAL; // Fallback
    }

}
