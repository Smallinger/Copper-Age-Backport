package com.github.smallinger.copperagebackport.mixin.client;

import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ensures our Fabric registry helper re-registers minecraft namespace entries
 * before Fabric's own disconnect hook unmaps the registries.
 * 
 * In 1.20.1, the method is clearLevel(Screen) instead of disconnect(Screen, boolean).
 */
@Mixin(Minecraft.class)
public abstract class MinecraftDisconnectMixin {

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void copperagebackport$restoreMinecraftEntriesBeforeDisconnect(Screen screen, CallbackInfo ci) {
        RegistryHelper.getInstance().restoreVanillaNamespaceEntries();
    }
}
