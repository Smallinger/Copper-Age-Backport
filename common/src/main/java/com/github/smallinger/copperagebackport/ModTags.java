package com.github.smallinger.copperagebackport;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> COPPER = tag("copper");
        public static final TagKey<Block> COPPER_CHESTS = tag("copper_chests");
        public static final TagKey<Block> GOLEM_TARGET_CHESTS = tag("golem_target_chests");
        public static final TagKey<Block> GOLEM_TARGET_BARRELS = tag("golem_target_barrels");
        public static final TagKey<Block> WOODEN_SHELVES = tag("wooden_shelves");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Constants.MOD_ID, name));
        }
    }
}

