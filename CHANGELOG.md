# Changelog - Forge 1.20.1 Port


## [1.20.1-0.0.9-hotfix1] - 23.11.2025

### Fixed
- Copper Golem Spawn Egg model now uses correct template_spawn_egg parent instead of generated item model

## [1.20.1-0.0.9] - 23.11.2025

### Added
- Mining support for Copper Chests and Copper Golem Statues with pickaxe
- Block tags for mineable/pickaxe and needs_stone_tool for all chest and statue variants
- Pose preservation: Statue items now retain their pose in inventory, GUIs, and item frames
- Block state component rendering for statue items

### Fixed
- Copper Chests and Copper Golem Statues can now be mined and harvested with correct tool (stone pickaxe or better)
- Copper Golem Statue items now display the correct pose in all contexts (inventory, item frames, GUIs)
- Item renderer now reads DataComponents.BLOCK_STATE from dropped statue items
- Loot tables correctly apply minecraft:copy_state for pose property
- Item frame rendering: Copper Chests and Copper Golem Statues now display correctly without offset issues

## [1.20.1-0.0.8] - 2025-11-22

### Fixed
- Recipe format corrected from 1.20.5+ format to 1.20.1 format (changed `"id"` to `"item"` in result objects)
- Added missing `"count": 1` to Golem Statue waxing recipes

## [1.20.1-0.0.7] - 2025-11-22

### Fixed
- Waxed Copper Chests are now recognized by Copper Golems as source containers
- Barrels surrounded by blocks (only front face accessible) are now correctly detected by Copper Golems

### Added
- Separate tag system for container types: `golem_target_chests` and `golem_target_barrels`
- Tag-based barrel detection for modpack compatibility
- Container-specific blocking checks (chests need space above, barrels only need front face free)
- Copper Golem AI now accepts both chest and barrel tags as valid target containers
- Automatic sound detection based on container type (Copper Chest, Barrel, or Regular Chest sounds)
- Spawn Egg now uses color-based rendering (`0xB87333` and `0x48D1CC`) instead of texture files

### Changed
- Updated `CHEST_COMPATIBILITY.md` with separate documentation for chests and barrels
- Updated datapack example to include `golem_target_barrels.json`
- Improved barrel accessibility logic for better pathfinding in tight spaces

## [1.20.1-0.0.6] - 2025-11-22

### Added
- 3D Item Rendering for Copper Chests and Copper Golem Statues in inventory, hand, and item frames
- Copper Chest Oxidation through 4 stages (Copper → Exposed → Weathered → Oxidized)
- Copper Chest weathering mechanics: scraping with axe removes oxidation stages
- Copper Chest waxing with honeycomb prevents oxidation
- Waxed Copper Chests (4 variants) that prevent oxidation, craftable with honeycomb
- Waxed Copper Chest dewaxing with axe
- Copper Button oxidation, scraping, and waxing support
- Copper Golem Statue Oxidation through 4 stages
- Copper Golem Statue scraping and waxing support
- Waxed Copper Golem Statues (4 variants), craftable with honeycomb, still revivable with axe
- Waxed Copper Golem Statue dewaxing with axe (statue remains revivable)
- Double Chest Oxidation Sync to prevent separation
- Double Chest atomic updates to prevent separation during oxidation
- Item preservation during chest oxidation - items no longer drop or duplicate
- Durability damage for axes when scraping/dewaxing (2x for double chests)
- Interaction System for honeycomb waxing and axe scraping with particles and sounds
- `WeatheringHelper` utility class with vanilla oxidation probability (0.05688889)

### Fixed
- Copper Golem become_statue sound now uses existing block sounds
- Copper Golem now drops held items when turning into statue
- Waxed Copper Chests and Statues now appear in Creative Mode tab
- Waxed Copper Chests now render with correct copper textures
- Build compatibility with Forge 1.20.1 API
- Chests no longer oxidize while open (prevents item loss)
- Chests no longer accept scraping/waxing while open
- Double chest oxidation now updates both halves simultaneously
- Particles and sounds now appear on both halves of double chests
- Chest opening with axe/honeycomb after full descraping/dewaxing
- Statue revival with axe prioritizes scraping/dewaxing over revival when applicable

## [1.20.1-0.0.5-hotfix1] - 2025-11-20

### Fixed
- Tag loading error for optional mod chests (IronChest, Ars Nouveau) when mods not installed

## [1.20.1-0.0.5] - 2025-11-20

### Added
- Tag-based mod compatibility system for chest interactions
- Barrel support for item transport
- IronChest mod integration (14 variants)
- Documentation: CHEST_COMPATIBILITY.md and datapack examples
- Container-type detection for proper sounds (copper chests, barrels, regular chests)
- Config screen accessible via Mods menu

### Fixed
- Server crash from client-only imports in config file
- Container detection now supports all container types (barrels, mod chests)
- Container open/close animations for barrels and mod chests

## [1.20.1-0.0.4] - 2025-11-19

### Added
- Complete port from NeoForge 1.21.1 to Forge 1.20.1 (Minecraft 1.20.1)
- Item transport behavior for Copper Golems (transports items between copper chests and regular chests)
- Button press behavior with interruption protection
- Memory system for behavior coordination
- Config screen for mod settings

### Removed
- MapCodec system (doesn't exist in Forge 1.20.1)
- DataComponents API (1.21+ only)
- StreamCodec (1.21+ only)

### Fixed
- All compilation errors for Forge 1.20.1 API compatibility
- Constructor injection incompatibility
- Missing pack.mcmeta and block tags
