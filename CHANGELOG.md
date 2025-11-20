# Changelog

All notable changes to the Copper Golem Legacy mod will be documented in this file.

## [1.21.1-0.0.5-hotfix1] - 2025-11-20

### Fixed
- **Tag Loading Error**: Fixed "missing references" error for optional mod chests
  - Issue: Tag loading failed when IronChest mod were not installed
  - Error: `Couldn't load tag coppergolemlegacy:golem_target_chests as it is missing following references`
  - Solution: Changed all optional mod entries to use `{"id": "...", "required": false}` format
  - Affected mods: IronChest (14 chest types)
  - Mod now works properly both with and without IronChest installed

## [1.21.1-0.0.5] - 2025-11-20

### Added
- **Mod Compatibility System**
  - Flexible tag-based system for chest interactions
  - New block tags: `golem_target_chests`
  - Target chests define where golems can **place items to** (all compatible chests)
  - Easy mod integration without code changes - just add block IDs to tags

- **Barrel Support**
  - Copper Golems can now place items into barrels
  - Correct barrel sounds (`BARREL_OPEN` / `BARREL_CLOSE`) when interacting
  - Added `minecraft:barrel` to `golem_target_chests` tag

- **IronChest Mod Integration**
  - Full support for all 14 IronChest variants (7 types + trapped variants)
  - Supported chests: iron, gold, diamond, copper, crystal, obsidian, dirt
  - Both regular and trapped versions work with golem item transport

- **Documentation**
  - `CHEST_COMPATIBILITY.md` - Complete guide for adding new mod support
  - `datapack_example/` - Example datapack for players to add custom chests
  - Instructions for three integration methods: direct block IDs, mod tags, datapacks
  - Examples for popular mods (Storage Drawers, Sophisticated Storage, AE2)

### Changed
- **AI Sound System**
  - Enhanced `playChestSound()` method with container-type detection
  - Automatic sound selection based on container type:
    - Copper Chests → Custom copper chest sounds
    - Barrels → Barrel-specific sounds
    - Regular Chests → Standard chest sounds
  - Improved blockState caching for better performance

- **Code Structure**
  - `ModTags.java` - Added `GOLEM_TARGET_CHESTS` constants
  - `CopperGolemAi.java` - Refactored to use tags instead of hardcoded block checks
  - More maintainable and extensible architecture

### Fixed
- **Container Detection Bug**
  - Fixed golem only detecting vanilla chests (`ChestBlockEntity`)
  - Now properly detects all container types including barrels and mod chests
  - Changed search from `ChestBlockEntity` to `BaseContainerBlockEntity`
  - Enables support for IronChest, barrels, and other mod containers

- **Container Animation Bug**
  - Fixed missing open/close animations for barrels and mod chests
  - Barrels now correctly animate using `OPEN` BlockState property
  - Chests use `blockEvent` for animation
  - All container types (vanilla, barrels, mod chests) now animate properly

### Technical
- Tag-based predicates in `TransportItemsBetweenContainers` behavior
- Container type detection with proper sound event mapping
- Support for datapack-based chest additions with `"replace": false` pattern
- Performance-optimized through Minecraft's tag caching system

## [1.21.1-0.0.4] - 2025-11-19

### Changed
- **Entity Size**: Reduced Copper Golem width from 0.7 to 0.6 blocks
  - Allows golem to fit through doors more easily

### Fixed
- **Chest Interaction Range**: Fixed vertical distance detection for chest interactions
  - Increased vertical reach from 0.5 to match horizontal distance (0.5-1.0 blocks)
  - Golems can now properly reach chests that are one block above or below them
  - Fixes issue where golem standing on a block couldn't interact with chest one block below
  
- **Spawn Behavior**
  - Copper Golems now perform idle walk animation after spawning before checking the chest they spawned on
  - Added initial 140-tick (7 second) transport cooldown when golem spawns
  - Prevents golem from immediately trying to interact with spawn chest
  - Allows idle walk/animation behavior to trigger naturally after spawn


## [1.21.1-0.0.3] - 2025-11-18

### Added
- **Copper Button System**
  - Four oxidation variants: copper_button, exposed_copper_button, weathered_copper_button, oxidized_copper_button
  - Four waxed variants: waxed_copper_button, waxed_exposed_copper_button, waxed_weathered_copper_button, waxed_oxidized_copper_button
  - 15 tick (1.5 seconds) activation time matching wooden buttons
  - Full oxidation progression over time
  - Oxidized buttons are non-functional and play copper hit sound when attempted to use
  - Waxed oxidized buttons remain functional

- **Copper Button Interactions**
  - Axe scraping removes oxidation levels (oxidized → weathered → exposed → copper)
  - Honeycomb application converts to waxed variant (stops oxidation)
  - Axe removes wax from waxed buttons
  - All interactions preserve button state (facing, powered, face position)
  - Proper sound effects (AXE_SCRAPE, HONEYCOMB_WAX_ON, AXE_WAX_OFF, COPPER_HIT)
  - Particle effects for scraping and waxing operations

