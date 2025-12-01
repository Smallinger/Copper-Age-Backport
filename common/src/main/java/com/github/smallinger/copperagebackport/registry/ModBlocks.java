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
    
    // Copper Bars Blocks (Weathering)
    public static Supplier<WeatheringCopperBarsBlock> COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> EXPOSED_COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> WEATHERED_COPPER_BARS;
    public static Supplier<WeatheringCopperBarsBlock> OXIDIZED_COPPER_BARS;
    
    // Waxed Copper Bars Blocks
    public static Supplier<CopperBarsBlock> WAXED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_EXPOSED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_WEATHERED_COPPER_BARS;
    public static Supplier<CopperBarsBlock> WAXED_OXIDIZED_COPPER_BARS;
    
    // Copper Lightning Rod Blocks (Weathering)
    // Note: Base lightning_rod is vanilla, extended via Mixin (LightningRodBlockMixin)
    public static Supplier<WeatheringCopperLightningRodBlock> EXPOSED_LIGHTNING_ROD;
    public static Supplier<WeatheringCopperLightningRodBlock> WEATHERED_LIGHTNING_ROD;
    public static Supplier<WeatheringCopperLightningRodBlock> OXIDIZED_LIGHTNING_ROD;
    
    // Waxed Copper Lightning Rod Blocks
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_EXPOSED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_WEATHERED_LIGHTNING_ROD;
    public static Supplier<WaxedCopperLightningRodBlock> WAXED_OXIDIZED_LIGHTNING_ROD;
    
    // Copper Trapdoor Blocks (Weathering)
    public static Supplier<WeatheringCopperTrapDoorBlock> COPPER_TRAPDOOR;
    public static Supplier<WeatheringCopperTrapDoorBlock> EXPOSED_COPPER_TRAPDOOR;
    public static Supplier<WeatheringCopperTrapDoorBlock> WEATHERED_COPPER_TRAPDOOR;
    public static Supplier<WeatheringCopperTrapDoorBlock> OXIDIZED_COPPER_TRAPDOOR;
    
    // Waxed Copper Trapdoor Blocks
    public static Supplier<WaxedCopperTrapDoorBlock> WAXED_COPPER_TRAPDOOR;
    public static Supplier<WaxedCopperTrapDoorBlock> WAXED_EXPOSED_COPPER_TRAPDOOR;
    public static Supplier<WaxedCopperTrapDoorBlock> WAXED_WEATHERED_COPPER_TRAPDOOR;
    public static Supplier<WaxedCopperTrapDoorBlock> WAXED_OXIDIZED_COPPER_TRAPDOOR;
    
    // Copper Bulb Blocks (Weathering)
    public static Supplier<WeatheringCopperBulbBlock> COPPER_BULB;
    public static Supplier<WeatheringCopperBulbBlock> EXPOSED_COPPER_BULB;
    public static Supplier<WeatheringCopperBulbBlock> WEATHERED_COPPER_BULB;
    public static Supplier<CopperBulbBlock> OXIDIZED_COPPER_BULB;
    
    // Waxed Copper Bulb Blocks
    public static Supplier<WaxedCopperBulbBlock> WAXED_COPPER_BULB;
    public static Supplier<WaxedCopperBulbBlock> WAXED_EXPOSED_COPPER_BULB;
    public static Supplier<WaxedCopperBulbBlock> WAXED_WEATHERED_COPPER_BULB;
    public static Supplier<WaxedCopperBulbBlock> WAXED_OXIDIZED_COPPER_BULB;
    
    // Copper Grate Blocks (Weathering)
    public static Supplier<Block> COPPER_GRATE;
    public static Supplier<Block> EXPOSED_COPPER_GRATE;
    public static Supplier<Block> WEATHERED_COPPER_GRATE;
    public static Supplier<Block> OXIDIZED_COPPER_GRATE;
    
    // Waxed Copper Grate Blocks
    public static Supplier<Block> WAXED_COPPER_GRATE;
    public static Supplier<Block> WAXED_EXPOSED_COPPER_GRATE;
    public static Supplier<Block> WAXED_WEATHERED_COPPER_GRATE;
    public static Supplier<Block> WAXED_OXIDIZED_COPPER_GRATE;
    
    // Chiseled Copper Blocks (Weathering)
    public static Supplier<Block> CHISELED_COPPER;
    public static Supplier<Block> EXPOSED_CHISELED_COPPER;
    public static Supplier<Block> WEATHERED_CHISELED_COPPER;
    public static Supplier<Block> OXIDIZED_CHISELED_COPPER;
    
    // Waxed Chiseled Copper Blocks
    public static Supplier<Block> WAXED_CHISELED_COPPER;
    public static Supplier<Block> WAXED_EXPOSED_CHISELED_COPPER;
    public static Supplier<Block> WAXED_WEATHERED_CHISELED_COPPER;
    public static Supplier<Block> WAXED_OXIDIZED_CHISELED_COPPER;
    
    // Copper Door Blocks (Weathering)
    public static Supplier<WeatheringCopperDoorBlock> COPPER_DOOR;
    public static Supplier<WeatheringCopperDoorBlock> EXPOSED_COPPER_DOOR;
    public static Supplier<WeatheringCopperDoorBlock> WEATHERED_COPPER_DOOR;
    public static Supplier<WeatheringCopperDoorBlock> OXIDIZED_COPPER_DOOR;
    
    // Waxed Copper Door Blocks
    public static Supplier<CopperDoorBlock> WAXED_COPPER_DOOR;
    public static Supplier<CopperDoorBlock> WAXED_EXPOSED_COPPER_DOOR;
    public static Supplier<CopperDoorBlock> WAXED_WEATHERED_COPPER_DOOR;
    public static Supplier<CopperDoorBlock> WAXED_OXIDIZED_COPPER_DOOR;
    
    public static void register() {
        Constants.LOG.info("Registering blocks for {}", Constants.MOD_NAME);
        
        RegistryHelper helper = RegistryHelper.getInstance();
        
        // Register Copper Chest Blocks
        COPPER_CHEST = helper.registerAuto(BLOCK, "copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHEST = helper.registerAuto(BLOCK, "exposed_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHEST = helper.registerAuto(BLOCK, "weathered_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHEST = helper.registerAuto(BLOCK, "oxidized_copper_chest",
            () -> new WeatheringCopperChestBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .randomTicks()));
        
        // Register Waxed Copper Chest Blocks
        WAXED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_EXPOSED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_exposed_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_WEATHERED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_weathered_copper_chest",
            () -> new CopperChestBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));
        
        WAXED_OXIDIZED_COPPER_CHEST = helper.registerAuto(BLOCK, "waxed_oxidized_copper_chest",
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
        COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        EXPOSED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "exposed_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        WEATHERED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "weathered_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        OXIDIZED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "oxidized_copper_golem_statue",
            () -> new WeatheringCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .noOcclusion()));
        
        // Register Waxed Copper Golem Statue Blocks
        WAXED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_exposed_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_weathered_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_GOLEM_STATUE = helper.registerAuto(BLOCK, "waxed_oxidized_copper_golem_statue",
            () -> new WaxedCopperGolemStatueBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_STATUE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
        
        // Register Shelf Blocks
        OAK_SHELF = helper.registerAuto(BLOCK, "oak_shelf", () -> new ShelfBlock(shelfProperties()));
        SPRUCE_SHELF = helper.registerAuto(BLOCK, "spruce_shelf", () -> new ShelfBlock(shelfProperties()));
        BIRCH_SHELF = helper.registerAuto(BLOCK, "birch_shelf", () -> new ShelfBlock(shelfProperties()));
        JUNGLE_SHELF = helper.registerAuto(BLOCK, "jungle_shelf", () -> new ShelfBlock(shelfProperties()));
        ACACIA_SHELF = helper.registerAuto(BLOCK, "acacia_shelf", () -> new ShelfBlock(shelfProperties()));
        DARK_OAK_SHELF = helper.registerAuto(BLOCK, "dark_oak_shelf", () -> new ShelfBlock(shelfProperties()));
        MANGROVE_SHELF = helper.registerAuto(BLOCK, "mangrove_shelf", () -> new ShelfBlock(shelfProperties()));
        CHERRY_SHELF = helper.registerAuto(BLOCK, "cherry_shelf", () -> new ShelfBlock(shelfProperties()));
        BAMBOO_SHELF = helper.registerAuto(BLOCK, "bamboo_shelf", () -> new ShelfBlock(shelfProperties()));
        CRIMSON_SHELF = helper.registerAuto(BLOCK, "crimson_shelf", () -> new ShelfBlock(shelfProperties()));
        WARPED_SHELF = helper.registerAuto(BLOCK, "warped_shelf", () -> new ShelfBlock(shelfProperties()));
        PALE_OAK_SHELF = helper.registerAuto(BLOCK, "pale_oak_shelf", () -> new ShelfBlock(shelfProperties()));
        
        // Register Copper Torch Blocks
        COPPER_TORCH = helper.registerAuto(BLOCK, "copper_torch",
            () -> new CopperTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)));
        
        COPPER_WALL_TORCH = helper.registerAuto(BLOCK, "copper_wall_torch",
            () -> new CopperWallTorchBlock(
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .lightLevel(p -> 14)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
                    .dropsLike(COPPER_TORCH.get())));
        
        // Register Copper Lantern Blocks (Weathering)
        COPPER_LANTERN = helper.registerAuto(BLOCK, "copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        EXPOSED_COPPER_LANTERN = helper.registerAuto(BLOCK, "exposed_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        WEATHERED_COPPER_LANTERN = helper.registerAuto(BLOCK, "weathered_copper_lantern",
            () -> new WeatheringCopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        OXIDIZED_COPPER_LANTERN = helper.registerAuto(BLOCK, "oxidized_copper_lantern",
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
        WAXED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_EXPOSED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_exposed_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_WEATHERED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_weathered_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_OXIDIZED_COPPER_LANTERN = helper.registerAuto(BLOCK, "waxed_oxidized_copper_lantern",
            () -> new CopperLanternBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(p -> 15)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Copper Chain Blocks
        COPPER_CHAIN = helper.registerAuto(BLOCK, "copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        EXPOSED_COPPER_CHAIN = helper.registerAuto(BLOCK, "exposed_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_COPPER_CHAIN = helper.registerAuto(BLOCK, "weathered_copper_chain",
            () -> new WeatheringCopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_COPPER_CHAIN = helper.registerAuto(BLOCK, "oxidized_copper_chain",
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
        WAXED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_exposed_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_weathered_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_CHAIN = helper.registerAuto(BLOCK, "waxed_oxidized_copper_chain",
            () -> new CopperChainBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.CHAIN)
                    .noOcclusion()));
        
        // Register Copper Bars Blocks (Weathering)
        COPPER_BARS = helper.registerAuto(BLOCK, "copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        EXPOSED_COPPER_BARS = helper.registerAuto(BLOCK, "exposed_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_COPPER_BARS = helper.registerAuto(BLOCK, "weathered_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_COPPER_BARS = helper.registerAuto(BLOCK, "oxidized_copper_bars",
            () -> new WeatheringCopperBarsBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Waxed Copper Bars Blocks
        WAXED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_exposed_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_weathered_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_BARS = helper.registerAuto(BLOCK, "waxed_oxidized_copper_bars",
            () -> new CopperBarsBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Copper Lightning Rod Blocks (Weathering)
        // Note: Base lightning_rod is vanilla, extended via Mixin (LightningRodBlockMixin)
        EXPOSED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "exposed_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "weathered_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "oxidized_lightning_rod",
            () -> new WeatheringCopperLightningRodBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Waxed Copper Lightning Rod Blocks
        WAXED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_EXPOSED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_exposed_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_WEATHERED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_weathered_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_LIGHTNING_ROD = helper.registerAuto(BLOCK, "waxed_oxidized_lightning_rod",
            () -> new WaxedCopperLightningRodBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Copper Trapdoor Blocks (Weathering)
        COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "copper_trapdoor",
            () -> new WeatheringCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        EXPOSED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "exposed_copper_trapdoor",
            () -> new WeatheringCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "weathered_copper_trapdoor",
            () -> new WeatheringCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "oxidized_copper_trapdoor",
            () -> new WeatheringCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Waxed Copper Trapdoor Blocks
        WAXED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "waxed_copper_trapdoor",
            () -> new WaxedCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                COPPER_TRAPDOOR,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "waxed_exposed_copper_trapdoor",
            () -> new WaxedCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                EXPOSED_COPPER_TRAPDOOR,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "waxed_weathered_copper_trapdoor",
            () -> new WaxedCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                WEATHERED_COPPER_TRAPDOOR,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_TRAPDOOR = helper.registerAuto(BLOCK, "waxed_oxidized_copper_trapdoor",
            () -> new WaxedCopperTrapDoorBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                OXIDIZED_COPPER_TRAPDOOR,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()));
        
        // Register Copper Bulb Blocks (Weathering)
        COPPER_BULB = helper.registerAuto(BLOCK, "copper_bulb",
            () -> new WeatheringCopperBulbBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelUnaffected)
                    .randomTicks()));
        
        EXPOSED_COPPER_BULB = helper.registerAuto(BLOCK, "exposed_copper_bulb",
            () -> new WeatheringCopperBulbBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelExposed)
                    .randomTicks()));
        
        WEATHERED_COPPER_BULB = helper.registerAuto(BLOCK, "weathered_copper_bulb",
            () -> new WeatheringCopperBulbBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelWeathered)
                    .randomTicks()));
        
        OXIDIZED_COPPER_BULB = helper.registerAuto(BLOCK, "oxidized_copper_bulb",
            () -> new CopperBulbBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelOxidized)));
        
        // Register Waxed Copper Bulb Blocks
        WAXED_COPPER_BULB = helper.registerAuto(BLOCK, "waxed_copper_bulb",
            () -> new WaxedCopperBulbBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                COPPER_BULB,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelUnaffected)));
        
        WAXED_EXPOSED_COPPER_BULB = helper.registerAuto(BLOCK, "waxed_exposed_copper_bulb",
            () -> new WaxedCopperBulbBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                EXPOSED_COPPER_BULB,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelExposed)));
        
        WAXED_WEATHERED_COPPER_BULB = helper.registerAuto(BLOCK, "waxed_weathered_copper_bulb",
            () -> new WaxedCopperBulbBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                WEATHERED_COPPER_BULB,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelWeathered)));
        
        WAXED_OXIDIZED_COPPER_BULB = helper.registerAuto(BLOCK, "waxed_oxidized_copper_bulb",
            () -> new WaxedCopperBulbBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                OXIDIZED_COPPER_BULB,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .lightLevel(CopperBulbBlock::getLightLevelOxidized)));
        
        // Register Copper Grate Blocks (Weathering)
        COPPER_GRATE = helper.registerAuto(BLOCK, "copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()
                    .randomTicks()));
        
        EXPOSED_COPPER_GRATE = helper.registerAuto(BLOCK, "exposed_copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()
                    .randomTicks()));
        
        WEATHERED_COPPER_GRATE = helper.registerAuto(BLOCK, "weathered_copper_grate",
            () -> new WeatheringCopperGrateBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()
                    .randomTicks()));
        
        OXIDIZED_COPPER_GRATE = helper.registerAuto(BLOCK, "oxidized_copper_grate",
            () -> new CopperGrateBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()));
        
        // Register Waxed Copper Grate Blocks
        WAXED_COPPER_GRATE = helper.registerAuto(BLOCK, "waxed_copper_grate",
            () -> new CopperGrateBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()));
        
        WAXED_EXPOSED_COPPER_GRATE = helper.registerAuto(BLOCK, "waxed_exposed_copper_grate",
            () -> new CopperGrateBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()));
        
        WAXED_WEATHERED_COPPER_GRATE = helper.registerAuto(BLOCK, "waxed_weathered_copper_grate",
            () -> new CopperGrateBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()));
        
        WAXED_OXIDIZED_COPPER_GRATE = helper.registerAuto(BLOCK, "waxed_oxidized_copper_grate",
            () -> new CopperGrateBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(ModSoundTypes.COPPER_GRATE)
                    .noOcclusion()));
        
        // Register Chiseled Copper Blocks (Weathering)
        CHISELED_COPPER = helper.registerAuto(BLOCK, "chiseled_copper",
            () -> new WeatheringChiseledCopperBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .randomTicks()));
        
        EXPOSED_CHISELED_COPPER = helper.registerAuto(BLOCK, "exposed_chiseled_copper",
            () -> new WeatheringChiseledCopperBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .randomTicks()));
        
        WEATHERED_CHISELED_COPPER = helper.registerAuto(BLOCK, "weathered_chiseled_copper",
            () -> new WeatheringChiseledCopperBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .randomTicks()));
        
        OXIDIZED_CHISELED_COPPER = helper.registerAuto(BLOCK, "oxidized_chiseled_copper",
            () -> new ChiseledCopperBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)));
        
        // Register Waxed Chiseled Copper Blocks
        WAXED_CHISELED_COPPER = helper.registerAuto(BLOCK, "waxed_chiseled_copper",
            () -> new ChiseledCopperBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)));
        
        WAXED_EXPOSED_CHISELED_COPPER = helper.registerAuto(BLOCK, "waxed_exposed_chiseled_copper",
            () -> new ChiseledCopperBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)));
        
        WAXED_WEATHERED_CHISELED_COPPER = helper.registerAuto(BLOCK, "waxed_weathered_chiseled_copper",
            () -> new ChiseledCopperBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)));
        
        WAXED_OXIDIZED_CHISELED_COPPER = helper.registerAuto(BLOCK, "waxed_oxidized_chiseled_copper",
            () -> new ChiseledCopperBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)));
        
        // Register Copper Door Blocks
        COPPER_DOOR = helper.registerAuto(BLOCK, "copper_door",
            () -> new WeatheringCopperDoorBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        EXPOSED_COPPER_DOOR = helper.registerAuto(BLOCK, "exposed_copper_door",
            () -> new WeatheringCopperDoorBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        WEATHERED_COPPER_DOOR = helper.registerAuto(BLOCK, "weathered_copper_door",
            () -> new WeatheringCopperDoorBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));
        
        OXIDIZED_COPPER_DOOR = helper.registerAuto(BLOCK, "oxidized_copper_door",
            () -> new WeatheringCopperDoorBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Register Waxed Copper Door Blocks
        WAXED_COPPER_DOOR = helper.registerAuto(BLOCK, "waxed_copper_door",
            () -> new CopperDoorBlock(
                WeatheringCopper.WeatherState.UNAFFECTED,
                COPPER_DOOR,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_EXPOSED_COPPER_DOOR = helper.registerAuto(BLOCK, "waxed_exposed_copper_door",
            () -> new CopperDoorBlock(
                WeatheringCopper.WeatherState.EXPOSED,
                EXPOSED_COPPER_DOOR,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_WEATHERED_COPPER_DOOR = helper.registerAuto(BLOCK, "waxed_weathered_copper_door",
            () -> new CopperDoorBlock(
                WeatheringCopper.WeatherState.WEATHERED,
                WEATHERED_COPPER_DOOR,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        WAXED_OXIDIZED_COPPER_DOOR = helper.registerAuto(BLOCK, "waxed_oxidized_copper_door",
            () -> new CopperDoorBlock(
                WeatheringCopper.WeatherState.OXIDIZED,
                OXIDIZED_COPPER_DOOR,
                BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)));
        
        // Setup button references after registration
        helper.onRegisterComplete(() -> {
            COPPER_BUTTON.get().setWaxedButton(WAXED_COPPER_BUTTON);
            EXPOSED_COPPER_BUTTON.get().setWaxedButton(WAXED_EXPOSED_COPPER_BUTTON);
            WEATHERED_COPPER_BUTTON.get().setWaxedButton(WAXED_WEATHERED_COPPER_BUTTON);
            OXIDIZED_COPPER_BUTTON.get().setWaxedButton(WAXED_OXIDIZED_COPPER_BUTTON);
            
            // Setup trapdoor references
            COPPER_TRAPDOOR.get().setWaxedTrapdoor(WAXED_COPPER_TRAPDOOR);
            EXPOSED_COPPER_TRAPDOOR.get().setWaxedTrapdoor(WAXED_EXPOSED_COPPER_TRAPDOOR);
            WEATHERED_COPPER_TRAPDOOR.get().setWaxedTrapdoor(WAXED_WEATHERED_COPPER_TRAPDOOR);
            OXIDIZED_COPPER_TRAPDOOR.get().setWaxedTrapdoor(WAXED_OXIDIZED_COPPER_TRAPDOOR);
            
            // Setup bulb references
            COPPER_BULB.get().setWaxedBulb(WAXED_COPPER_BULB);
            EXPOSED_COPPER_BULB.get().setWaxedBulb(WAXED_EXPOSED_COPPER_BULB);
            WEATHERED_COPPER_BULB.get().setWaxedBulb(WAXED_WEATHERED_COPPER_BULB);
            OXIDIZED_COPPER_BULB.get().setWaxedBulb(WAXED_OXIDIZED_COPPER_BULB);
            
            // Setup door references
            COPPER_DOOR.get().setWaxedDoor(WAXED_COPPER_DOOR);
            EXPOSED_COPPER_DOOR.get().setWaxedDoor(WAXED_EXPOSED_COPPER_DOOR);
            WEATHERED_COPPER_DOOR.get().setWaxedDoor(WAXED_WEATHERED_COPPER_DOOR);
            OXIDIZED_COPPER_DOOR.get().setWaxedDoor(WAXED_OXIDIZED_COPPER_DOOR);
        });
    }
    
    private static BlockBehaviour.Properties shelfProperties() {
        return BlockBehaviour.Properties.of()
            .strength(1.5F)
            .sound(SoundType.WOOD)
            .noOcclusion();
    }
}
