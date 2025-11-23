# Changelog

All notable changes to the Copper Golem Legacy mod will be documented in this file.

## [1.21.1-0.0.9-hotfix1] - 23.11.2025

### Fixed
- Critical bug: Copper Golem spawning was broken due to missing block tag definitions
- Added missing `copper.json` tag file defining all copper block variants (normal + oxidized + waxed)
- Added missing `copper_chests.json` tag file defining all copper chest variants
- Added missing `golem_target_chests.json` and `golem_target_barrels.json` tag files for item transport
- Golem spawning now works correctly: Place carved pumpkin on copper block to spawn golem + copper chest

## [1.21.1-0.0.9] - 23.11.2025

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

## [1.21.1-0.0.8] - 22.11.2025

### Fixed
- Recipe format corrected to Minecraft 1.21.1 specification (using `"id"` instead of `"item"` in result field)

## [1.21.1-0.0.7] - 22.11.2025

### Fixed
- Waxed Copper Chests are now recognized by Copper Golem as valid source containers
- Enclosed barrels with only front face exposed are now properly accessible by Copper Golem
- Container-specific blocking checks: Chests check top space, Barrels check facing direction

### Added
- Separate `golem_target_barrels` tag for modpack-friendly barrel variants
- All 4 waxed Copper Chest variants added to `copper_chests` tag:
  - `waxed_copper_chest`
  - `waxed_exposed_copper_chest`
  - `waxed_weathered_copper_chest`
  - `waxed_oxidized_copper_chest`

### Changed
- Improved container blocking logic: Barrels only need facing side clear, not all sides
- Barrel moved from `golem_target_chests` to separate `golem_target_barrels` tag
- Sound system now uses barrel tag instead of hardcoded block check

## [1.21.1-0.0.6] - 22.11.2025

### Added
- 3D Item Rendering for Copper Chests and Copper Golem Statues in inventory, hand, and item slot
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
- Chests no longer oxidize while open (prevents item loss)
- Chests no longer accept scraping/waxing while open
- Double chest oxidation now updates both halves simultaneously
- Particles and sounds now appear on both halves of double chests
- Chest opening with axe/honeycomb after full descraping/dewaxing
- Statue revival with axe prioritizes scraping/dewaxing over revival when applicable

## [1.21.1-0.0.5-hotfix1] - 2025-11-20

### Fixed
- Tag loading error for optional mod chests (IronChest) when mod not installed

## [1.21.1-0.0.5] - 2025-11-20

### Added
- Tag-based mod compatibility system for chest interactions
- Barrel support for item transport
- IronChest mod integration (14 variants)
- Documentation: CHEST_COMPATIBILITY.md and datapack examples
- Container-type detection for proper sounds (copper chests, barrels, regular chests)

### Fixed
- Container detection now supports all container types (barrels, mod chests)
- Container open/close animations for barrels and mod chests

## [1.21.1-0.0.4] - 2025-11-19

### Added
- Initial 140-tick (7 second) transport cooldown when golem spawns

### Changed
- Reduced Copper Golem width from 0.7 to 0.6 blocks (fits through doors more easily)

### Fixed
- Vertical distance detection for chest interactions (increased vertical reach from 0.5 to match horizontal)
- Spawn behavior: Golems now perform idle walk animation before checking spawn chest


## [1.21.1-0.0.3] - 2025-11-18

### Added
- Copper Button System (4 oxidation variants + 4 waxed variants)
- Copper Button interactions: axe scraping, honeycomb waxing with particles and sounds
- Copper Golem AI: Button pressing behavior with custom animations
- Configuration system for button-pressing behavior
- Crafting recipes for copper buttons and waxed variants
- Creative tab integration for all copper buttons

### Fixed
- Improved button press animation (golem stops completely, no walk/run animation interference)

## [1.21.1-0.0.2] - 2025-11-18

### Fixed
- Item sorting: Copper Golems no longer skip slots when placing items in chests

## [1.21.1-0.0.1] - 2025-11-18

### Added
- Initial release of Copper Golem Legacy for Minecraft 1.21.1 with NeoForge
- Complete sound system (item interactions, chest sounds, statue sounds, entity sounds)
- Copper Chest System (4 oxidation variants with waxing support)
- Copper Statue System (4 oxidation variants with 4 poses)
- Copper Golem entity with item transport behavior
- Custom block entities and renderers
- GitHub repository with automated release workflow
- MIT License (Copyright 2025 Marc Schirrmann)

### Changed
- Package structure migrated to com.github.smallinger.coppergolemlegacy
- Player-oriented spawning mechanics (structures face toward player)
- Custom JAR naming format (modid-mcversion-modversion.jar)

### Fixed
- Chest sounds and animations now working properly
- Step sounds now play every step (instead of every 2 steps)
- Spawning orientation corrected (chest and golem face player)

### Removed
- Config system (no user configuration needed)
