package com.github.smallinger.coppergolemlegacy;

import org.slf4j.Logger;

import com.github.smallinger.coppergolemlegacy.entity.CopperGolemEntity;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.github.smallinger.coppergolemlegacy.block.CopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperChestBlock;
import com.github.smallinger.coppergolemlegacy.block.WeatheringCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperButtonBlock;
import com.github.smallinger.coppergolemlegacy.block.WaxedCopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperChestBlockEntity;
import com.github.smallinger.coppergolemlegacy.block.entity.CopperGolemStatueBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CopperGolemLegacy.MODID)
public class CopperGolemLegacy {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "coppergolemlegacy";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Items which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold Entities which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    // Create a Deferred Register to hold Blocks which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    // Create a Deferred Register to hold Block Entities which will all be registered under the "coppergolemlegacy" namespace
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    // Register Copper Golem Entity
    public static final DeferredHolder<EntityType<?>, EntityType<CopperGolemEntity>> COPPER_GOLEM = ENTITY_TYPES.register("copper_golem",
        () -> EntityType.Builder.of(CopperGolemEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.3F)
            .clientTrackingRange(8)
            .build("copper_golem"));
    
    // Register Copper Chest Blocks
    public static final DeferredHolder<Block, WeatheringCopperChestBlock> COPPER_CHEST = BLOCKS.register("copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final DeferredHolder<Block, WeatheringCopperChestBlock> EXPOSED_COPPER_CHEST = BLOCKS.register("exposed_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final DeferredHolder<Block, WeatheringCopperChestBlock> WEATHERED_COPPER_CHEST = BLOCKS.register("weathered_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    public static final DeferredHolder<Block, WeatheringCopperChestBlock> OXIDIZED_COPPER_CHEST = BLOCKS.register("oxidized_copper_chest",
        () -> new WeatheringCopperChestBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()
                .randomTicks()));
    
