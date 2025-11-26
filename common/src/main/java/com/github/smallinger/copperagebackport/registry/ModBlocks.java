package com.github.smallinger.copperagebackport.registry;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.ModSoundTypes;
import com.github.smallinger.copperagebackport.block.*;
import com.github.smallinger.copperagebackport.block.shelf.ShelfBlock;
import com.github.smallinger.copperagebackport.platform.Services;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.BLOCK;

/**
 * Handles registration of all blocks for the mod.
 */
public class ModBlocks {
    
    // Copper Chest Blocks (Weathering)
    public static Supplier<WeatheringCopperChestBlock> COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> EXPOSED_COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> WEATHERED_COPPER_CHEST;
    public static Supplier<WeatheringCopperChestBlock> OXIDIZED_COPPER_CHEST;
    
    // Waxed Copper Chest Blocks
    public static Supplier<CopperChestBlock> WAXED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_EXPOSED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_WEATHERED_COPPER_CHEST;
    public static Supplier<CopperChestBlock> WAXED_OXIDIZED_COPPER_CHEST;
    
    // Copper Button Blocks (Weathering)
    public static Supplier<CopperButtonBlock> COPPER_BUTTON;
    public static Supplier<CopperButtonBlock> EXPOSED_COPPER_BUTTON;
    public static Supplier<CopperButtonBlock> WEATHERED_COPPER_BUTTON;
    public static Supplier<CopperButtonBlock> OXIDIZED_COPPER_BUTTON;
    
    // Waxed Copper Button Blocks
    public static Supplier<WaxedCopperButtonBlock> WAXED_COPPER_BUTTON;
    public static Supplier<WaxedCopperButtonBlock> WAXED_EXPOSED_COPPER_BUTTON;
    public static Supplier<WaxedCopperButtonBlock> WAXED_WEATHERED_COPPER_BUTTON;
    public static Supplier<WaxedCopperButtonBlock> WAXED_OXIDIZED_COPPER_BUTTON;
    
    // Copper Golem Statue Blocks (Weathering)
    public static Supplier<WeatheringCopperGolemStatueBlock> COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> EXPOSED_COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> WEATHERED_COPPER_GOLEM_STATUE;
    public static Supplier<WeatheringCopperGolemStatueBlock> OXIDIZED_COPPER_GOLEM_STATUE;
    
    // Waxed Copper Golem Statue Blocks
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_WEATHERED_COPPER_GOLEM_STATUE;
    public static Supplier<WaxedCopperGolemStatueBlock> WAXED_OXIDIZED_COPPER_GOLEM_STATUE;
    
    // Shelf Blocks
    public static Supplier<ShelfBlock> OAK_SHELF;
    public static Supplier<ShelfBlock> SPRUCE_SHELF;
    public static Supplier<ShelfBlock> BIRCH_SHELF;
    public static Supplier<ShelfBlock> JUNGLE_SHELF;
    public static Supplier<ShelfBlock> ACACIA_SHELF;
    public static Supplier<ShelfBlock> DARK_OAK_SHELF;
    public static Supplier<ShelfBlock> MANGROVE_SHELF;
    public static Supplier<ShelfBlock> CHERRY_SHELF;
    public static Supplier<ShelfBlock> BAMBOO_SHELF;
    public static Supplier<ShelfBlock> CRIMSON_SHELF;
    public static Supplier<ShelfBlock> WARPED_SHELF;
    public static Supplier<ShelfBlock> PALE_OAK_SHELF; // Requires VanillaBackport for crafting
    
    // Copper Torch Blocks
    public static Supplier<CopperTorchBlock> COPPER_TORCH;
    public static Supplier<CopperWallTorchBlock> COPPER_WALL_TORCH;
    
    // Copper Lantern Blocks (Weathering)
    public static Supplier<WeatheringCopperLanternBlock> COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> EXPOSED_COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> WEATHERED_COPPER_LANTERN;
    public static Supplier<WeatheringCopperLanternBlock> OXIDIZED_COPPER_LANTERN;
    
    // Waxed Copper Lantern Blocks
    public static Supplier<CopperLanternBlock> WAXED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_EXPOSED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_WEATHERED_COPPER_LANTERN;
    public static Supplier<CopperLanternBlock> WAXED_OXIDIZED_COPPER_LANTERN;
    
    // Copper Chain Blocks (Weathering)
    public static Supplier<WeatheringCopperChainBlock> COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> EXPOSED_COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> WEATHERED_COPPER_CHAIN;
    public static Supplier<WeatheringCopperChainBlock> OXIDIZED_COPPER_CHAIN;
    
    // Waxed Copper Chain Blocks
    public static Supplier<CopperChainBlock> WAXED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_EXPOSED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_WEATHERED_COPPER_CHAIN;
    public static Supplier<CopperChainBlock> WAXED_OXIDIZED_COPPER_CHAIN;
    
