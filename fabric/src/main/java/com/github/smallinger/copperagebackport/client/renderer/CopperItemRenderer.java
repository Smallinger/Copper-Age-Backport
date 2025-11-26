package com.github.smallinger.copperagebackport.client.renderer;

import com.github.smallinger.copperagebackport.block.CopperGolemStatueBlock;
import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.copperagebackport.platform.Services;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Fabric custom item renderer for Copper Chest and Copper Golem Statue items that mirrors the
 * Forge implementation by delegating to the block entity renderer dispatcher.
 */
public class CopperItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block block = blockItem.getBlock();
        if (isChestBlock(block)) {
            // If FastChest is active, use static block model rendering
            if (Services.PLATFORM.isFastChestSimplifiedEnabled()) {
                renderChestItemAsBlock(block, poseStack, bufferSource, packedLight, packedOverlay);
            } else {
                renderChestItem(stack, block, poseStack, bufferSource, packedLight, packedOverlay);
            }
        } else if (block instanceof CopperGolemStatueBlock statueBlock) {
            renderStatueItem(stack, statueBlock, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private boolean isChestBlock(Block block) {
        return block == ModBlocks.COPPER_CHEST.get() ||
            block == ModBlocks.EXPOSED_COPPER_CHEST.get() ||
            block == ModBlocks.WEATHERED_COPPER_CHEST.get() ||
            block == ModBlocks.OXIDIZED_COPPER_CHEST.get() ||
            block == ModBlocks.WAXED_COPPER_CHEST.get() ||
            block == ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get() ||
            block == ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get() ||
            block == ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get();
    }

    private void renderChestItem(ItemStack stack, Block block, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = getBlockStateFromStack(stack, block);
        CopperChestBlockEntity blockEntity = new CopperChestBlockEntity(BlockPos.ZERO, state);
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(
            blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }
    
    /**
     * Render chest item as a static block model (for FastChest compatibility).
     * Uses the block's baked model instead of the BlockEntity renderer.
     * Rotates the model 180 degrees so the front faces the player.
     */
    private void renderChestItemAsBlock(Block block, PoseStack poseStack,
                                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = block.defaultBlockState();
        
        poseStack.pushPose();
        // Rotate 180 degrees around Y axis to face the player
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(-0.5, 0, -0.5);
        
        Minecraft.getInstance().getBlockRenderer()
            .renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderStatueItem(ItemStack stack, CopperGolemStatueBlock statueBlock, PoseStack poseStack,
                                  MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = getBlockStateFromStack(stack, statueBlock);
        if (state.hasProperty(CopperGolemStatueBlock.FACING)) {
            state = state.setValue(CopperGolemStatueBlock.FACING, Direction.SOUTH);
        }
        CopperGolemStatueBlockEntity blockEntity = new CopperGolemStatueBlockEntity(BlockPos.ZERO, state);
        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(
            blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private BlockState getBlockStateFromStack(ItemStack stack, Block block) {
        BlockState state = block.defaultBlockState();
        CompoundTag stateTag = stack.getTagElement(BlockItem.BLOCK_STATE_TAG);
        if (stateTag == null) {
            return state;
        }

        StateDefinition<Block, BlockState> definition = block.getStateDefinition();
        for (String key : stateTag.getAllKeys()) {
            Property<?> property = definition.getProperty(key);
            if (property != null) {
                state = applyProperty(state, property, stateTag.getString(key));
            }
        }
        return state;
    }

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
                                                                      String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }
}

