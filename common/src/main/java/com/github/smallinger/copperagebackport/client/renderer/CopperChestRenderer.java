package com.github.smallinger.copperagebackport.client.renderer;

import com.github.smallinger.copperagebackport.block.CopperChestBlock;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.github.smallinger.copperagebackport.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class CopperChestRenderer implements BlockEntityRenderer<ChestBlockEntity> {
    
    // Copper chest materials
    private static final Material COPPER_CHEST_MATERIAL = chestMaterial("copper");
    private static final Material COPPER_CHEST_LEFT_MATERIAL = chestMaterial("copper_left");
    private static final Material COPPER_CHEST_RIGHT_MATERIAL = chestMaterial("copper_right");
    
    private static final Material EXPOSED_COPPER_CHEST_MATERIAL = chestMaterial("copper_exposed");
    private static final Material EXPOSED_COPPER_CHEST_LEFT_MATERIAL = chestMaterial("copper_exposed_left");
    private static final Material EXPOSED_COPPER_CHEST_RIGHT_MATERIAL = chestMaterial("copper_exposed_right");
    
    private static final Material WEATHERED_COPPER_CHEST_MATERIAL = chestMaterial("copper_weathered");
    private static final Material WEATHERED_COPPER_CHEST_LEFT_MATERIAL = chestMaterial("copper_weathered_left");
    private static final Material WEATHERED_COPPER_CHEST_RIGHT_MATERIAL = chestMaterial("copper_weathered_right");
    
    private static final Material OXIDIZED_COPPER_CHEST_MATERIAL = chestMaterial("copper_oxidized");
    private static final Material OXIDIZED_COPPER_CHEST_LEFT_MATERIAL = chestMaterial("copper_oxidized_left");
    private static final Material OXIDIZED_COPPER_CHEST_RIGHT_MATERIAL = chestMaterial("copper_oxidized_right");

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;

    public CopperChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart singleChest = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = singleChest.getChild("bottom");
        this.lid = singleChest.getChild("lid");
        this.lock = singleChest.getChild("lock");
        
        ModelPart doubleChestLeft = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleLeftBottom = doubleChestLeft.getChild("bottom");
        this.doubleLeftLid = doubleChestLeft.getChild("lid");
        this.doubleLeftLock = doubleChestLeft.getChild("lock");
        
        ModelPart doubleChestRight = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.doubleRightBottom = doubleChestRight.getChild("bottom");
        this.doubleRightLid = doubleChestRight.getChild("lid");
        this.doubleRightLock = doubleChestRight.getChild("lock");
    }

    private static Material chestMaterial(String name) {
        return new Material(Sheets.CHEST_SHEET, new ResourceLocation(Constants.MOD_ID, "entity/chest/" + name));
    }

    @Override
    public void render(ChestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        boolean hasLevel = level != null;
        BlockState blockstate = blockEntity.getBlockState();
        Block block = blockstate.getBlock();

        CopperChestBlock copperChestBlock;
        if (block instanceof CopperChestBlock chestBlock) {
            copperChestBlock = chestBlock;
        } else {
            blockstate = ModBlocks.COPPER_CHEST.get().defaultBlockState();
            block = blockstate.getBlock();
            if (!(block instanceof CopperChestBlock fallback)) {
                return;
            }
            copperChestBlock = fallback;
        }

        if (!blockstate.hasProperty(ChestBlock.FACING)) {
            blockstate = blockstate.getBlock().defaultBlockState();
        }
        if (!blockstate.hasProperty(ChestBlock.FACING)) {
            blockstate = ModBlocks.COPPER_CHEST.get().defaultBlockState();
        }
        if (!blockstate.hasProperty(ChestBlock.FACING)) {
            return;
        }

        if (!hasLevel) {
            blockstate = blockstate.setValue(ChestBlock.FACING, Direction.SOUTH);
        }

        poseStack.pushPose();
        float f = blockstate.getValue(ChestBlock.FACING).toYRot();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-f));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> result = hasLevel
            ? copperChestBlock.combine(blockstate, level, blockEntity.getBlockPos(), true)
            : DoubleBlockCombiner.Combiner::acceptNone;

        float openness = result.apply(ChestBlock.opennessCombiner((LidBlockEntity) blockEntity)).get(partialTick);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        int light = result.apply(new BrightnessCombiner<>()).applyAsInt(packedLight);

        ChestType chestType = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        Material material = getMaterial(blockstate, chestType);
        VertexConsumer vertexconsumer = material.buffer(bufferSource, RenderType::entityCutout);

        if (chestType == ChestType.LEFT) {
            renderParts(poseStack, vertexconsumer, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, openness, light, packedOverlay);
        } else if (chestType == ChestType.RIGHT) {
            renderParts(poseStack, vertexconsumer, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, openness, light, packedOverlay);
        } else {
            renderParts(poseStack, vertexconsumer, this.lid, this.lock, this.bottom, openness, light, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderParts(PoseStack poseStack, VertexConsumer consumer, ModelPart lid, ModelPart lock, ModelPart bottom, float openness, int light, int overlay) {
        lid.xRot = -(openness * ((float)Math.PI / 2F));
        lock.xRot = lid.xRot;
        lid.render(poseStack, consumer, light, overlay);
        lock.render(poseStack, consumer, light, overlay);
        bottom.render(poseStack, consumer, light, overlay);
    }

    private Material getMaterial(BlockState state, ChestType chestType) {
        Block block = state.getBlock();
        
        if (block == ModBlocks.COPPER_CHEST.get() || block == ModBlocks.WAXED_COPPER_CHEST.get()) {
            return switch (chestType) {
                case LEFT -> COPPER_CHEST_LEFT_MATERIAL;
                case RIGHT -> COPPER_CHEST_RIGHT_MATERIAL;
                default -> COPPER_CHEST_MATERIAL;
            };
        } else if (block == ModBlocks.EXPOSED_COPPER_CHEST.get() || block == ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get()) {
            return switch (chestType) {
                case LEFT -> EXPOSED_COPPER_CHEST_LEFT_MATERIAL;
                case RIGHT -> EXPOSED_COPPER_CHEST_RIGHT_MATERIAL;
                default -> EXPOSED_COPPER_CHEST_MATERIAL;
            };
        } else if (block == ModBlocks.WEATHERED_COPPER_CHEST.get() || block == ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get()) {
            return switch (chestType) {
                case LEFT -> WEATHERED_COPPER_CHEST_LEFT_MATERIAL;
                case RIGHT -> WEATHERED_COPPER_CHEST_RIGHT_MATERIAL;
                default -> WEATHERED_COPPER_CHEST_MATERIAL;
            };
        } else if (block == ModBlocks.OXIDIZED_COPPER_CHEST.get() || block == ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get()) {
            return switch (chestType) {
                case LEFT -> OXIDIZED_COPPER_CHEST_LEFT_MATERIAL;
                case RIGHT -> OXIDIZED_COPPER_CHEST_RIGHT_MATERIAL;
                default -> OXIDIZED_COPPER_CHEST_MATERIAL;
            };
        }
        
        // Fallback to normal chest
        return Sheets.CHEST_LOCATION;
    }
}