    // Register Waxed Copper Chest Blocks
    public static final DeferredHolder<Block, CopperChestBlock> WAXED_COPPER_CHEST = BLOCKS.register("waxed_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final DeferredHolder<Block, CopperChestBlock> WAXED_EXPOSED_COPPER_CHEST = BLOCKS.register("waxed_exposed_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final DeferredHolder<Block, CopperChestBlock> WAXED_WEATHERED_COPPER_CHEST = BLOCKS.register("waxed_weathered_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    public static final DeferredHolder<Block, CopperChestBlock> WAXED_OXIDIZED_COPPER_CHEST = BLOCKS.register("waxed_oxidized_copper_chest",
        () -> new CopperChestBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER)
                .requiresCorrectToolForDrops()));
    
    // Register Copper Button Blocks
    public static final DeferredHolder<Block, CopperButtonBlock> COPPER_BUTTON = BLOCKS.register("copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, CopperButtonBlock> EXPOSED_COPPER_BUTTON = BLOCKS.register("exposed_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, CopperButtonBlock> WEATHERED_COPPER_BUTTON = BLOCKS.register("weathered_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, CopperButtonBlock> OXIDIZED_COPPER_BUTTON = BLOCKS.register("oxidized_copper_button",
        () -> new CopperButtonBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    // Register Waxed Copper Button Blocks
    public static final DeferredHolder<Block, WaxedCopperButtonBlock> WAXED_COPPER_BUTTON = BLOCKS.register("waxed_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            () -> COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, WaxedCopperButtonBlock> WAXED_EXPOSED_COPPER_BUTTON = BLOCKS.register("waxed_exposed_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            () -> EXPOSED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, WaxedCopperButtonBlock> WAXED_WEATHERED_COPPER_BUTTON = BLOCKS.register("waxed_weathered_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            () -> WEATHERED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    public static final DeferredHolder<Block, WaxedCopperButtonBlock> WAXED_OXIDIZED_COPPER_BUTTON = BLOCKS.register("waxed_oxidized_copper_button",
        () -> new WaxedCopperButtonBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            () -> OXIDIZED_COPPER_BUTTON.get(),
            BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.COPPER)));
    
    // Register Copper Golem Statue Blocks
    public static final DeferredHolder<Block, WeatheringCopperGolemStatueBlock> COPPER_GOLEM_STATUE = BLOCKS.register("copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WeatheringCopperGolemStatueBlock> EXPOSED_COPPER_GOLEM_STATUE = BLOCKS.register("exposed_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WeatheringCopperGolemStatueBlock> WEATHERED_COPPER_GOLEM_STATUE = BLOCKS.register("weathered_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WeatheringCopperGolemStatueBlock> OXIDIZED_COPPER_GOLEM_STATUE = BLOCKS.register("oxidized_copper_golem_statue",
        () -> new WeatheringCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .randomTicks()
                .noOcclusion()));
    
    // Register Waxed Copper Golem Statue Blocks
    public static final DeferredHolder<Block, WaxedCopperGolemStatueBlock> WAXED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WaxedCopperGolemStatueBlock> WAXED_EXPOSED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_exposed_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WaxedCopperGolemStatueBlock> WAXED_WEATHERED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_weathered_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    public static final DeferredHolder<Block, WaxedCopperGolemStatueBlock> WAXED_OXIDIZED_COPPER_GOLEM_STATUE = BLOCKS.register("waxed_oxidized_copper_golem_statue",
        () -> new WaxedCopperGolemStatueBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.of()
                .strength(3.0F, 6.0F)
                .sound(ModSoundTypes.COPPER_STATUE)
                .requiresCorrectToolForDrops()
                .noOcclusion()));
    
    // Register Copper Chest Block Entity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperChestBlockEntity>> COPPER_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("copper_chest",
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperGolemStatueBlockEntity>> COPPER_GOLEM_STATUE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("copper_golem_statue",
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
    
    // Register Copper Golem Spawn Egg
    public static final DeferredItem<SpawnEggItem> COPPER_GOLEM_SPAWN_EGG = ITEMS.register("copper_golem_spawn_egg",
        () -> new SpawnEggItem(COPPER_GOLEM.get(), 0xB87333, 0x48D1CC, new Item.Properties()));
    
    // Register Copper Chest Items
    public static final DeferredItem<BlockItem> COPPER_CHEST_ITEM = ITEMS.register("copper_chest",
        () -> new BlockItem(COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> EXPOSED_COPPER_CHEST_ITEM = ITEMS.register("exposed_copper_chest",
        () -> new BlockItem(EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WEATHERED_COPPER_CHEST_ITEM = ITEMS.register("weathered_copper_chest",
        () -> new BlockItem(WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> OXIDIZED_COPPER_CHEST_ITEM = ITEMS.register("oxidized_copper_chest",
        () -> new BlockItem(OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
    
    // Register Waxed Copper Chest Items
    public static final DeferredItem<BlockItem> WAXED_COPPER_CHEST_ITEM = ITEMS.register("waxed_copper_chest",
        () -> new BlockItem(WAXED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_EXPOSED_COPPER_CHEST_ITEM = ITEMS.register("waxed_exposed_copper_chest",
        () -> new BlockItem(WAXED_EXPOSED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_WEATHERED_COPPER_CHEST_ITEM = ITEMS.register("waxed_weathered_copper_chest",
        () -> new BlockItem(WAXED_WEATHERED_COPPER_CHEST.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_OXIDIZED_COPPER_CHEST_ITEM = ITEMS.register("waxed_oxidized_copper_chest",
        () -> new BlockItem(WAXED_OXIDIZED_COPPER_CHEST.get(), new Item.Properties()));
    
    // Register Copper Button Items
    public static final DeferredItem<BlockItem> COPPER_BUTTON_ITEM = ITEMS.register("copper_button",
        () -> new BlockItem(COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> EXPOSED_COPPER_BUTTON_ITEM = ITEMS.register("exposed_copper_button",
        () -> new BlockItem(EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WEATHERED_COPPER_BUTTON_ITEM = ITEMS.register("weathered_copper_button",
        () -> new BlockItem(WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> OXIDIZED_COPPER_BUTTON_ITEM = ITEMS.register("oxidized_copper_button",
        () -> new BlockItem(OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
    
    // Register Waxed Copper Button Items
    public static final DeferredItem<BlockItem> WAXED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_copper_button",
        () -> new BlockItem(WAXED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_EXPOSED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_exposed_copper_button",
        () -> new BlockItem(WAXED_EXPOSED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_WEATHERED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_weathered_copper_button",
        () -> new BlockItem(WAXED_WEATHERED_COPPER_BUTTON.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WAXED_OXIDIZED_COPPER_BUTTON_ITEM = ITEMS.register("waxed_oxidized_copper_button",
        () -> new BlockItem(WAXED_OXIDIZED_COPPER_BUTTON.get(), new Item.Properties()));
    
    // Register Copper Golem Statue Items
    public static final DeferredItem<BlockItem> COPPER_GOLEM_STATUE_ITEM = ITEMS.register("copper_golem_statue",
        () -> new BlockItem(COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> EXPOSED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("exposed_copper_golem_statue",
        () -> new BlockItem(EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> WEATHERED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("weathered_copper_golem_statue",
        () -> new BlockItem(WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));
    
    public static final DeferredItem<BlockItem> OXIDIZED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("oxidized_copper_golem_statue",
        () -> new BlockItem(OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    // Register Waxed Copper Golem Statue Items
    public static final DeferredItem<BlockItem> WAXED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_copper_golem_statue",
        () -> new BlockItem(WAXED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> WAXED_EXPOSED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_exposed_copper_golem_statue",
        () -> new BlockItem(WAXED_EXPOSED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> WAXED_WEATHERED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_weathered_copper_golem_statue",
        () -> new BlockItem(WAXED_WEATHERED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> WAXED_OXIDIZED_COPPER_GOLEM_STATUE_ITEM = ITEMS.register("waxed_oxidized_copper_golem_statue",
        () -> new BlockItem(WAXED_OXIDIZED_COPPER_GOLEM_STATUE.get(), new Item.Properties()));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CopperGolemLegacy(IEventBus modEventBus, ModContainer modContainer) {
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
        modContainer.registerConfig(ModConfig.Type.COMMON, CopperGolemLegacyConfig.SPEC);
        
        // Register entity attributes
        modEventBus.addListener(this::registerEntityAttributes);
        
        // Set up waxed button references after blocks are registered
        modEventBus.addListener(this::setupButtonReferences);

        // Register ourselves for server and other game events we are interested in.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }
    
    // Set up waxed button references
    private void setupButtonReferences(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
        LOGGER.info("HELLO from server starting");
    }
}


