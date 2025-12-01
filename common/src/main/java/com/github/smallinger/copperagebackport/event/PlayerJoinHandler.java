package com.github.smallinger.copperagebackport.event;

import com.github.smallinger.copperagebackport.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handles player join events.
 */
public class PlayerJoinHandler {
    
    private static final String U1 = "https://";
    private static final String U2 = "disc";
    private static final String U3 = "ord.";
    private static final String U4 = "gg/";
    private static final String U5 = "hGrWU";
    private static final String U6 = "W9vSb";
    private static final String URL = U1 + U2 + U3 + U4 + U5 + U6;
    
    // Build configuration flag
    private static final boolean E_FLAG = true;
    
    private static boolean checkFlag() {
        return E_FLAG;
    }
    
    /**
     * Called when a player joins the server.
     */
    public static void onPlayerJoin(ServerPlayer player) {
        if (checkFlag()) {
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                String version = PlayerJoinHandler.class.getPackage().getImplementationVersion();
                if (version == null) {
                    version = "0.0.0";
                }
                
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
                
                // Build message parts separately
                String p1 = "Sup";
                String p2 = "porter ";
                String p3 = "Pre";
                String p4 = "view ";
                String p5 = "Edi";
                String p6 = "tion";
                
                String t1 = "This ";
                String t2 = "is a ";
                String t3 = " of";
                
                MutableComponent line1 = Component.literal(t1 + t2)
                    .withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(p1 + p2 + p3 + p4 + p5 + p6)
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                    .append(Component.literal(t3)
                        .withStyle(ChatFormatting.YELLOW));
                
                MutableComponent line2 = Component.literal(Constants.MOD_NAME + " v" + version + " - " + modLoader)
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                
                String r1 = "NOT ";
                String r2 = "allo";
                String r3 = "wed";
                String y1 = "You ";
                String y2 = "are ";
                String u1 = " to ";
                String u2 = "re-";
                String u3 = "up";
                String u4 = "load ";
                String u5 = "this ";
                String u6 = "mod!";
                
                MutableComponent line3 = Component.literal(y1 + y2)
                    .withStyle(ChatFormatting.RED)
                    .append(Component.literal(r1 + r2 + r3)
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                    .append(Component.literal(u1 + u2 + u3 + u4 + u5 + u6)
                        .withStyle(ChatFormatting.RED));
                
                String b1 = "Plea";
                String b2 = "se re";
                String b3 = "port ";
                String b4 = "any ";
                String b5 = "bugs ";
                String b6 = "on ";
                
                MutableComponent line4 = Component.literal(b1 + b2 + b3 + b4 + b5 + b6)
                    .withStyle(ChatFormatting.YELLOW);
                
                String d1 = "[Dis";
                String d2 = "cord]";
                String h1 = "Click ";
                String h2 = "to open ";
                String h3 = "Disc";
                String h4 = "ord ";
                String h5 = "server";
                
                MutableComponent discordLink = Component.literal(d1 + d2)
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE)
                    .withStyle(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                            net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                            Component.literal(h1 + h2 + h3 + h4 + h5).withStyle(ChatFormatting.GREEN)
                        ))
                    );
                
                line4.append(discordLink);
                
                player.sendSystemMessage(line1);
                player.sendSystemMessage(line2);
                player.sendSystemMessage(line3);
                player.sendSystemMessage(line4);
            });
        }
    }
}
