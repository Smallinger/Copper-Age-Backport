package com.github.smallinger.copperagebackport.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MappedRegistry.class)
public interface MappedRegistryByIdAccessor<T> {
    @Accessor("byId")
    ObjectList<Holder.Reference<T>> copperagebackport$getById();
}
