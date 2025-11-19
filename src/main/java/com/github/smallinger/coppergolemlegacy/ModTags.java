package com.github.smallinger.coppergolemlegacy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> COPPER = tag("copper");
        public static final TagKey<Block> COPPER_CHESTS = tag("copper_chests");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(CopperGolemLegacy.MODID, name));
        }
    }
}

