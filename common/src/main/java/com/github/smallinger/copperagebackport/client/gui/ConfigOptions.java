package com.github.smallinger.copperagebackport.client.gui;

import com.github.smallinger.copperagebackport.client.gui.options.*;
import com.github.smallinger.copperagebackport.client.gui.options.control.IntegerFieldControl;
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

        // Weathering time from option
        Option<Integer> weatheringTickFrom = OptionImpl.<Integer>builder(Integer.class)
            .name("config.copperagebackport.weathering_tick_from")
            .tooltip("config.copperagebackport.weathering_tick_from.tooltip")
            .control(opt -> new IntegerFieldControl(opt, 0, 10000000, IntegerFieldControl.ValueFormatter.minecraftDays()))
            .binding(
                CommonConfig::weatheringTickFrom,
                CommonConfig::setWeatheringTickFrom
            )
            .defaultValue(504000)
            .build();

        // Weathering time to option (must be >= from) - reads from weatheringTickFrom's buffer value
        Option<Integer> weatheringTickTo = OptionImpl.<Integer>builder(Integer.class)
            .name("config.copperagebackport.weathering_tick_to")
            .tooltip("config.copperagebackport.weathering_tick_to.tooltip")
            .control(opt -> new IntegerFieldControl(opt, weatheringTickFrom::getValue, 10000000, IntegerFieldControl.ValueFormatter.minecraftDays()))
            .binding(
                CommonConfig::weatheringTickTo,
                CommonConfig::setWeatheringTickTo
            )
            .defaultValue(552000)
            .build();

        // Create groups
        OptionGroup behaviorGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.behavior")
            .add(golemPressesButtons)
            .add(buttonPressChance)
            .build();

        OptionGroup weatheringGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.weathering")
            .add(weatheringTickFrom)
            .add(weatheringTickTo)
            .build();

        return OptionPage.builder()
            .name("config.copperagebackport.page.golem")
            .add(behaviorGroup)
            .add(weatheringGroup)
            .build();
    }
}
