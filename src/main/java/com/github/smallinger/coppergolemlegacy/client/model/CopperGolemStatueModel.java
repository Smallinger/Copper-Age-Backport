package com.github.smallinger.coppergolemlegacy.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

/**
 * Statue model that reuses the Copper Golem's model but is static (no animation)
 * and rotates based on the Direction it's facing.
 */
public class CopperGolemStatueModel extends Model {
    private final ModelPart root;

    public CopperGolemStatueModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Setup the model rotation based on the facing direction
     */
    public void setupAnim(Direction facing) {
        this.root.y = 0.0F;
        this.root.yRot = facing.getOpposite().toYRot() * ((float) Math.PI / 180.0F);
        this.root.zRot = (float) Math.PI; // Flip upside down for statue
    }
}

