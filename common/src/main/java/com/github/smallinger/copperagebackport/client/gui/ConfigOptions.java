package com.github.smallinger.copperagebackport.client.gui;

import com.github.smallinger.copperagebackport.client.gui.options.*;
import com.github.smallinger.copperagebackport.client.gui.options.control.SliderControl;
import com.github.smallinger.copperagebackport.client.gui.options.control.TickBoxControl;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines all config options and organizes them into pages.
 */
public class ConfigOptions {

    public static List<OptionPage> createPages() {
        List<OptionPage> pages = new ArrayList<>();
        
        pages.add(createGolemPage());
        // Add more pages here as needed
        
        return pages;
    }

    private static OptionPage createGolemPage() {
        // Golem behavior option
        Option<Boolean> golemPressesButtons = OptionImpl.<Boolean>builder(Boolean.class)
            .name("config.copperagebackport.golem_presses_buttons")
            .tooltip("config.copperagebackport.golem_presses_buttons.tooltip")
            .control(TickBoxControl::new)
            .binding(
                CommonConfig::golemPressesButtons,
                CommonConfig::setGolemPressesButtons
            )
            .defaultValue(true)
            .build();

        // Button press chance option
        Option<Integer> buttonPressChance = OptionImpl.<Integer>builder(Integer.class)
            .name("config.copperagebackport.button_press_chance")
            .tooltip("config.copperagebackport.button_press_chance.tooltip")
            .control(opt -> new SliderControl(opt, 0, 100, 5, "%"))
            .binding(
                CommonConfig::buttonPressChancePercent,
                CommonConfig::setButtonPressChancePercent
            )
            .defaultValue(10)
            .available(CommonConfig::golemPressesButtons)
            .build();

        // Create groups
        OptionGroup behaviorGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.behavior")
            .add(golemPressesButtons)
            .add(buttonPressChance)
            .build();

        return OptionPage.builder()
            .name("config.copperagebackport.page.golem")
            .add(behaviorGroup)
            .build();
    }
}
