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
import net.minecraft.world.item.CreativeModeTabs;

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
        
        // Register config screen only on client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> 
            com.github.smallinger.copperagebackport.client.CopperAgeBackportForgeClient.registerConfigScreen()
        );
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
        
        // Add copper chests and statues to functional blocks tab
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.COPPER_CHEST_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_CHEST_ITEM.get());
            event.accept(ModItems.COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM.get());
            // Shelves
            event.accept(ModItems.OAK_SHELF_ITEM.get());
            event.accept(ModItems.SPRUCE_SHELF_ITEM.get());
            event.accept(ModItems.BIRCH_SHELF_ITEM.get());
            event.accept(ModItems.JUNGLE_SHELF_ITEM.get());
            event.accept(ModItems.ACACIA_SHELF_ITEM.get());
            event.accept(ModItems.DARK_OAK_SHELF_ITEM.get());
            event.accept(ModItems.MANGROVE_SHELF_ITEM.get());
            event.accept(ModItems.CHERRY_SHELF_ITEM.get());
            event.accept(ModItems.BAMBOO_SHELF_ITEM.get());
            event.accept(ModItems.CRIMSON_SHELF_ITEM.get());
            event.accept(ModItems.WARPED_SHELF_ITEM.get());
            // Pale Oak Shelf - only if VanillaBackport is loaded
            if (ModItems.PALE_OAK_SHELF_ITEM != null) {
                event.accept(ModItems.PALE_OAK_SHELF_ITEM.get());
            }
        }
        
        // Add copper buttons to redstone blocks tab
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.EXPOSED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WEATHERED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.OXIDIZED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_EXPOSED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_WEATHERED_COPPER_BUTTON_ITEM.get());
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_BUTTON_ITEM.get());
        }
        
        // Add copper torch to functional blocks tab
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.COPPER_TORCH_ITEM.get());
        }
        
        // Add copper tools to tools tab
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.COPPER_SHOVEL.get());
            event.accept(ModItems.COPPER_PICKAXE.get());
            event.accept(ModItems.COPPER_AXE.get());
            event.accept(ModItems.COPPER_HOE.get());
        }
        
        // Add copper sword to combat tab
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.COPPER_SWORD.get());
            event.accept(ModItems.COPPER_HELMET.get());
            event.accept(ModItems.COPPER_CHESTPLATE.get());
            event.accept(ModItems.COPPER_LEGGINGS.get());
            event.accept(ModItems.COPPER_BOOTS.get());
        }
        
        // Add copper nugget to ingredients tab
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.COPPER_NUGGET.get());
        }
    }
    
}
