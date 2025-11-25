package com.github.smallinger.copperagebackport.config;

import com.github.smallinger.copperagebackport.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared configuration for Copper Age Backport.
 * Works on all loaders - persisted as JSON in the config directory.
 */
public final class CommonConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // Config keys
    private static final String KEY_GOLEM_PRESSES_BUTTONS = "golemPressesButtons";
    private static final String KEY_BUTTON_PRESS_CHANCE = "buttonPressChancePercent";
    
    // Default values
    private static final boolean DEFAULT_GOLEM_PRESSES_BUTTONS = true;
    private static final int DEFAULT_BUTTON_PRESS_CHANCE = 5;
    
    // Runtime values
    private static boolean golemPressesButtons = DEFAULT_GOLEM_PRESSES_BUTTONS;
    private static int buttonPressChancePercent = DEFAULT_BUTTON_PRESS_CHANCE;
    
    // Config file path (set by platform)
    private static Path configPath;

    private CommonConfig() {
    }

    /**
     * Initialize the config system with the platform-specific config directory.
     * Call this once during mod initialization.
     */
    public static void init(Path configDir) {
        configPath = configDir.resolve("copperagebackport.json");
        load();
    }

    /**
     * Should Copper Golems randomly press copper buttons?
     */
    public static boolean golemPressesButtons() {
        return golemPressesButtons;
    }

    public static void setGolemPressesButtons(boolean value) {
        golemPressesButtons = value;
    }

    /**
     * Chance (0-100%) that a golem presses a nearby button.
     */
    public static int buttonPressChancePercent() {
        return buttonPressChancePercent;
    }

    public static void setButtonPressChancePercent(int value) {
        buttonPressChancePercent = clamp(value, 0, 100);
    }

    /**
     * Load config from disk. Creates default config if not present.
     */
    public static void load() {
        if (configPath == null) {
            Constants.LOG.warn("Config path not set, using defaults");
            return;
        }

        if (!Files.exists(configPath)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json == null) {
                return;
            }

            if (json.has(KEY_GOLEM_PRESSES_BUTTONS)) {
                golemPressesButtons = json.get(KEY_GOLEM_PRESSES_BUTTONS).getAsBoolean();
            }
            if (json.has(KEY_BUTTON_PRESS_CHANCE)) {
                buttonPressChancePercent = clamp(json.get(KEY_BUTTON_PRESS_CHANCE).getAsInt(), 0, 100);
            }

            Constants.LOG.info("Loaded config: golemPressesButtons={}, buttonPressChance={}%", 
                golemPressesButtons, buttonPressChancePercent);
        } catch (IOException | IllegalStateException e) {
            Constants.LOG.error("Failed to load config, using defaults", e);
        }
    }

    /**
     * Save current config to disk.
     */
    public static void save() {
        if (configPath == null) {
            Constants.LOG.warn("Config path not set, cannot save");
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty(KEY_GOLEM_PRESSES_BUTTONS, golemPressesButtons);
        json.addProperty(KEY_BUTTON_PRESS_CHANCE, buttonPressChancePercent);

        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