- **Copper Golem AI: Button Pressing**
  - Copper Golems can now randomly search for and press nearby copper buttons
  - Golems walk at normal speed to buttons, stop completely when arriving, then play pressing animation
  - Custom button-pressing animation with arm and body movement (1 second duration)
  - Animation plays first, button is pressed at peak of animation (0.375s mark)
  - Must be within 0.8 blocks before stopping and starting animation (very close positioning)
  - **Improved Animation Behavior:**
    - Golem stops completely for 5 ticks before starting button press animation
    - Walk and run animations are completely disabled during button pressing
    - Idle animation no longer plays during button press state
    - Press animation plays exclusively without interference from movement animations
    - Animation timing: starts at tick 6, button pressed at tick 14, completes at tick 30
  - Proper stopping mechanism: halts navigation, clears all movement (including Zza), zeroes velocity, and stops walk animation
  - Configurable behavior via config file (enabled by default)
  - Golems search within 16 blocks horizontally and 4 blocks vertically
  - Global cooldown: 5-15 seconds (random) wait time after pressing any button before searching for next
  - Per-button cooldown: 20-40 seconds (random) before the same button can be pressed again
  - Prevents rapid consecutive button presses through timestamp-based tracking
  - Respects oxidized button state (won't press non-waxed oxidized buttons)

- **Configuration System**
  - New config file: `coppergolemlegacy-common.toml`
  - `golemPressesButtons` setting to enable/disable button-pressing AI (default: true)
  - Server/singleplayer controllable

- **Crafting Recipes**
  - Copper buttons craftable from corresponding cut copper blocks (1 cut copper → 1 button)
  - Waxed buttons craftable with button + honeycomb (shapeless)
  - Waxed buttons also craftable directly from waxed cut copper blocks
  - Copper chests craftable from 8 copper blocks in ring pattern
  - All recipes for all oxidation variants

- **Assets & Data**
  - Complete blockstate files for all button variants with all orientations (ceiling, floor, wall)
  - Block and item models using vanilla copper block textures
  - Loot tables for all button and chest variants
  - English translations for all new blocks

- **Creative Tab Integration**
  - All copper buttons added to Redstone Blocks creative tab
  - Both normal and waxed variants included

### Technical
- `CopperButtonBlock` class implementing WeatheringCopper interface
- `WaxedCopperButtonBlock` class for non-oxidizing variants
- `PressRandomCopperButton` AI behavior for button-pressing logic with enhanced stopping mechanism
- `CopperGolemModel` with state-based animation control to prevent walk/run animations during button pressing
- `CopperGolemLegacyConfig` class for mod configuration
- Button reference system linking waxed/unwaxed variants
- FMLCommonSetupEvent listener for button reference initialization
- Enhanced animation state management for cleaner, more realistic button pressing behavior

## [1.21.1-0.0.2] - 2025-11-18

### Fixed
- **Item Sorting Bug**: Fixed Copper Golems skipping slots when placing items in chests
  - Removed duplicate slot increment causing grid pattern (slot filled, slot skipped, slot filled, etc.)
  - Fixed incorrect stack size calculation when stacking items (`spaceLeft` vs `toAdd`)
  - Items now fill chest slots consecutively without gaps

## [1.21.1-0.0.1] - 2025-11-18

### Added
- Initial release of Copper Golem Legacy
- MIT License (Copyright 2025 Marc Schirrmann)
- GitHub repository setup with automated release workflow
- Comprehensive README.md with installation instructions, features, and development guide
- Ko-fi support link for project development funding

### Added
- **Complete Sound System Implementation**
  - Item interaction sounds (item_get, item_no_get, item_drop, item_no_drop) playing at tick 9 with 1.0F volume
  - Chest opening/closing sounds with conditional logic for copper vs regular chests at tick 1 and 60
  - Custom copper chest sounds (copper_chest.open/close) for all oxidation levels
  - Copper statue sounds (hit, break, place, become_statue) for all block interactions
  - Entity sounds for all oxidation levels (death, hurt, step, head_spin) for unaffected, exposed, weathered, and oxidized states
  - Custom `ModSoundTypes.COPPER_STATUE` SoundType for statue blocks
  - All sound events registered in `ModSounds.java` with proper mappings in `sounds.json`

- **Copper Chest System**
  - `CopperChestBlock` with full oxidation support (unaffected, exposed, weathered, oxidized)
  - Copper-to-copper chest mapping system for seamless block conversion
  - Waxing support for all oxidation levels
  - Custom block entity (`CopperChestBlockEntity`) with 27-slot inventory
  - Connection rules for double chest formation
  - Chest renderer (`CopperChestRenderer`) with proper material mappings for all variants
  - Block tag `copper_chests` for copper chest identification

- **Copper Statue System**
  - Four oxidation variants: copper, exposed_copper, weathered_copper, oxidized_copper statues
  - Interactive pose system (standing, running, sitting, star) changeable with empty hand
  - Axe restoration mechanic to convert statues back to golems
  - Custom block entity (`CopperGolemStatueBlockEntity`) storing golem data
  - Statue renderer (`CopperGolemStatueRenderer`) with proper model animations
  - Full sound integration for all interactions

### Changed
- **Sound Volume Optimization**
  - Item interaction sounds increased to 1.0F for better audibility
  - Chest sounds set to 1.0F volume for clear audio feedback
  - Step sounds maintained at 0.15F (standard walking volume)
  - Entity base sound volume set to 0.4F

- **Step Sound Frequency**
  - Overridden `nextStep()` method to return `moveDist + 0.35F` (reduced from default 0.6F)
  - Step sounds now play every step instead of every 2 steps

- **Chest Interaction System**
  - Custom `playChestSound()` method with manual sound playback
  - Conditional sound selection based on `ModTags.Blocks.COPPER_CHESTS`
  - Integrated `blockEvent()` calls for proper chest opening/closing animations
  - Game events (CONTAINER_OPEN/CLOSE) properly triggered

- **Package Structure**
  - Migrated package from `com.example.coppergolemlegacy` to `com.github.smallinger.coppergolemlegacy`
  - Updated all package declarations and imports across 29+ files
  - Maintained proper subpackage organization (block, entity, client, events, ai)

- **Spawning Mechanics**
  - Player-oriented golem and chest spawning (structures face toward player)
  - Precise rotation control using `Direction.fromYRot()` with 180° offset for player-facing orientation
  - Golem positioned 90° clockwise from chest direction
  - All rotation values (yRot, yBodyRot, yHeadRot and old values) set explicitly for consistent orientation

- **Build Configuration**
  - Custom JAR naming format: `modid-mcversion-modversion.jar` (e.g., `coppergolemlegacy-1.21.1-0.0.1.jar`)
  - GitHub Actions workflow for automated release builds
  - Automatic version extraction from gradle.properties
  - JAR artifacts automatically uploaded to GitHub releases

- **Project Documentation**
  - Removed unnecessary config system (mod requires no user configuration)
  - Complete CHANGELOG.md with detailed feature history
  - Professional README.md for GitHub repository
  - MIT License file with proper copyright attribution

### Fixed
- Chest sounds not playing during golem container interactions
- Chest animations not triggering when golem opens/closes chests
- Wrong sounds playing for copper chests (now uses custom copper chest sounds)
- Step sounds only playing every 2 steps (now plays every step)
- Item interaction sounds too quiet (increased to 1.0F volume)
- Package name inconsistencies after refactoring
- Build errors due to old package references in import statements
- Incorrect spawning orientation (chest and golem not facing player)
- Golem spawning facing south regardless of player direction
- Missing rotation values causing inconsistent entity orientation

### Technical Details
- **AI Behavior Integration**: Sound coordination at specific ticks (1, 9, 60) in `CopperGolemAi.java`
- **Entity Animation**: Proper timing between chest operations and sound playback
- **Oxidation System**: Full weathering support for copper chests with visual and audio changes
- **Block Registry**: All blocks registered with proper properties, sounds, and oxidation levels
- **Renderer System**: Custom renderers for chest variants, statue poses, and golem entity
- **CI/CD Pipeline**: GitHub Actions workflow with permissions: contents: write for release automation
- **Item Models**: All 4 copper golem statue variants using `minecraft:item/generated` parent with proper texture paths

### Audio Files Added
- `entity/copper_golem/`: Death, hurt, step, and head_spin sounds for 4 oxidation levels
- `entity/copper_golem/`: Item interaction sounds (item_drop, item_no_drop, no_item_get, no_item_no_get)
- `block/copper_chest/`: Open/close sounds for 4 oxidation levels
- `block/copper_statue/`: Hit, break, place, and become_statue sounds with multiple variants
- Total: 50+ sound files integrated

### Removed
- Config system (Config.java and all related integration)
- Config screen registration from client initialization
- Config translation keys from en_us.json
- Unnecessary template code and example configurations

### Known Issues
- None currently reported

---

## [Unreleased]

### Planned Features
- Additional golem behaviors and interactions
- More statue poses
- Redstone integration capabilities
- Extended oxidation mechanics

---

## Notes
- This mod is for Minecraft 1.21.1 with NeoForge 21.1.215
- Requires Java 21 or higher
- All sounds properly registered and mapped through `ModSounds.java`
- Custom SoundTypes properly implemented for blocks
- Package refactoring completed successfully with all references updated
- Licensed under MIT License (see LICENSE file)
