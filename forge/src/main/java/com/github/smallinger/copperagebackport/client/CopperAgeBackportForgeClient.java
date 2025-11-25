package com.github.smallinger.copperagebackport.client;

import com.github.smallinger.copperagebackport.client.gui.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Helper class for registering client-side config screen.
 * This class is only loaded on the client.
 */
public class CopperAgeBackportForgeClient {
    
    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                (mc, parent) -> ConfigScreen.create(parent)
            )
        );
    }
}
