package com.github.smallinger.copperagebackport.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;

public interface ContainerUser {
    boolean hasContainerOpen(ContainerOpenersCounter openCounter, BlockPos pos);

    double getContainerInteractionRange();

    default LivingEntity getLivingEntity() {
        if (this instanceof LivingEntity) {
            return (LivingEntity)this;
        } else {
            throw new IllegalStateException("A container user must be a LivingEntity");
        }
    }
}

