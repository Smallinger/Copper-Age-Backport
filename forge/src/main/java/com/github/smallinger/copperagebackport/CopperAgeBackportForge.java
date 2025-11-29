package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.CommonClass;
import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import com.github.smallinger.copperagebackport.platform.ForgeRegistryHelper;
import com.github.smallinger.copperagebackport.registry.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

@Mod(Constants.MOD_ID)
public class CopperAgeBackportForge {
    
    public CopperAgeBackportForge() {
        // Initialize config first
        CommonConfig.init(FMLPaths.CONFIGDIR.get());
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Initialize the registry helper for Forge
        RegistryHelper.setInstance(new ForgeRegistryHelper());
        
        // Initialize common mod content
        CommonClass.init();
        
        // Register entity attributes
        modEventBus.addListener(this::registerEntityAttributes);
        
        // Register creative tabs
        modEventBus.addListener(this::addCreative);
        
        // Common setup (for button references)
        modEventBus.addListener(this::commonSetup);
        
        // Register loot modifiers
        com.github.smallinger.copperagebackport.forge.loot.ForgeLootTableModifier.register(modEventBus);
        
        // Register config screen only on client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            com.github.smallinger.copperagebackport.client.CopperAgeBackportForgeClient.registerConfigScreen();
            // Register render layers in FMLClientSetupEvent instead
            modEventBus.addListener(com.github.smallinger.copperagebackport.client.CopperAgeBackportForgeClient::onClientSetup);
        });
    }
    
    private void commonSetup(FMLCommonSetupEvent event) {
        // Fire registration callbacks (like button waxed references)
        event.enqueueWork(() -> {
            if (RegistryHelper.getInstance() instanceof ForgeRegistryHelper helper) {
                helper.fireRegistrationCallbacks();
            }
        });
    }
    
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COPPER_GOLEM.get(), CommonClass.getCopperGolemAttributes().build());
    }
    
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Add spawn egg to spawn eggs tab
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.COPPER_GOLEM_SPAWN_EGG.get());
        }
        
        // Add copper chests after normal chest
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            var entries = event.getEntries();
            entries.putAfter(Items.CHEST.getDefaultInstance(), ModItems.COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Lightning Rods after vanilla lightning rod
            entries.putAfter(Items.LIGHTNING_ROD.getDefaultInstance(), ModItems.EXPOSED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.EXPOSED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WEATHERED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OXIDIZED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.WAXED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_EXPOSED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_WEATHERED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_LIGHTNING_ROD_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Copper Golem Statues (keep at end)
            event.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Shelves after chiseled bookshelf
            entries.putAfter(Items.CHISELED_BOOKSHELF.getDefaultInstance(), ModItems.OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OAK_SHELF_ITEM.get().getDefaultInstance(), ModItems.SPRUCE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.SPRUCE_SHELF_ITEM.get().getDefaultInstance(), ModItems.BIRCH_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.BIRCH_SHELF_ITEM.get().getDefaultInstance(), ModItems.JUNGLE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.JUNGLE_SHELF_ITEM.get().getDefaultInstance(), ModItems.ACACIA_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.ACACIA_SHELF_ITEM.get().getDefaultInstance(), ModItems.DARK_OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.DARK_OAK_SHELF_ITEM.get().getDefaultInstance(), ModItems.MANGROVE_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.MANGROVE_SHELF_ITEM.get().getDefaultInstance(), ModItems.CHERRY_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.CHERRY_SHELF_ITEM.get().getDefaultInstance(), ModItems.BAMBOO_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.BAMBOO_SHELF_ITEM.get().getDefaultInstance(), ModItems.CRIMSON_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.CRIMSON_SHELF_ITEM.get().getDefaultInstance(), ModItems.WARPED_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                entries.putAfter(ModItems.WARPED_SHELF_ITEM.get().getDefaultInstance(), ModItems.PALE_OAK_SHELF_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
        
        // Add copper buttons after stone button
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            var entries = event.getEntries();
            entries.putAfter(Items.STONE_BUTTON.getDefaultInstance(), ModItems.COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper lanterns after soul lantern, copper torch at end
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            var entries = event.getEntries();
            entries.putAfter(Items.SOUL_LANTERN.getDefaultInstance(), ModItems.COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_EXPOSED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_WEATHERED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_LANTERN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Copper Torch after soul torch
            entries.putAfter(Items.SOUL_TORCH.getDefaultInstance(), ModItems.COPPER_TORCH_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper tools after stone tools
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            var entries = event.getEntries();
            entries.putAfter(Items.STONE_HOE.getDefaultInstance(), ModItems.COPPER_SHOVEL.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_SHOVEL.get().getDefaultInstance(), ModItems.COPPER_PICKAXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_PICKAXE.get().getDefaultInstance(), ModItems.COPPER_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_AXE.get().getDefaultInstance(), ModItems.COPPER_HOE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper sword after stone sword, copper armor after chainmail armor
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            var entries = event.getEntries();
            entries.putAfter(Items.STONE_SWORD.getDefaultInstance(), ModItems.COPPER_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(Items.CHAINMAIL_BOOTS.getDefaultInstance(), ModItems.COPPER_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_HELMET.get().getDefaultInstance(), ModItems.COPPER_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_CHESTPLATE.get().getDefaultInstance(), ModItems.COPPER_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_LEGGINGS.get().getDefaultInstance(), ModItems.COPPER_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // Add copper horse armor after leather horse armor (before iron horse armor)
            entries.putAfter(Items.LEATHER_HORSE_ARMOR.getDefaultInstance(), ModItems.COPPER_HORSE_ARMOR.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper nugget after iron nugget
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.getEntries().putAfter(Items.IRON_NUGGET.getDefaultInstance(), ModItems.COPPER_NUGGET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        
        // Add copper bars and chains after cut copper slabs in building blocks
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            var entries = event.getEntries();
            // After cut copper slab
            entries.putAfter(Items.CUT_COPPER_SLAB.getDefaultInstance(), ModItems.COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After exposed cut copper slab
            entries.putAfter(Items.EXPOSED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.EXPOSED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After weathered cut copper slab
            entries.putAfter(Items.WEATHERED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WEATHERED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After oxidized cut copper slab
            entries.putAfter(Items.OXIDIZED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.OXIDIZED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed cut copper slab
            entries.putAfter(Items.WAXED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed exposed cut copper slab
            entries.putAfter(Items.WAXED_EXPOSED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_EXPOSED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_EXPOSED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed weathered cut copper slab
            entries.putAfter(Items.WAXED_WEATHERED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_WEATHERED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_WEATHERED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            // After waxed oxidized cut copper slab
            entries.putAfter(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB.getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(ModItems.WAXED_OXIDIZED_COPPER_BARS_ITEM.get().getDefaultInstance(), ModItems.WAXED_OXIDIZED_COPPER_CHAIN_ITEM.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
    
}
