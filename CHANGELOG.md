# Changelog

All notable changes to this project will be documented in this file.

## [1.20.1] - 0.1.4 - In Development

### Added

#### Missing Loot Tables
- **Copper Chain**: Added loot tables for all 8 copper chain variants
- **Copper Bars**: Added loot tables for all 8 copper bars variants
- **Copper Lantern**: Added loot tables for all 8 copper lantern variants
- **Lightning Rod**: Added base lightning_rod loot table

#### Missing Recipes
- **Pale Oak Shelf**: Added recipe with Forge/Fabric load conditions for VanillaBackport compatibility

#### Missing Tags
- **blocks/copper.json**: Tag for copper blocks
- **blocks/copper_chests.json**: Tag for copper chest variants
- **blocks/lightning_rods.json**: Tag for lightning rod variants
- **blocks/wooden_shelves.json**: Tag for wooden shelf variants
- **items/lightning_rods.json**: Item tag for lightning rods
- **items/stripped_pale_oak_log.json**: Item tag for VanillaBackport compatibility

### Fixed

#### Block Mining Tags
- **needs_stone_tool**: Added missing entries for lightning rods, copper lanterns, chains, and trapdoors
- **mineable/pickaxe**: Added copper trapdoors to pickaxe mineable list
- **mineable/axe**: Added pale_oak_shelf (optional) for VanillaBackport compatibility

#### End Flash Consistency
- **LightTextureMixin**: Synchronized End Flash implementation with 1.21.1 version for identical behavior

#### Friends and Foes Compatibility
- **Lightning Rod Oxidation**: Fixed conflict with Friends and Foes mod
  - Lightning Rod now always oxidizes to Copper Age Backport blocks, not F&F blocks
  - Bypasses static WeatheringCopper maps that F&F overrides
  - Direct block setting in randomTick() ensures our oxidation chain is used
  - Added conversion recipe: F&F lightning rods → CAB variants (1:1 crafting)
  - Config screen now shows informational text explaining the mixin situation

### Improved

#### Config Screen UI
- **Group Headers**: Option groups now display their names as headers above the options
- **TextBox Control**: New multi-line text display for informational messages
  - Supports paragraph breaks with proper spacing
  - Dynamic height calculation based on text content
  - Background extends to cover entire text area
- **Compatibility Page**: Added new page explaining mod compatibility details

#### Copper Golem Mod Compatibility
- **Extended container support**: Copper Golem now recognizes all containers that extend `ChestBlock` or `BarrelBlock`
  - Supports Woodworks closets, Quark chests, and any other mod that extends vanilla container classes
  - Previously only recognized exact vanilla chest/barrel blocks by ID
- **Better detection logic**: Changed from `state.is(Blocks.CHEST)` to `instanceof ChestBlock` for proper inheritance support

---

## [1.20.1] - 0.1.3 - 30.11.2025

### Added

#### Copper Trapdoor (Backport from 1.21.10)
- **New block**: Copper Trapdoor with full copper weathering system
- **8 variants**: Unaffected, Exposed, Weathered, Oxidized + Waxed versions of each
- **Weathering**: Copper Trapdoors oxidize over time like other copper blocks
- **Waxing support**: Use honeycomb to prevent oxidation
- **Scraping support**: Use axe to remove wax or revert oxidation
- **Hand-openable**: Unlike iron trapdoors, copper trapdoors can be opened by hand
- **Custom sounds**: Unique copper trapdoor open/close sounds (ported from 1.21.10)

#### End Flash Effect
- **End dimension lighting**: Ported the End Flash visual effect from Minecraft 1.21.10
  - Periodic sky flashes that illuminate the End dimension with a purple tint
  - Directional flash source visible in the sky with additive blending
  - Spatial audio that plays from the flash direction
  - Complete lightmap system that applies purple sky lighting and block brightness boost during flashes
  - Dragon fog compatibility: Flash intensity reduced by 1/3 during boss fight
  - Respects "Hide Lightning Flashes" accessibility option
  - Doesn’t work with shaders because End Flash doesn’t exist in 1.20.1; it was only added in 1.21.4.
  - Iris/OptiFine only provide the endFlashIntensity uniform on 1.21.4+
  
### Fixed

#### Shelf Rendering
- **Banner display**: Banners on shelves are now displayed larger with correct positioning

