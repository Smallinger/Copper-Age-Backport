package com.github.smallinger.copperagebackport.client.gui;

import com.github.smallinger.copperagebackport.client.gui.options.*;
import com.github.smallinger.copperagebackport.client.gui.options.control.IntegerFieldControl;
import com.github.smallinger.copperagebackport.client.gui.options.control.SliderControl;
import com.github.smallinger.copperagebackport.client.gui.options.control.TextBoxControl;
import com.github.smallinger.copperagebackport.client.gui.options.control.TickBoxControl;
import com.github.smallinger.copperagebackport.config.CommonConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines all config options and organizes them into pages.
 */
public class ConfigOptions {

    public static List<OptionPage> createPages() {
        List<OptionPage> pages = new ArrayList<>();
        
        pages.add(createGolemPage());
        pages.add(createCompatibilityPage());
        // Add more pages here as needed
        
        return pages;
    }

    private static OptionPage createGolemPage() {
        // Golem build spawning option
        Option<Boolean> golemBuildSpawning = OptionImpl.<Boolean>builder(Boolean.class)
            .name("config.copperagebackport.golem_build_spawning")
            .tooltip("config.copperagebackport.golem_build_spawning.tooltip")
            .control(TickBoxControl::new)
            .binding(
                CommonConfig::golemBuildSpawning,
                CommonConfig::setGolemBuildSpawning
            )
            .defaultValue(true)
            .build();

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

        // Golem transport stack size option
        Option<Integer> golemTransportStackSize = OptionImpl.<Integer>builder(Integer.class)
            .name("config.copperagebackport.golem_transport_stack_size")
            .tooltip("config.copperagebackport.golem_transport_stack_size.tooltip")
            .control(opt -> new SliderControl(opt, 1, 64, 1, ""))
            .binding(
                CommonConfig::golemTransportStackSize,
                CommonConfig::setGolemTransportStackSize
            )
            .defaultValue(16)
            .build();

        // Weathering time from option
        Option<Integer> weatheringTickFrom = OptionImpl.<Integer>builder(Integer.class)
            .name("config.copperagebackport.weathering_tick_from")
            .tooltip("config.copperagebackport.weathering_tick_from.tooltip")
            .control(opt -> new IntegerFieldControl(opt, 0, 10000000, IntegerFieldControl.ValueFormatter.realMinutes()))
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
            .control(opt -> new IntegerFieldControl(opt, weatheringTickFrom::getValue, 10000000, IntegerFieldControl.ValueFormatter.realMinutes()))
            .binding(
                CommonConfig::weatheringTickTo,
                CommonConfig::setWeatheringTickTo
            )
            .defaultValue(552000)
            .build();

        // Create groups
        OptionGroup transportGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.transport")
            .add(golemTransportStackSize)
            .build();

        OptionGroup behaviorGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.behavior")
            .add(golemBuildSpawning)
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
            .add(transportGroup)
            .add(behaviorGroup)
            .add(weatheringGroup)
            .build();
    }

    private static OptionPage createCompatibilityPage() {
        // Lightning Rod oxidation info - always enabled, just informational
        Option<String> lightningRodInfo = OptionImpl.<String>builder(String.class)
            .name("config.copperagebackport.lightning_rod_oxidation")
            .control(TextBoxControl::new)
            .binding(
                () -> "config.copperagebackport.lightning_rod_oxidation.text",
                (v) -> {} // Read-only
            )
            .defaultValue("config.copperagebackport.lightning_rod_oxidation.text")
            .build();

        // Create group
        OptionGroup lightningRodGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.lightning_rod")
            .add(lightningRodInfo)
            .build();

        return OptionPage.builder()
            .name("config.copperagebackport.page.compatibility")
            .add(lightningRodGroup)
            .build();
    }
}