    // Copper Grate Blocks (Weathering)
    public static Supplier<WeatheringCopperGrateBlock> COPPER_GRATE;
    public static Supplier<WeatheringCopperGrateBlock> EXPOSED_COPPER_GRATE;
    public static Supplier<WeatheringCopperGrateBlock> WEATHERED_COPPER_GRATE;
    public static Supplier<WeatheringCopperGrateBlock> OXIDIZED_COPPER_GRATE;
    
    // Waxed Copper Grate Blocks
    public static Supplier<CopperGrateBlock> WAXED_COPPER_GRATE;
    public static Supplier<CopperGrateBlock> WAXED_EXPOSED_COPPER_GRATE;
    public static Supplier<CopperGrateBlock> WAXED_WEATHERED_COPPER_GRATE;
    public static Supplier<CopperGrateBlock> WAXED_OXIDIZED_COPPER_GRATE;
    
    public static void register() {
        Constants.LOG.info("Registering blocks for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Copper Chest Blocks
        COPPER_CHEST = helper.register(BLOCK, "copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHEST = helper.register(BLOCK, "exposed_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHEST = helper.register(BLOCK, "weathered_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHEST = helper.register(BLOCK, "oxidized_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        // Register Waxed Copper Chest Blocks
        WAXED_COPPER_CHEST = helper.register(BLOCK, "waxed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_EXPOSED_COPPER_CHEST = helper.register(BLOCK, "waxed_exposed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_WEATHERED_COPPER_CHEST = helper.register(BLOCK, "waxed_weathered_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_OXIDIZED_COPPER_CHEST = helper.register(BLOCK, "waxed_oxidized_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        // Register Copper Button Blocks
        COPPER_BUTTON = helper.register(BLOCK, "copper_button",
            () -> new CopperButtonBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        EXPOSED_COPPER_BUTTON = helper.register(BLOCK, "exposed_copper_button",
            () -> new CopperButtonBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        WEATHERED_COPPER_BUTTON = helper.register(BLOCK, "weathered_copper_button",
            () -> new CopperButtonBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        OXIDIZED_COPPER_BUTTON = helper.register(BLOCK, "oxidized_copper_button",
            () -> new CopperButtonBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        // Register Waxed Copper Button Blocks
        WAXED_COPPER_BUTTON = helper.register(BLOCK, "waxed_copper_button",
            () -> new WaxedCopperButtonBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                COPPER_BUTTON,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        WAXED_EXPOSED_COPPER_BUTTON = helper.register(BLOCK, "waxed_exposed_copper_button",
            () -> new WaxedCopperButtonBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                EXPOSED_COPPER_BUTTON,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        WAXED_WEATHERED_COPPER_BUTTON = helper.register(BLOCK, "waxed_weathered_copper_button",
            () -> new WaxedCopperButtonBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                WEATHERED_COPPER_BUTTON,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        WAXED_OXIDIZED_COPPER_BUTTON = helper.register(BLOCK, "waxed_oxidized_copper_button",
            () -> new WaxedCopperButtonBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                OXIDIZED_COPPER_BUTTON,
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(0.5F)
                    .sound(SoundType.COPPER)));
        
        // Register Copper Golem Statue Blocks
        COPPER_GOLEM_STATUE = helper.register(BLOCK, "copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        EXPOSED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "exposed_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        WEATHERED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "weathered_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        OXIDIZED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "oxidized_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        // Register Waxed Copper Golem Statue Blocks
        WAXED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "waxed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "waxed_exposed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "waxed_weathered_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_GOLEM_STATUE = helper.register(BLOCK, "waxed_oxidized_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        // Register Shelf Blocks
        OAK_SHELF = helper.register(BLOCK, "oak_shelf", () -> new ShelfBlock(shelfProperties()));
        SPRUCE_SHELF = helper.register(BLOCK, "spruce_shelf", () -> new ShelfBlock(shelfProperties()));
        BIRCH_SHELF = helper.register(BLOCK, "birch_shelf", () -> new ShelfBlock(shelfProperties()));
        JUNGLE_SHELF = helper.register(BLOCK, "jungle_shelf", () -> new ShelfBlock(shelfProperties()));
        ACACIA_SHELF = helper.register(BLOCK, "acacia_shelf", () -> new ShelfBlock(shelfProperties()));
        DARK_OAK_SHELF = helper.register(BLOCK, "dark_oak_shelf", () -> new ShelfBlock(shelfProperties()));
        MANGROVE_SHELF = helper.register(BLOCK, "mangrove_shelf", () -> new ShelfBlock(shelfProperties()));
        CHERRY_SHELF = helper.register(BLOCK, "cherry_shelf", () -> new ShelfBlock(shelfProperties()));
        BAMBOO_SHELF = helper.register(BLOCK, "bamboo_shelf", () -> new ShelfBlock(shelfProperties()));
        CRIMSON_SHELF = helper.register(BLOCK, "crimson_shelf", () -> new ShelfBlock(shelfProperties()));
        WARPED_SHELF = helper.register(BLOCK, "warped_shelf", () -> new ShelfBlock(shelfProperties()));
        
        // Pale Oak Shelf - only register if VanillaBackport is loaded
        if (Services.PLATFORM.isModLoaded("vanillabackport")) {
            PALE_OAK_SHELF = helper.register(BLOCK, "pale_oak_shelf", () -> new ShelfBlock(shelfProperties()));
            Constants.LOG.info("VanillaBackport detected - Pale Oak Shelf enabled");
        } else {
            Constants.LOG.info("VanillaBackport not found - Pale Oak Shelf disabled");
        }
        
        // Register Copper Torch Blocks
        COPPER_TORCH = helper.register(BLOCK, "copper_torch",
            () -> new CopperTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)));
        
        COPPER_WALL_TORCH = helper.register(BLOCK, "copper_wall_torch",
            () -> new CopperWallTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
                    .dropsLike(COPPER_TORCH.get())));
        
        // Register Copper Lantern Blocks (Weathering)
        COPPER_LANTERN = helper.register(BLOCK, "copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        EXPOSED_COPPER_LANTERN = helper.register(BLOCK, "exposed_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        WEATHERED_COPPER_LANTERN = helper.register(BLOCK, "weathered_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        OXIDIZED_COPPER_LANTERN = helper.register(BLOCK, "oxidized_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        // Register Waxed Copper Lantern Blocks
        WAXED_COPPER_LANTERN = helper.register(BLOCK, "waxed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_EXPOSED_COPPER_LANTERN = helper.register(BLOCK, "waxed_exposed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_WEATHERED_COPPER_LANTERN = helper.register(BLOCK, "waxed_weathered_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_OXIDIZED_COPPER_LANTERN = helper.register(BLOCK, "waxed_oxidized_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Copper Chain Blocks
        COPPER_CHAIN = helper.register(BLOCK, "copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHAIN = helper.register(BLOCK, "exposed_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHAIN = helper.register(BLOCK, "weathered_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHAIN = helper.register(BLOCK, "oxidized_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        // Register Waxed Copper Chain Blocks
        WAXED_COPPER_CHAIN = helper.register(BLOCK, "waxed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_CHAIN = helper.register(BLOCK, "waxed_exposed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_CHAIN = helper.register(BLOCK, "waxed_weathered_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_CHAIN = helper.register(BLOCK, "waxed_oxidized_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        // Register Copper Grate Blocks
        COPPER_GRATE = helper.register(BLOCK, "copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .randomTicks()));
        
        EXPOSED_COPPER_GRATE = helper.register(BLOCK, "exposed_copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .randomTicks()));
        
        WEATHERED_COPPER_GRATE = helper.register(BLOCK, "weathered_copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .randomTicks()));
        
        OXIDIZED_COPPER_GRATE = helper.register(BLOCK, "oxidized_copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .randomTicks()));
        
        // Register Waxed Copper Grate Blocks
        WAXED_COPPER_GRATE = helper.register(BLOCK, "waxed_copper_grate",
            () -> new CopperGrateBlock(
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));
        
        WAXED_EXPOSED_COPPER_GRATE = helper.register(BLOCK, "waxed_exposed_copper_grate",
            () -> new CopperGrateBlock(
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));
        
        WAXED_WEATHERED_COPPER_GRATE = helper.register(BLOCK, "waxed_weathered_copper_grate",
            () -> new CopperGrateBlock(
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));
        
        WAXED_OXIDIZED_COPPER_GRATE = helper.register(BLOCK, "waxed_oxidized_copper_grate",
            () -> new CopperGrateBlock(
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));
        
        // Setup button references after registration
        helper.onRegisterComplete(() -> {
            COPPER_BUTTON.get().setWaxedButton(WAXED_COPPER_BUTTON);
            EXPOSED_COPPER_BUTTON.get().setWaxedButton(WAXED_EXPOSED_COPPER_BUTTON);
            WEATHERED_COPPER_BUTTON.get().setWaxedButton(WAXED_WEATHERED_COPPER_BUTTON);
            OXIDIZED_COPPER_BUTTON.get().setWaxedButton(WAXED_OXIDIZED_COPPER_BUTTON);
        });
    }
    
    private static BlockBehaviour.Properties shelfProperties() {
        return BlockBehaviour.Properties.of()
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion();
    }
}