#### Fabric Disconnect Crash
- **Registry sync crash**: Fixed client crash (NullPointerException in Screen.tick) when disconnecting from a multiplayer server on Fabric
  - Root cause: Fabric's RegistrySyncManager unmaps `minecraft:` namespace entries on disconnect, leaving null references
  - Solution: Cache all `minecraft:` namespace entries during registration and restore them before Fabric's cleanup via Mixin and event hooks

#### Data Load Errors
- **Conditional recipe**: Added Fabric/Forge load conditions to `pale_oak_shelf.json` so it only loads when VanillaBackport is present
- **Recipe filename**: Fixed `waxed_copper_lantern_from_honeycomb_from_honeycomb.json` duplicate naming

#### Copper Horse Armor
- **Texture not loading**: Fixed Copper Horse Armor entity texture not displaying on horses
  - Root cause: Texture path was incorrectly using `copperagebackport:` namespace instead of `minecraft:`
  - Solution: Changed texture lookup to use `minecraft:textures/entity/horse/armor/horse_armor_copper.png`

### Added

#### Copper Golem Statue Comparator Support
- **Comparator output**: Copper Golem Statues now emit a redstone signal when read by a Comparator
- **Signal strength**: Returns 1-4 based on the statue's pose (Standing=1, Running=2, Sitting=3, Star=4)
- **Through-block reading**: Comparators can read the statue's signal through a solid block (matches vanilla 1.21.10 behavior)

#### Mod Compatibility
- **PaleGardenBackport support**: Added `pale_oak_shelf` recipe using `palegardenbackport:stripped_pale_oak_log` when PaleGardenBackport mod is installed
- **Friends and Foes support**: Added conversion recipes to convert Friends and Foes Lightning Rods to CopperAgeBackport Lightning Rods
  - Converts all 7 variants: Exposed, Weathered, Oxidized, and their Waxed versions
- **Lightning Rod Oxidation config**: Added config option to disable Lightning Rod oxidation for compatibility with mods like Friends and Foes
  - Found in Config Menu under "Compatibility" → "Lightning Rod"
- **Amendments support**: Copper Torch now works with Amendments "Torch Holding" animation
  - `CopperTorchBlock` now extends `TorchBlock` for proper mod detection
- **Quark support** (Forge only): Copper Golem can now deposit items into Quark variant chests
  - Quark is only officially available for Forge 1.20.1
  - Supports all wood variant chests (Oak, Spruce, Birch, etc.)
  - Supports special variant chests (Nether Brick, Purpur, Prismarine)
  - Supports both regular and trapped chest variants

---

## [1.20.1] - 0.1.2 - 29.11.2025

### Changed

#### Namespace Migration (Vanilla Backport Preparation) - 1400 file changes
- **Registry namespace changed**: All vanilla backport features now use `minecraft:` namespace instead of `copperagebackport:`
- **Affected items**: Copper Tools, Copper Armor, Copper Nugget, Copper Horse Armor, Spawn Egg
- **Affected blocks**: Copper Chests, Copper Golem Statues, Shelves, Copper Torch, Copper Lanterns, Copper Chains, Copper Bars
- **Affected entities**: Copper Golem
- **Copper Buttons**: Remain at `copperagebackport:` as they are not in vanilla Minecraft 1.21.10
- **Backwards compatibility**: Old worlds with `copperagebackport:` IDs are automatically migrated on load
- **Resource files**: All recipes, loot tables, tags, models, and textures updated to use `minecraft:` namespace

#### Copper Chest Recipe (Vanilla 1.21.10 Style)
- **New recipe**: Copper Ingots surrounding a Chest now craft a Copper Chest (matches vanilla 1.21.10)
- **Removed recipes**: Exposed, Weathered, and Oxidized Copper Chest crafting recipes removed
- **Oxidation only**: Oxidized chest variants can now only be obtained through natural oxidation over time

### Added

#### Lightning Rod Weathering (Backport from 1.21.10)
- **New block**: Lightning Rod with full copper weathering system
- **8 variants**: Unaffected, Exposed, Weathered, Oxidized + Waxed versions of each
- **Weathering**: Lightning Rods oxidize over time like other copper blocks
- **Waxing support**: Use honeycomb to prevent oxidation
- **Scraping support**: Use axe to remove wax or revert oxidation
- **Full functionality**: All variants attract lightning and provide redstone output

