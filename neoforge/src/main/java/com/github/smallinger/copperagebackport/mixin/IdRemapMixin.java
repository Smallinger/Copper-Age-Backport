package com.github.smallinger.copperagebackport.mixin;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.registry.RegistryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin to remap legacy copperagebackport: IDs to minecraft: IDs when loading chunks.
 * This ensures proper migration of block/entity data from old worlds.
 */
@Mixin(ChunkSerializer.class)
public class IdRemapMixin {

    @Unique
    private static final String OLD_NAMESPACE = Constants.MOD_ID + ":";
    
    @Unique
    private static final String NEW_NAMESPACE = "minecraft:";

    /**
     * Intercept chunk NBT data and remap legacy IDs before the chunk is deserialized.
     */
    @ModifyVariable(method = "read", at = @At("HEAD"), argsOnly = true)
    private static CompoundTag copperagebackport$remapChunkData(CompoundTag tag) {
        if (tag == null) return null;
        
        boolean modified = false;
        
        // Remap block palette in sections
        if (tag.contains("sections", Tag.TAG_LIST)) {
            ListTag sections = tag.getList("sections", Tag.TAG_COMPOUND);
            for (int i = 0; i < sections.size(); i++) {
                CompoundTag section = sections.getCompound(i);
                if (section.contains("block_states", Tag.TAG_COMPOUND)) {
                    CompoundTag blockStates = section.getCompound("block_states");
                    if (blockStates.contains("palette", Tag.TAG_LIST)) {
                        ListTag palette = blockStates.getList("palette", Tag.TAG_COMPOUND);
                        for (int j = 0; j < palette.size(); j++) {
                            CompoundTag blockState = palette.getCompound(j);
                            if (blockState.contains("Name", Tag.TAG_STRING)) {
                                String name = blockState.getString("Name");
                                String remapped = copperagebackport$remapId(name, "Block");
                                if (remapped != null) {
                                    blockState.putString("Name", remapped);
                                    modified = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Remap block entities
        if (tag.contains("block_entities", Tag.TAG_LIST)) {
            ListTag blockEntities = tag.getList("block_entities", Tag.TAG_COMPOUND);
            for (int i = 0; i < blockEntities.size(); i++) {
                CompoundTag blockEntity = blockEntities.getCompound(i);
                if (blockEntity.contains("id", Tag.TAG_STRING)) {
                    String id = blockEntity.getString("id");
                    String remapped = copperagebackport$remapId(id, "BlockEntity");
                    if (remapped != null) {
                        blockEntity.putString("id", remapped);
                        modified = true;
                    }
                }
                
                // Remap items inside block entities (like chests)
                copperagebackport$remapContainerItems(blockEntity);
            }
        }
        
        // Remap entities
        if (tag.contains("entities", Tag.TAG_LIST)) {
            ListTag entities = tag.getList("entities", Tag.TAG_COMPOUND);
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                if (entity.contains("id", Tag.TAG_STRING)) {
                    String id = entity.getString("id");
                    String remapped = copperagebackport$remapId(id, "Entity");
                    if (remapped != null) {
                        entity.putString("id", remapped);
                        modified = true;
                    }
                }
            }
        }
        
        if (modified) {
            Constants.LOG.debug("Remapped legacy copperagebackport: IDs in chunk data");
        }
        
        return tag;
    }
    
    /**
     * Remap items in container block entities
     */
    @Unique
    private static void copperagebackport$remapContainerItems(CompoundTag blockEntity) {
        if (blockEntity.contains("Items", Tag.TAG_LIST)) {
            ListTag items = blockEntity.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag item = items.getCompound(i);
                if (item.contains("id", Tag.TAG_STRING)) {
                    String id = item.getString("id");
                    String remapped = copperagebackport$remapId(id, "ContainerItem");
                    if (remapped != null) {
                        item.putString("id", remapped);
                    }
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
