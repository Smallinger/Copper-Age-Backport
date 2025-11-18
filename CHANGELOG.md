# Changelog

All notable changes to the Copper Golem Legacy mod will be documented in this file.

## [0.0.2] - 2025-01-18

### Fixed
- **Item Sorting Bug**: Fixed Copper Golems skipping slots when placing items in chests
  - Removed duplicate slot increment causing grid pattern (slot filled, slot skipped, slot filled, etc.)
  - Fixed incorrect stack size calculation when stacking items (`spaceLeft` vs `toAdd`)
  - Items now fill chest slots consecutively without gaps

## [0.0.1] - 2025-01-18

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