#### Copper Armor Trims
- **Smithing Table support**: Copper Armor can now be trimmed in the Smithing Table
- **Darker trim variant**: Copper Trim on Copper Armor uses a darker color palette for visibility (matches vanilla behavior)

#### Mod Support
- **Amendments compatibility**: Copper Lanterns now extend LanternBlock for automatic Amendments support (wall placement, falling behavior)
- **FastChest-Reforged support (Forge)**: Copper Chests now render as static block models when FastChest simplified mode is enabled
- **SophisticatedStorage support (Forge)**: Copper Golem can now use SophisticatedStorage Chests and Barrels as item destinations (works with all tiers: Wood, Copper, Iron, Gold, Diamond, Netherite)
  - Chests play opening/closing animations when accessed by Copper Golem
- **IronChests support (Forge)**: Copper Golem can now use Iron Chests containers as item destinations (works with all types: Copper, Iron, Gold, Diamond, Crystal, Obsidian, Dirt + trapped variants)
  - Chests play opening/closing animations when accessed by Copper Golem
- **ConnectibleChains support (Forge & Fabric)**: Copper Chains can be connected between fences and walls like vanilla Iron Chains

#### Localization Support
- **Crowdin integration**: Added automatic translation download from Crowdin during build process
- **Community translations**: Everyone can help translate the mod at https://crowdin.com/project/copper-golem-legacy
- **Supported languages**: German, Spanish (Spain & Mexico), French, Italian, Japanese, Korean, Dutch, Polish, Portuguese (Brazil & Portugal), Russian, Chinese (Simplified & Traditional), Ukrainian, Swedish, Danish, Finnish, Norwegian, Czech, Hungarian, Turkish, Thai, Vietnamese, Indonesian, Greek, Arabic, Hebrew, Romanian, Bulgarian, Slovak, Slovenian, Croatian, Lithuanian, Latvian, Estonian more can be requested

### Fixed

#### Copper Golem
- **Container selection order**: Fixed Copper Golem selecting containers in wrong order when multiple containers are equidistant. Now correctly matches vanilla 1.21.10 behavior by sorting BlockEntities by `BlockPos.hashCode()` in descending order. This also fixes selection across chunk boundaries.

#### Copper Chest
- **Loot table behavior**: Fixed Copper Chest to match vanilla behavior - chest contents now drop on the ground when mined instead of being stored in the item (like normal chests, not like Shulker Boxes)

#### Copper Button
- **Oxidation while pressed**: Copper Buttons no longer oxidize while they are pressed, preventing them from getting stuck

#### Build System
- **BOM detection and removal**: Added automatic UTF-8 BOM detection and removal task for all mod files across all versions. This prevents encoding issues that could cause compilation problems or runtime errors.


## [1.20.1] - 0.1.1 - 26.11.2025

### Added

#### Copper Horse Armor
- **New armor item**: Copper Horse Armor with 4 protection (between leather 3 and iron 5)
- **Loot generation**: Can be found in dungeon, desert pyramid, jungle temple, nether bridge, stronghold, end city, and village weaponsmith chests
- **Creative tab placement**: Appears after leather horse armor, before iron horse armor

#### FastChest Support (Fabric only)
- **Supported version**: FastChest 1.5 for 1.20.x
- **Optional compatibility**: Copper Chests now render as static block models when FastChest mod is installed
- **Performance boost**: Simplified rendering reduces lag with many chests
- **Item rendering**: Chest items also use static models in inventories and when held

#### Config Menu
- **Copper Golem oxidation time**: The oxidation time of the Copper Golem can now be configured in the mod settings
- **Golem Transport Stack Size**: New config option to set how many items a Copper Golem can transport at once (1-64, default: 16)

#### Copper Torch
- **New light source**: Copper Torch with unique flame particle effects
- **Wall variant**: Can be placed on walls like regular torches
- **Custom particles**: Special copper-colored flame particles

#### Copper Lantern
- **8 variants**: Copper, Exposed, Weathered, Oxidized + Waxed versions
- **Oxidation system**: Lanterns weather over time like other copper blocks
- **Waxing support**: Use honeycomb to prevent oxidation
- **Hanging variant**: Can be placed on ceilings or standing on blocks
- **Light level 15**: Provides maximum light output

