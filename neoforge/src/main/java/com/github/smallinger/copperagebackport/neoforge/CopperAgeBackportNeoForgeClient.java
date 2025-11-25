package com.github.smallinger.copperagebackport.neoforge;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.client.gui.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only setup for NeoForge.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CopperAgeBackportNeoForgeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModContainer modContainer = ModList.get().getModContainerById(Constants.MOD_ID).orElseThrow();
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, 
                (mc, parent) -> ConfigScreen.create(parent));
        });
    }
}
