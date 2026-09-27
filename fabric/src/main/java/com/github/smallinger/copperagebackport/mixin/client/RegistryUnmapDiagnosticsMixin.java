package com.github.smallinger.copperagebackport.mixin.client;

import com.github.smallinger.copperagebackport.Constants;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Diagnostic only: before Fabric unmaps the registries on disconnect, report every registry whose
 * raw-id table contains holes (null entries). Those holes are what makes SimpleRegistry.remap throw
 * an NPE, so the log names the registry and the entries around each hole.
 */
@Mixin(RegistrySyncManager.class)
public abstract class RegistryUnmapDiagnosticsMixin {

    @Inject(method = "unmap", at = @At("HEAD"), remap = false)
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void copperagebackport$reportRegistryHoles(CallbackInfo ci) {
        try {
            for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
                if (!(registry instanceof MappedRegistry<?> mapped)) {
                    continue;
                }

                ObjectList<Holder.Reference<Object>> byId =
                    ((MappedRegistryByIdAccessor<Object>) (MappedRegistry) mapped).copperagebackport$getById();

                int holes = 0;
                StringBuilder details = new StringBuilder();
                for (int i = 0; i < byId.size(); i++) {
                    if (byId.get(i) != null) {
                        continue;
                    }
                    holes++;
                    if (holes <= 5) {
                        String before = i > 0 && byId.get(i - 1) != null ? byId.get(i - 1).key().location().toString() : "-";
                        String after = i + 1 < byId.size() && byId.get(i + 1) != null ? byId.get(i + 1).key().location().toString() : "-";
                        details.append("\n    hole at id ").append(i).append(" (before: ").append(before)
                            .append(", after: ").append(after).append(")");
                    }
                }

                if (holes > 0) {
                    boolean modded = RegistryAttributeHolder.get(registry.key()).hasAttribute(RegistryAttribute.MODDED);
                    Constants.LOG.error("[RegistryDiagnostics] {} has {} hole(s) in its raw-id table before unmap (size {}, modded={}){}",
                        registry.key().location(), holes, byId.size(), modded, details);
                }
            }
        } catch (Throwable throwable) {
            Constants.LOG.error("[RegistryDiagnostics] Failed to inspect registries", throwable);
        }
    }
}
