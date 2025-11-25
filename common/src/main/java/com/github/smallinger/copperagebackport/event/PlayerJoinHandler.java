package com.github.smallinger.copperagebackport.event;

import com.github.smallinger.copperagebackport.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handles player join events to display preview build messages.
 */
public class PlayerJoinHandler {
    
    private static final String ISSUE_URL = "https://discord.gg/hGrWUW9vSb";
    
    /**
     * Checks if this is a preview build by examining the version string.
     * Preview builds contain "preview" in their version (e.g., "0.1.0-0.1preview")
     */
    private static boolean isPreviewBuild() {
        // First check system property (for dev environment)
        String sysProp = System.getProperty("copperagebackport.preview", "false");
        if (Boolean.parseBoolean(sysProp)) {
            return true;
        }
        
        // Then check version string from package (for built JARs)
        String version = PlayerJoinHandler.class.getPackage().getImplementationVersion();
        if (version != null && version.toLowerCase().contains("preview")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Called when a player joins the server.
     * Sends a preview build message if this is a preview build.
     */
    public static void onPlayerJoin(ServerPlayer player) {
        if (isPreviewBuild()) {
            // Schedule message to be sent after a short delay (1 second = 20 ticks)
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Get version from Constants or package
                String version = PlayerJoinHandler.class.getPackage().getImplementationVersion();
                if (version == null) {
                    version = "0.0.0"; // Fallback for dev environment
                }
                
                // Detect mod loader
                String modLoader;
                try {
                    Class.forName("net.fabricmc.loader.api.FabricLoader");
                    modLoader = "Fabric";
                } catch (ClassNotFoundException e) {
                    try {
                        Class.forName("net.neoforged.fml.common.Mod");
                        modLoader = "NeoForge";
                    } catch (ClassNotFoundException ex) {
                        try {
                            Class.forName("net.minecraftforge.fml.common.Mod");
                            modLoader = "Forge";
                        } catch (ClassNotFoundException ex2) {
                            modLoader = "Unknown";
                        }
                    }
                }
                
                // Line 1: Supporter Preview Edition
                MutableComponent line1 = Component.literal("This is a ")
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("Supporter Preview Edition")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                    .append(Component.literal(" of")
                        .withStyle(ChatFormatting.YELLOW));
                
                // Line 2: Mod name, version and loader
                MutableComponent line2 = Component.literal(Constants.MOD_NAME + " v" + version + " - " + modLoader)
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                
                // Line 3: Upload restriction
                MutableComponent line3 = Component.literal("You are ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.literal("NOT allowed")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                    .append(Component.literal(" to re-upload this mod!")
                        .withStyle(ChatFormatting.RED));
                
                // Line 4: Bug report request with link
                MutableComponent line4 = Component.literal("Please report any bugs on ")
                    .withStyle(ChatFormatting.YELLOW);
                
                MutableComponent githubLink = Component.literal("[Discord]")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE)
                    .withStyle(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ISSUE_URL))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                            net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                            Component.literal("Click to open Discord server").withStyle(ChatFormatting.GREEN)
                        ))
                    );
                
                line4.append(githubLink);
                
                // Send all four messages
                player.sendSystemMessage(line1);
                player.sendSystemMessage(line2);
                player.sendSystemMessage(line3);
                player.sendSystemMessage(line4);
            });
        }
    }
}
