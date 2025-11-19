package com.github.smallinger.coppergolemlegacy;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuration for Copper Golem Legacy mod
 */
public class CopperGolemLegacyConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue GOLEM_PRESSES_BUTTONS;

    static {
        BUILDER.push("coppergolemai");

        GOLEM_PRESSES_BUTTONS = BUILDER
            .comment("Enable/Disable Copper Golem randomly pressing copper buttons")
            .define("golemPressesButtons", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    public static Screen createConfigScreen(Screen parent) {
        // For now, return null - you can implement a proper config screen later
        // or use a library like Configured/Cloth Config
        return null;
    }
}
