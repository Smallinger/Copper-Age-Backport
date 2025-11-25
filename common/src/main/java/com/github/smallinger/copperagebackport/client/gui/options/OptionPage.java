package com.github.smallinger.copperagebackport.client.gui.options;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A page of option groups, displayed in the sidebar.
 */
public class OptionPage {
    
    private final Component name;
    private final List<OptionGroup> groups;

    private OptionPage(Component name, List<OptionGroup> groups) {
        this.name = name;
        this.groups = groups;
    }

    public Component getName() {
        return name;
    }

    public List<OptionGroup> getGroups() {
        return groups;
    }

    /**
     * Gets all options across all groups.
     */
    public List<Option<?>> getAllOptions() {
        List<Option<?>> allOptions = new ArrayList<>();
        for (OptionGroup group : groups) {
            allOptions.addAll(group.getOptions());
        }
        return allOptions;
    }

    /**
     * Checks if any option on this page has changed.
     */
    public boolean hasChanges() {
        for (Option<?> option : getAllOptions()) {
            if (option.hasChanged()) {
                return true;
            }
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Component name;
        private final List<OptionGroup> groups = new ArrayList<>();

        public Builder name(Component name) {
            this.name = name;
            return this;
        }

        public Builder name(String key) {
            this.name = Component.translatable(key);
            return this;
        }

        public Builder add(OptionGroup group) {
            this.groups.add(group);
            return this;
        }

        public OptionPage build() {
            if (name == null) throw new IllegalStateException("Page name is required");
            return new OptionPage(name, List.copyOf(groups));
        }
    }
}
