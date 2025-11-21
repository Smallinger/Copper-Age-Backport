package com.github.smallinger.coppergolemlegacy;

import com.github.smallinger.coppergolemlegacy.entity.CopperGolemEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.github.smallinger.coppergolemlegacy.block.CopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.coppergolemlegacy.item.Copper3DBlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.github.smallinger.coppergolemlegacy.block.CopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.coppergolemlegacy.item.Copper3DBlockItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CopperGolemLegacy.MODID)
public class CopperGolemLegacy {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "coppergolemlegacy";
    // Create a Deferred Register to hold Items which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold Entities which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    // Create a Deferred Register to hold Blocks which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Block Entities which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    // Register Copper Golem Entity
    public static final RegistryObject<EntityType<CopperGolemEntity>> COPPER_GOLEM = ENTITY_TYPES.register("copper_golem",
        () -> EntityType.Builder.of(CopperGolemEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.3F)  // Breite: 0.6 (schmaler für besseres Durchgehen durch Türen), Höhe: 1.3
            .clientTrackingRange(8)
            .build("copper_golem"));
    
    // Register Copper Chest Blocks
    public static final RegistryObject<WeatheringCopperChestBlock> COPPER_CHEST = BLOCKS.register("copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final RegistryObject<WeatheringCopperChestBlock> EXPOSED_COPPER_CHEST = BLOCKS.register("exposed_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final RegistryObject<WeatheringCopperChestBlock> WEATHERED_COPPER_CHEST = BLOCKS.register("weathered_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final RegistryObject<WeatheringCopperChestBlock> OXIDIZED_COPPER_CHEST = BLOCKS.register("oxidized_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    // Register Waxed Copper Chest Blocks
    public static final RegistryObject<CopperChestBlock> WAXED_COPPER_CHEST = BLOCKS.register("waxed_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<CopperChestBlock> WAXED_EXPOSED_COPPER_CHEST = BLOCKS.register("waxed_exposed_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<CopperChestBlock> WAXED_WEATHERED_COPPER_CHEST = BLOCKS.register("waxed_weathered_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final RegistryObject<CopperChestBlock> WAXED_OXIDIZED_COPPER_CHEST = BLOCKS.register("waxed_oxidized_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    // Register Copper Button Blocks
    public static final RegistryObject<CopperButtonBlock> COPPER_BUTTON = BLOCKS.register("copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<CopperButtonBlock> EXPOSED_COPPER_BUTTON = BLOCKS.register("exposed_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<CopperButtonBlock> WEATHERED_COPPER_BUTTON = BLOCKS.register("weathered_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<CopperButtonBlock> OXIDIZED_COPPER_BUTTON = BLOCKS.register("oxidized_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    // Register Waxed Copper Button Blocks
    public static final RegistryObject<WaxedCopperButtonBlock> WAXED_COPPER_BUTTON = BLOCKS.register("waxed_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            () -> COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<WaxedCopperButtonBlock> WAXED_EXPOSED_COPPER_BUTTON = BLOCKS.register("waxed_exposed_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            () -> EXPOSED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<WaxedCopperButtonBlock> WAXED_WEATHERED_COPPER_BUTTON = BLOCKS.register("waxed_weathered_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            () -> WEATHERED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final RegistryObject<WaxedCopperButtonBlock> WAXED_OXIDIZED_COPPER_BUTTON = BLOCKS.register("waxed_oxidized_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            () -> OXIDIZED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    // Register Copper Golem Statue Blocks
    public static final RegistryObject<WeatheringCopperGolemStatueBlock> COPPER_GOLEM_STATUE = BLOCKS.register("copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final RegistryObject<WeatheringCopperGolemStatueBlock> EXPOSED_COPPER_GOLEM_STATUE = BLOCKS.register("exposed_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final RegistryObject<WeatheringCopperGolemStatueBlock> WEATHERED_COPPER_GOLEM_STATUE = BLOCKS.register("weathered_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final RegistryObject<WeatheringCopperGolemStatueBlock> OXIDIZED_COPPER_GOLEM_STATUE = BLOCKS.register("oxidized_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    // Register Waxed Copper Golem Statue Blocks
    public static final RegistryObject<WaxedCopperGolemStatueBlock> WAXED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final RegistryObject<WaxedCopperGolemStatueBlock> WAXED_EXPOSED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_exposed_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final RegistryObject<WaxedCopperGolemStatueBlock> WAXED_WEATHERED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_weathered_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final RegistryObject<WaxedCopperGolemStatueBlock> WAXED_OXIDIZED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_oxidized_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    // Register Copper Chest Block Entity
    public static final RegistryObject<BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("copper_chest",
        () -> BlockEntityType.Builder.of(
            CopperChestBlockEntity::new,
            COPPER_CHEST.get(),
            EXPOSED_COPPER_CHEST.get(),
            WEATHERED_COPPER_CHEST.get(),
            OXIDIZED_COPPER_CHEST.get(),
            WAXED_COPPER_CHEST.get(),
            WAXED_EXPOSED_COPPER_CHEST.get(),
            WAXED_WEATHERED_COPPER_CHEST.get(),
            WAXED_OXIDIZED_COPPER_CHEST.get()
        ).build(null));
    
    // Register Copper Golem Statue Block Entity
    public static final RegistryObject<BlockEntityType<CopperGolemStatueBlockEntity>> COPPER_GOLEM_STATUE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("copper_golem_statue",
        () -> BlockEntityType.Builder.of(
            CopperGolemStatueBlockEntity::new,
            COPPER_GOLEM_STATUE.get(),
            EXPOSED_COPPER_GOLEM_STATUE.get(),
            WEATHERED_COPPER_GOLEM_STATUE.get(),
            OXIDIZED_COPPER_GOLEM_STATUE.get(),
            WAXED_COPPER_GOLEM_STATUE.get(),
            WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(),
            WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(),
            WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get()
        ).build(null));
    
    // Register Copper Golem Spawn Egg - ForgeSpawnEggItem handles deferred entity type
    public static final RegistryObject<ForgeSpawnEggItem> COPPER_GOLEM_SPAWN_EGG = ITEMS.register("copper_golem_spawn_egg",
        () -> new ForgeSpawnEggItem(COPPER_GOLEM, 0xB87333, 0x48D1CC, new Item.Properties()));
    
    // Register Copper Chest Items
    public static final RegistryObject<BlockItem> COPPER_CHEST_ITEM = ITEMS.register("copper_chest",
        () -> new Copper3DBlockItem(COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> EXPOSED_COPPER_CHEST_ITEM = ITEMS.register("exposed_copper_chest",
        () -> new Copper3DBlockItem(EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WEATHERED_COPPER_CHEST_ITEM = ITEMS.register("weathered_copper_chest",
        () -> new Copper3DBlockItem(WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> OXIDIZED_COPPER_CHEST_ITEM = ITEMS.register("oxidized_copper_chest",
        () -> new Copper3DBlockItem(OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
    
    // Register Waxed Copper Chest Items
    public static final RegistryObject<BlockItem> WAXED_COPPER_CHEST_ITEM = ITEMS.register("waxed_copper_chest",
        () -> new Copper3DBlockItem(WAXED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_EXPOSED_COPPER_CHEST_ITEM = ITEMS.register("waxed_exposed_copper_chest",
        () -> new Copper3DBlockItem(WAXED_EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_WEATHERED_COPPER_CHEST_ITEM = ITEMS.register("waxed_weathered_copper_chest",
        () -> new Copper3DBlockItem(WAXED_WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_OXIDIZED_COPPER_CHEST_ITEM = ITEMS.register("waxed_oxidized_copper_chest",
        () -> new Copper3DBlockItem(WAXED_OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
    
    // Register Copper Button Items
    public static final RegistryObject<BlockItem> COPPER_BUTTON_ITEM = ITEMS.register("copper_button",
        () -> new BlockItem(COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> EXPOSED_COPPER_BUTTON_ITEM = ITEMS.register("exposed_copper_button",
        () -> new BlockItem(EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WEATHERED_COPPER_BUTTON_ITEM = ITEMS.register("weathered_copper_button",
        () -> new BlockItem(WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> OXIDIZED_COPPER_BUTTON_ITEM = ITEMS.register("oxidized_copper_button",
        () -> new BlockItem(OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
    
    // Register Waxed Copper Button Items
    public static final RegistryObject<BlockItem> WAXED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_copper_button",
        () -> new BlockItem(WAXED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_EXPOSED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_exposed_copper_button",
        () -> new BlockItem(WAXED_EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_WEATHERED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_weathered_copper_button",
        () -> new BlockItem(WAXED_WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WAXED_OXIDIZED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_oxidized_copper_button",
        () -> new BlockItem(WAXED_OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
    
    // Register Copper Golem Statue Items
    public static final RegistryObject<BlockItem> COPPER_GOLEM_STATUE_ITEM = ITEMS.register("copper_golem_statue",
        () -> new Copper3DBlockItem(COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> EXPOSED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("exposed_copper_golem_statue",
        () -> new Copper3DBlockItem(EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> WEATHERED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("weathered_copper_golem_statue",
        () -> new Copper3DBlockItem(WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final RegistryObject<BlockItem> OXIDIZED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("oxidized_copper_golem_statue",
        () -> new Copper3DBlockItem(OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    // Register Waxed Copper Golem Statue Items
    public static final RegistryObject<BlockItem> WAXED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_copper_golem_statue",
        () -> new Copper3DBlockItem(WAXED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_exposed_copper_golem_statue",
        () -> new Copper3DBlockItem(WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_weathered_copper_golem_statue",
        () -> new Copper3DBlockItem(WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_oxidized_copper_golem_statue",
        () -> new Copper3DBlockItem(WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // In Forge 1.20.1, we get IEventBus from FMLJavaModLoadingContext instead of constructor injection
    public CopperGolemLegacy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so entities get registered
        ENTITY_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so block entities get registered
        BLOCK_ENTITY_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so sounds get registered
        ModSounds.SOUND_EVENTS.register(modEventBus);
        // Register custom memory module types for Copper Golem Brain AI
        ModMemoryTypes.MEMORY_MODULE_TYPES.register(modEventBus);
        
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CopperGolemLegacyConfig.SPEC);
        
        // Register entity attributes
        modEventBus.addListener(this::registerEntityAttributes);
        
        // Set up waxed button references after blocks are registered
        modEventBus.addListener(this::setupButtonReferences);

        // Register ourselves for server and other game events we are interested in.
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }
    
    // Set up waxed button references
    private void setupButtonReferences(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Set up waxed button references
            COPPER_BUTTON.get().setWaxedButton(() -> WAXED_COPPER_BUTTON.get());
            EXPOSED_COPPER_BUTTON.get().setWaxedButton(() -> WAXED_EXPOSED_COPPER_BUTTON.get());
            WEATHERED_COPPER_BUTTON.get().setWaxedButton(() -> WAXED_WEATHERED_COPPER_BUTTON.get());
            OXIDIZED_COPPER_BUTTON.get().setWaxedButton(() -> WAXED_OXIDIZED_COPPER_BUTTON.get());
        });
    }

    // Register entity attributes
    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(COPPER_GOLEM.get(), CopperGolemEntity.createAttributes().build());
    }

    // Add Copper Golem Spawn Egg to spawn eggs tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(COPPER_GOLEM_SPAWN_EGG);
        }
        
        // Add copper chests to functional blocks tab
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(COPPER_CHEST_ITEM);
            event.accept(EXPOSED_COPPER_CHEST_ITEM);
            event.accept(WEATHERED_COPPER_CHEST_ITEM);
            event.accept(OXIDIZED_COPPER_CHEST_ITEM);
            event.accept(WAXED_COPPER_CHEST_ITEM);
            event.accept(WAXED_EXPOSED_COPPER_CHEST_ITEM);
            event.accept(WAXED_WEATHERED_COPPER_CHEST_ITEM);
            event.accept(WAXED_OXIDIZED_COPPER_CHEST_ITEM);
            event.accept(COPPER_GOLEM_STATUE_ITEM);
            event.accept(EXPOSED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(WEATHERED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(OXIDIZED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(WAXED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM);
            event.accept(WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM);
        }
        
        // Add copper buttons to redstone blocks tab
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(COPPER_BUTTON_ITEM);
            event.accept(EXPOSED_COPPER_BUTTON_ITEM);
            event.accept(WEATHERED_COPPER_BUTTON_ITEM);
            event.accept(OXIDIZED_COPPER_BUTTON_ITEM);
            event.accept(WAXED_COPPER_BUTTON_ITEM);
            event.accept(WAXED_EXPOSED_COPPER_BUTTON_ITEM);
            event.accept(WAXED_WEATHERED_COPPER_BUTTON_ITEM);
            event.accept(WAXED_OXIDIZED_COPPER_BUTTON_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Mod initialized
    }
}