#### Copper Chain
- **8 variants**: Copper, Exposed, Weathered, Oxidized + Waxed versions
- **Oxidation system**: Chains weather over time like other copper blocks
- **Waxing support**: Use honeycomb to prevent oxidation
- **Axe scraping**: Remove wax or reduce oxidation with an axe
- **Rotatable**: Can be placed along any axis (X, Y, Z)
- **Waterloggable**: Can be placed in water

#### Copper Bars
- **8 variants**: Copper, Exposed, Weathered, Oxidized + Waxed versions
- **Oxidation system**: Bars weather over time like other copper blocks
- **Waxing support**: Use honeycomb to prevent oxidation
- **Axe scraping**: Remove wax or reduce oxidation with an axe
- **Connecting**: Connects to adjacent bars and solid blocks
- **Waterloggable**: Can be placed in water

### Fixed
- **Copper Tools & Armor**: Fixed stackability - all copper tools and armor are now correctly unstackable (max stack size 1)
- **Copper Nugget Compatibility**: Added tag support for cross-mod compatibility (c:nuggets/copper, c:copper_nuggets) - now works with other mods on both Forge and Fabric
- **Copper Golem Crash**: Fixed server crash when Copper Golem tried to evaluate item pickups (missing ATTACK_DAMAGE attribute)
- **Creative Tab Sorting**: Copper tools, armor, and nuggets now appear after their vanilla counterparts (stone tools, chainmail armor, iron nugget) instead of at the bottom of tabs
- **Copper Bars Textures**: Fixed black/purple texture rendering for Copper Bars (missing template models)

#### Copper Golem Improvements
- **Lightning protection**: Fixed duplicate oxidation removal when struck by lightning
- **Spawn sound**: Copper Golem now plays correct spawn sound instead of Iron Golem repair sound
- **Item throwing**: Items are now thrown towards the player when retrieved with empty hand (like in 1.21.10)
- **Equipment drops**: Improved equipment drop behavior on death and when turning to statue
- **Damage handling**: State reset now only triggers when actually taking damage
- **Shear sound**: Added custom shear sound for removing antenna items
- **Container interaction range**: Reduced from 4.0 to 3.0 to match vanilla behavior
- **Double chest support**: Fixed container tracking for double chests
- **Equipment slot constant**: Added EQUIPMENT_SLOT_ANTENNA constant for consistency
- **Step height**: Added step height of 1.0 (Copper Golem can now step up 1 block)
- **Block spawn particles**: Both pumpkin AND copper block now show break particles when spawning
- **Spawn method**: Added spawn(WeatherState) method matching vanilla CopperGolem API
- **Initial state**: Copper Golem now correctly initializes to IDLE state on construction
- **Sculk sensor detection**: Shearing the Copper Golem now triggers GameEvent.SHEAR
- **Statue transformation**: Fixed execution order - entity is now discarded before playing sound
- **Leash handling**: Leash is now properly dropped when turning to statue
- **Entity loot table**: Drops are now handled via loot table (1-3 copper ingots with looting bonus)
- **Copper Horse Armor texture**: Fixed armor not rendering on horses (texture was looked up in wrong namespace)

#### Config
- **Weathering time tooltip**: Added note that weathering config only affects newly spawned golems (existing golems keep their calculated oxidation time)
- **Fixed tooltip values**: Corrected Minecraft day calculations in config tooltips

## [1.20.1] - 0.1.0 - 25.11.2025 - Copper Age Backport

### Added

#### Shelves
- **12 wood variants**: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove, Cherry, Bamboo, Crimson, Warped, and Pale Oak (with VanillaBackport)
- **Item display**: Place up to 4 items on each shelf
- **Redstone output**: Shelves emit a redstone signal based on stored items
- **Connected textures**: Shelves connect visually when placed side by side

#### Copper Tools & Weapons
- **Full tool set**: Pickaxe, Axe, Shovel, Hoe, and Sword
- **Balanced stats**: Between stone and iron tier
- **Unique sounds**: Custom equip sounds for copper equipment

#### Copper Armor
- **Full armor set**: Helmet, Chestplate, Leggings, and Boots
- **Custom sounds**: Unique equip sounds for copper armor
- **Balanced protection**: Between chainmail and iron tier

### Fixed
- **Pale Oak wouldn't connect to other shelves**
- **Sound glitches after running the GitHub Action**
- **Copper items and armor added to the default Minecraft tag**

#### Other
- Fabric support added (Fabric Loader 0.16.9)
- Forge support added (Forge 47.3.0)
- Completely new mod structures
- VanillaBackports now supported