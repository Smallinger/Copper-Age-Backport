package com.github.smallinger.copperagebackport.client.gui.options;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of options that are displayed together.
 */
public class OptionGroup {
    
    private final Component name;
    private final List<Option<?>> options;
    private final boolean collapsed;

    private OptionGroup(Component name, List<Option<?>> options, boolean collapsed) {
        this.name = name;
        this.options = options;
        this.collapsed = collapsed;
    }

    public Component getName() {
        return name;
    }

    public List<Option<?>> getOptions() {
        return options;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Component name;
        private final List<Option<?>> options = new ArrayList<>();
        private boolean collapsed = false;

        public Builder name(Component name) {
            this.name = name;
            return this;
        }

        public Builder name(String key) {
            this.name = Component.translatable(key);
            return this;
        }

        public Builder add(Option<?> option) {
            this.options.add(option);
            return this;
        }

        public Builder collapsed(boolean collapsed) {
            this.collapsed = collapsed;
            return this;
        }

        public OptionGroup build() {
            return new OptionGroup(name, List.copyOf(options), collapsed);
        }
    }
}
