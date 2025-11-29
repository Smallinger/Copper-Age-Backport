package com.github.smallinger.copperagebackport.compat.modules;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.compat.IModCompatModule;
import com.github.smallinger.copperagebackport.compat.IRendererCompat;
import com.github.smallinger.copperagebackport.registry.ModBlocks;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Compatibility module for FastChest mod.
 * 
 * FastChest replaces animated chest BlockEntityRenderers with static block models
 * for better performance. We need to ensure our copper chests are compatible.
 * 
 * This is the COMMON module - loader-specific implementations should extend this
 * or handle the actual mixin/renderer registration.
 */
public class FastChestCompat implements IModCompatModule, IRendererCompat {
    
    public static final String MOD_ID = "fastchest";
    
    private final Set<Block> fastRenderBlocks = new HashSet<>();
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
    
    @Override
    public void init() {
        // Block registration is deferred to initClient() or when blocks are actually accessed
        // because at this point the block registry may not be fully loaded yet
    }
    
    @Override
    public void initClient() {
        // Register all copper chest variants for fast rendering
        // This is called later when the registry is ready
        try {
            registerFastRenderBlock(ModBlocks.COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.EXPOSED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.WEATHERED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.OXIDIZED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.WAXED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get());
            registerFastRenderBlock(ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get());
            
            Constants.LOG.info("FastChestCompat: Registered {} copper chest blocks for fast rendering", fastRenderBlocks.size());
        } catch (Exception e) {
            Constants.LOG.warn("FastChestCompat: Could not register blocks: {}", e.getMessage());
        }
    }
    
    protected void registerFastRenderBlock(Block block) {
        if (block != null) {
            fastRenderBlocks.add(block);
        }
    }
    
    @Override
    public Set<Block> getFastRenderBlocks() {
        return Set.copyOf(fastRenderBlocks);
    }
    
    @Override
    public boolean isFastRenderEnabled(Block block) {
        return fastRenderBlocks.contains(block);
    }
}
