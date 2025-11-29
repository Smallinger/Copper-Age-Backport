package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

/**
 * Mixin to remap legacy copperagebackport: IDs to minecraft: IDs when loading entities.
 * This targets EntityType.loadEntityRecursive which is called for every entity.
 */
@Mixin(EntityType.class)
public class EntityRemapMixin {

    @Unique
    private static final String OLD_NAMESPACE = Constants.MOD_ID + ":";
    
    @Unique
    private static final String NEW_NAMESPACE = "minecraft:";

    /**
     * Intercept the CompoundTag before the entity is loaded.
     * This handles entity IDs and any items they carry.
     */
    @Inject(
        method = "loadEntityRecursive(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;Ljava/util/function/Function;)Lnet/minecraft/world/entity/Entity;",
        at = @At("HEAD")
    )
    private static void copperagebackport$remapEntityCompound(CompoundTag compound, Level level, Function<Entity, Entity> function, CallbackInfoReturnable<Entity> cir) {
        if (compound == null) return;
        
        // Remap entity type ID
        if (compound.contains("id", Tag.TAG_STRING)) {
            String id = compound.getString("id");
            String remapped = copperagebackport$remapId(id, "Entity");
            if (remapped != null) {
                compound.putString("id", remapped);
            }
        }
        
        // Remap items held by the entity (like item frames)
        if (compound.contains("Item", Tag.TAG_COMPOUND)) {
            CompoundTag item = compound.getCompound("Item");
            copperagebackport$remapItemTag(item, "ItemFrame");
        }
        
        // Remap armor/hand items on entities
        copperagebackport$remapEquipment(compound, "ArmorItems");
        copperagebackport$remapEquipment(compound, "HandItems");
    }
    
    /**
     * Remap a single item tag
     */
    @Unique
    private static void copperagebackport$remapItemTag(CompoundTag item, String context) {
        if (item == null) return;
        
        if (item.contains("id", Tag.TAG_STRING)) {
            String id = item.getString("id");
            String remapped = copperagebackport$remapId(id, context);
            if (remapped != null) {
                item.putString("id", remapped);
            }
        }
    }
    
    /**
     * Remap equipment lists (ArmorItems, HandItems)
     */
    @Unique
    private static void copperagebackport$remapEquipment(CompoundTag entity, String tagName) {
        if (!entity.contains(tagName, Tag.TAG_LIST)) return;
        
        ListTag items = entity.getList(tagName, Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i);
            copperagebackport$remapItemTag(item, tagName);
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
