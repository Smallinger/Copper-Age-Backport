package com.github.smallinger.copperagebackport.client.gui.options;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builder-based Option implementation.
 */
public class OptionImpl<T> implements Option<T> {
    
    private final Component name;
    private final Component tooltip;
    private final Control<T> control;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final T defaultValue;
    private final BooleanSupplier available;
    
    private T value;
    private T modifiedValue;

    private OptionImpl(Builder<T> builder) {
        this.name = builder.name;
        this.tooltip = builder.tooltip;
        this.getter = builder.getter;
        this.setter = builder.setter;
        this.defaultValue = builder.defaultValue;
        this.available = builder.available;
        this.control = builder.controlProvider.apply(this);
        
        this.value = getter.get();
        this.modifiedValue = this.value;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    @Nullable
    public Component getTooltip() {
        return tooltip;
    }

    @Override
    public Control<T> getControl() {
        return control;
    }

    @Override
    public T getValue() {
        return modifiedValue;
    }

    @Override
    public void setValue(T value) {
        this.modifiedValue = value;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void reset() {
        this.value = getter.get();
        this.modifiedValue = this.value;
    }

    @Override
    public boolean hasChanged() {
        return !this.value.equals(this.modifiedValue);
    }

    @Override
    public boolean isAvailable() {
        return available.getAsBoolean();
    }

    @Override
    public void applyChanges() {
        setter.accept(modifiedValue);
        this.value = modifiedValue;
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Component name;
        private Component tooltip;
        private java.util.function.Function<Option<T>, Control<T>> controlProvider;
        private Supplier<T> getter;
        private Consumer<T> setter;
        private T defaultValue;
        private BooleanSupplier available = () -> true;

        public Builder<T> name(Component name) {
            this.name = name;
            return this;
        }

        public Builder<T> name(String key) {
            this.name = Component.translatable(key);
            return this;
        }

        public Builder<T> tooltip(Component tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder<T> tooltip(String key) {
            this.tooltip = Component.translatable(key);
            return this;
        }

        public Builder<T> control(java.util.function.Function<Option<T>, Control<T>> provider) {
            this.controlProvider = provider;
            return this;
        }

        public Builder<T> binding(Supplier<T> getter, Consumer<T> setter) {
            this.getter = getter;
            this.setter = setter;
            return this;
        }

        public Builder<T> defaultValue(T value) {
            this.defaultValue = value;
            return this;
        }

        public Builder<T> available(BooleanSupplier available) {
            this.available = available;
            return this;
        }

        public Option<T> build() {
            if (name == null) throw new IllegalStateException("Option name is required");
            if (getter == null || setter == null) throw new IllegalStateException("Binding is required");
            if (controlProvider == null) throw new IllegalStateException("Control is required");
            return new OptionImpl<>(this);
        }
    }
}
