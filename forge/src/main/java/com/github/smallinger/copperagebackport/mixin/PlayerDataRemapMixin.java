package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to remap legacy copperagebackport: IDs to minecraft: IDs when loading player data.
 * This ensures items in player inventories are properly migrated.
 * We inject into Player.load() to modify the NBT before items are deserialized.
 */
@Mixin(Player.class)
public class PlayerDataRemapMixin {

    @Unique
    private static final String OLD_NAMESPACE = Constants.MOD_ID + ":";
    
    @Unique
    private static final String NEW_NAMESPACE = "minecraft:";

    /**
     * Intercept player NBT data before items are loaded.
     */
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void copperagebackport$remapPlayerData(CompoundTag tag, CallbackInfo ci) {
        if (tag == null) return;
        
        // Remap inventory
        if (tag.contains("Inventory", Tag.TAG_LIST)) {
            ListTag inventory = tag.getList("Inventory", Tag.TAG_COMPOUND);
            copperagebackport$remapItemList(inventory);
        }
        
        // Remap ender chest
        if (tag.contains("EnderItems", Tag.TAG_LIST)) {
            ListTag enderItems = tag.getList("EnderItems", Tag.TAG_COMPOUND);
            copperagebackport$remapItemList(enderItems);
        }
    }
    
    /**
     * Remap items in a list
     */
    @Unique
    private static void copperagebackport$remapItemList(ListTag items) {
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i);
            if (item.contains("id", Tag.TAG_STRING)) {
                String id = item.getString("id");
                String remapped = copperagebackport$remapId(id, "PlayerItem");
                if (remapped != null) {
                    item.putString("id", remapped);
                }
            }
        }
    }

    /**
     * Check if an ID should be remapped and return the new ID, or null if no remap needed.
     */
    @Unique
    private static String copperagebackport$remapId(String id, String context) {
        if (id == null || !id.startsWith(OLD_NAMESPACE)) {
            return null;
        }
        
        String path = id.substring(OLD_NAMESPACE.length());
        
        // Only remap if it's a vanilla backport ID
        if (RegistryHelper.VANILLA_BACKPORT_IDS.contains(path)) {
            String newId = NEW_NAMESPACE + path;
            Constants.LOG.info("[{}] Migrate legacy {} -> {}", context, id, newId);
            return newId;
        }
        
        return null;
    }
}
