package com.github.smallinger.copperagebackport.client.model;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.client.animation.CopperGolemAnimation;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class CopperGolemModel extends HierarchicalModel<CopperGolemEntity> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "copper_golem"), "main");
    
    // Statue pose model layers
    public static final ModelLayerLocation STATUE_STANDING = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "copper_golem_statue_standing"), "main");
    public static final ModelLayerLocation STATUE_RUNNING = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "copper_golem_statue_running"), "main");
    public static final ModelLayerLocation STATUE_SITTING = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "copper_golem_statue_sitting"), "main");
    public static final ModelLayerLocation STATUE_STAR = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "copper_golem_statue_star"), "main");
    
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    
    // Animation definitions
    private final AnimationDefinition walkAnimation;
    private final AnimationDefinition walkWithItemAnimation;
    private final AnimationDefinition idleAnimation;
    private final AnimationDefinition interactionGetItem;
    private final AnimationDefinition interactionGetNoItem;
    private final AnimationDefinition interactionDropItem;
    private final AnimationDefinition interactionDropNoItem;
    private final AnimationDefinition pressButton;

    public CopperGolemModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        
        // Load animations
        this.walkAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK;
        this.walkWithItemAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK_ITEM;
        this.idleAnimation = CopperGolemAnimation.COPPER_GOLEM_IDLE;
        this.interactionGetItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_GET;
        this.interactionGetNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_NOITEM_NOGET;
        this.interactionDropItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_DROP;
        this.interactionDropNoItem = CopperGolemAnimation.COPPER_GOLEM_CHEST_INTERACTION_ITEM_NODROP;
        this.pressButton = CopperGolemAnimation.COPPER_GOLEM_PRESS_BUTTON;
    }

    // Entity model (living Copper Golem) - uses transformed mesh with Y translation
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        body.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.015F))
                .texOffs(56, 0).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(37, 8).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
            PartPose.offset(0.0F, -6.0F, 0.0F));
        
        body.addOrReplaceChild("right_arm",
            CubeListBuilder.create().texOffs(36, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-4.0F, -6.0F, 0.0F));
        
        body.addOrReplaceChild("left_arm",
            CubeListBuilder.create().texOffs(50, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(4.0F, -6.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("right_leg",
            CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("left_leg",
            CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    // STANDING statue pose - uses Y=-5.0F (statue coordinates)
    public static LayerDefinition createStandingStatueBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        body.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.015F))
                .texOffs(56, 0).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(37, 8).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
            PartPose.offset(0.0F, -6.0F, 0.0F));
        
        body.addOrReplaceChild("right_arm",
            CubeListBuilder.create().texOffs(36, 16).addBox(-3.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-4.0F, -6.0F, 0.0F));
        
        body.addOrReplaceChild("left_arm",
            CubeListBuilder.create().texOffs(50, 16).addBox(0.0F, -1.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(4.0F, -6.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("right_leg",
            CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        partdefinition.addOrReplaceChild("left_leg",
            CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    // RUNNING pose - legs in running position
    public static LayerDefinition createRunningPoseBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(-1.064F, -5.0F, 0.0F));
        body.addOrReplaceChild("body_r1",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.02F, -6.116F, -3.5F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.1F, 0.1F, 0.7F, 0.1204F, -0.0064F, -0.0779F));
        
        body.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -5.1F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-1.02F, -2.1F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(37, 8).addBox(-1.02F, -9.1F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0).addBox(-2.0F, -13.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
            PartPose.offset(0.7F, -5.6F, -1.8F));
        
        PartDefinition rightArm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 0.0F));
        rightArm.addOrReplaceChild("right_arm_r1",
            CubeListBuilder.create().texOffs(36, 16).addBox(-3.052F, -1.11F, -2.036F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.7F, -0.248F, -1.62F, 1.0036F, 0.0F, 0.0F));
        
        PartDefinition leftArm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -6.0F, 0.0F));
        leftArm.addOrReplaceChild("left_arm_r1",
            CubeListBuilder.create().texOffs(50, 16).addBox(0.032F, -1.1F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.732F, 0.0F, 0.0F, -0.8715F, -0.0535F, -0.0449F));
        
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-3.064F, -5.0F, 0.0F));
        rightLeg.addOrReplaceChild("right_leg_r1",
            CubeListBuilder.create().texOffs(0, 27).addBox(-1.856F, -0.1F, -1.09F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.048F, 0.0F, -0.9F, -0.8727F, 0.0F, 0.0F));
        
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.936F, -5.0F, 0.0F));
        leftLeg.addOrReplaceChild("left_leg_r1",
            CubeListBuilder.create().texOffs(16, 27).addBox(-2.088F, -0.1F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    // SITTING pose - sitting position
    public static LayerDefinition createSittingPoseBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(3, 19).addBox(-3.0F, -4.0F, -4.525F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 15).addBox(-4.0F, -3.0F, -3.525F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -3.0F, 2.325F));
        
        body.addOrReplaceChild("body_r1",
            CubeListBuilder.create().texOffs(3, 18).addBox(-4.0F, -3.0F, -2.2F, 8.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -1.0F, -4.325F, 0.0F, 0.0F, -3.1416F));
        
        body.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(37, 8).addBox(-1.0F, -7.0F, -3.3F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0).addBox(-2.0F, -11.0F, -4.3F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F))
                .texOffs(0, 0).addBox(-4.0F, -3.0F, -7.325F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-1.0F, 0.0F, -8.325F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -6.0F, -0.2F));
        
        PartDefinition rightArm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), 
            PartPose.offsetAndRotation(-4.0F, -5.6F, -1.8F, 0.4363F, 0.0F, 0.0F));
        rightArm.addOrReplaceChild("right_arm_r1",
            CubeListBuilder.create().texOffs(36, 16).addBox(-3.075F, -0.9733F, -1.9966F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0893F, 0.1198F, -1.0472F, 0.0F, 0.0F));
        
        PartDefinition leftArm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), 
            PartPose.offsetAndRotation(4.0F, -5.6F, -1.7F, 0.4363F, 0.0F, 0.0F));
        leftArm.addOrReplaceChild("left_arm_r1",
            CubeListBuilder.create().texOffs(50, 16).addBox(0.075F, -1.0443F, -1.8997F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -0.0015F, -0.0808F, -1.0472F, 0.0F, 0.0F));
        
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.1F, -2.1F, -2.075F));
        rightLeg.addOrReplaceChild("right_leg_r1",
            CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, 0.975F, 0.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.05F, -1.9F, 1.075F, -1.5708F, 0.0F, 0.0F));
        
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0F, -2.075F));
        leftLeg.addOrReplaceChild("left_leg_r1",
            CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, 0.975F, 0.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.05F, -2.0F, 1.075F, -1.5708F, 0.0F, 0.0F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    // STAR pose - star pose (arms out)
    public static LayerDefinition createStarPoseBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition body = partdefinition.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, -5.0F, 0.0F));
        
        body.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(37, 8).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.015F))
                .texOffs(37, 0).addBox(-2.0F, -13.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.015F)),
            PartPose.offset(0.0F, -6.0F, 0.0F));
        
        PartDefinition rightArm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-4.0F, -6.0F, 0.0F));
        rightArm.addOrReplaceChild("right_arm_r1",
            CubeListBuilder.create().texOffs(36, 16).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.9199F));
        
        PartDefinition leftArm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -6.0F, 0.0F));
        leftArm.addOrReplaceChild("left_arm_r1",
            CubeListBuilder.create().texOffs(50, 16).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.9199F));
        
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-3.0F, -5.0F, 0.0F));
        rightLeg.addOrReplaceChild("right_leg_r1",
            CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.35F, 2.0F, 0.01F, 0.0F, 0.0F, 0.2618F));
        
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.0F, -5.0F, 0.0F));
        leftLeg.addOrReplaceChild("left_leg_r1",
            CubeListBuilder.create().texOffs(16, 27).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.65F, 2.0F, 0.0F, 0.0F, 0.0F, -0.2618F));
        
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(CopperGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        
        // Head rotation
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // Get current state
        CopperGolemState state = entity.getState();
        
        // Apply walking animation based on whether entity has items
        // BUT only if NOT pressing button (no walk/run animation during button press)
        boolean hasItems = !entity.getMainHandItem().isEmpty();
        if (limbSwingAmount > 0.0F && state != CopperGolemState.PRESSING_BUTTON) {
            if (hasItems) {
                this.animateWalk(this.walkWithItemAnimation, limbSwing, limbSwingAmount, 2.0F, 2.5F);
                this.poseHeldItemArmsIfStill();
            } else {
                this.animateWalk(this.walkAnimation, limbSwing, limbSwingAmount, 2.0F, 2.5F);
            }
        }

        // Apply idle animation (only when not interacting)
        if (state == CopperGolemState.IDLE) {
            this.animate(entity.idleAnimationState, this.idleAnimation, ageInTicks);
        }
        
        // Apply interaction animations based on state
        switch (state) {
            case GETTING_ITEM -> this.animate(entity.interactionGetItemAnimationState, this.interactionGetItem, ageInTicks);
            case GETTING_NO_ITEM -> this.animate(entity.interactionGetNoItemAnimationState, this.interactionGetNoItem, ageInTicks);
            case DROPPING_ITEM -> this.animate(entity.interactionDropItemAnimationState, this.interactionDropItem, ageInTicks);
            case DROPPING_NO_ITEM -> this.animate(entity.interactionDropNoItemAnimationState, this.interactionDropNoItem, ageInTicks);
            case PRESSING_BUTTON -> this.animate(entity.pressingButtonAnimationState, this.pressButton, ageInTicks);
            default -> {}
        }
    }
    
    @Override
    public ModelPart root() {
        return this.root;
    }
    
    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        ModelPart armPart = arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
        armPart.translateAndRotate(poseStack);
        
        // Position item differently based on whether golem is idle or interacting
        CopperGolemState state = CopperGolemState.IDLE; // Default to idle positioning
        // Note: In 1.21.1 we don't have access to render state here, so we use default positioning
        // The original 1.21.10 checks the state, but for 1.21.1 we use the idle position
        
        if (state == CopperGolemState.IDLE) {
            // Item held normally (horizontal)
            poseStack.mulPose(Axis.YP.rotationDegrees(arm == HumanoidArm.RIGHT ? -90.0F : 90.0F));
            poseStack.translate(0.0F, 0.0F, 0.125F);
        } else {
            // Item during interaction (smaller and repositioned)
            poseStack.scale(0.55F, 0.55F, 0.55F);
            poseStack.translate(-0.125F, 0.3125F, -0.1875F);
        }
    }
    
    /**
     * Applies the transformation needed to render an item on the Copper Golem's antenna.
     * This is used by CopperGolemAntennaLayer to position flowers (like Poppy) on top of the head.
     * Adjusted from vanilla 1.21.10 to fit our model's coordinate system.
     */
    public void applyBlockOnAntennaTransform(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
        // Adjusted Y translation to position flower on top of antenna
        // Original vanilla 1.21.10 value was -2.25, but our model uses different coordinates
        poseStack.translate(0.0, -1.05, 0.0);
    }
    
    private void poseHeldItemArmsIfStill() {
        this.rightArm.xRot = Math.min(this.rightArm.xRot, -0.87266463F);
        this.leftArm.xRot = Math.min(this.leftArm.xRot, -0.87266463F);
        this.rightArm.yRot = Math.min(this.rightArm.yRot, -0.1134464F);
        this.leftArm.yRot = Math.max(this.leftArm.yRot, 0.1134464F);
        this.rightArm.zRot = Math.min(this.rightArm.zRot, -0.064577185F);
        this.leftArm.zRot = Math.max(this.leftArm.zRot, 0.064577185F);
    }
}

