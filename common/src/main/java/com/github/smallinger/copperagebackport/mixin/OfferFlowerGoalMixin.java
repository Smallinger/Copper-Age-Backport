package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixin to add the 1.21.10 Iron Golem flower gift feature.
 * In vanilla 1.21.10, Iron Golems can now give flowers to Copper Golems,
 * not just look at Villagers. When the offer ends and a Copper Golem is nearby,
 * the Iron Golem gives it a poppy for its antenna slot.
 */
@Mixin(OfferFlowerGoal.class)
public abstract class OfferFlowerGoalMixin extends Goal {

    @Shadow @Final private IronGolem golem;
    @Shadow private Villager villager;
    @Shadow private int tick;

    @Unique
    private static final TargetingConditions copperagebackport$OFFER_TARGET_CONTEXT = 
        TargetingConditions.forNonCombat().range(6.0D);

    /**
     * The Copper Golem we're offering the flower to (null if offering to a Villager).
     */
    @Unique
    private CopperGolemEntity copperagebackport$copperGolemTarget = null;

    /**
     * Inject into canUse to also check for nearby Copper Golems as valid targets.
     * If no villager is found, try to find a Copper Golem instead.
     */
    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void copperagebackport$canUseWithCopperGolem(CallbackInfoReturnable<Boolean> cir) {
        // If vanilla already found a villager, use that
        if (cir.getReturnValue()) {
            this.copperagebackport$copperGolemTarget = null;
            return;
        }

        // Otherwise, try to find a Copper Golem
        AABB searchBox = this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
        List<CopperGolemEntity> copperGolems = this.golem.level().getEntitiesOfClass(
            CopperGolemEntity.class, 
            searchBox,
            entity -> copperagebackport$OFFER_TARGET_CONTEXT.test(this.golem, entity)
        );

        if (!copperGolems.isEmpty()) {
            // Find the nearest one
            CopperGolemEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;
            for (CopperGolemEntity cg : copperGolems) {
                double dist = this.golem.distanceToSqr(cg);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = cg;
                }
            }
            
            if (nearest != null) {
                this.copperagebackport$copperGolemTarget = nearest;
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Inject into start() to set a shorter timer for Copper Golems.
     * Vanilla uses 400 ticks (20 seconds) which is too long - we use 100 ticks (5 seconds).
     */
    @Inject(method = "start", at = @At("TAIL"))
    private void copperagebackport$startWithCopperGolem(CallbackInfo ci) {
        if (this.copperagebackport$copperGolemTarget != null) {
            this.tick = 100; // 5 seconds instead of 20
        }
    }

    /**
     * Inject into tick to look at Copper Golem if that's our target.
     * We need to handle this completely ourselves since villager is null.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void copperagebackport$tickWithCopperGolem(CallbackInfo ci) {
        // If our target is a CopperGolem (not a villager), handle it ourselves
        if (this.copperagebackport$copperGolemTarget != null) {
            this.golem.getLookControl().setLookAt(this.copperagebackport$copperGolemTarget, 30.0F, 30.0F);
            --this.tick;
            ci.cancel();
        }
    }

    /**
     * Inject at the HEAD of stop() to give the flower to the Copper Golem before cleanup.
     * Only give the flower if the goal completed normally (tick <= 0), not if it was
     * interrupted by higher priority goals like panic or attack.
     */
    @Inject(method = "stop", at = @At("HEAD"))
    private void copperagebackport$giveFlowerToCopperGolem(CallbackInfo ci) {
        // Only give flower if goal completed normally (not interrupted by panic/attack)
        if (this.copperagebackport$copperGolemTarget != null && this.tick <= 0) {
            CopperGolemEntity copperGolem = this.copperagebackport$copperGolemTarget;
            
            // Check if the Copper Golem is still nearby and antenna slot is empty
            AABB searchBox = this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
            boolean inRange = searchBox.intersects(copperGolem.getBoundingBox());
            boolean slotEmpty = copperGolem.getItemBySlot(CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA).isEmpty();
            
            if (inRange && slotEmpty) {
                // Give the poppy to the Copper Golem
                copperGolem.setItemSlot(
                    CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA, 
                    Items.POPPY.getDefaultInstance()
                );
                // Mark as guaranteed drop (like vanilla 1.21.10)
                copperGolem.setGuaranteedDrop(CopperGolemEntity.EQUIPMENT_SLOT_ANTENNA);
            }
        }
        
        // Always clear our target
        this.copperagebackport$copperGolemTarget = null;
    }
}
