# Changelog

All notable changes to this project will be documented in this file.

## [1.21.1] - 0.1.1 - In Development

### Added

#### Copper Horse Armor
- **New armor item**: Copper Horse Armor with 4 protection (between leather 3 and iron 5)
- **Loot generation**: Can be found in dungeon, desert pyramid, jungle temple, nether bridge, stronghold, end city, and village weaponsmith chests
- **Creative tab placement**: Appears after leather horse armor, before iron horse armor

#### FastChest Support (Fabric only)
- **Supported version**: FastChest 1.6 for 1.20.2+
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
- **Copper Nugget Compatibility**: Added tag support for cross-mod compatibility (c:nuggets/copper, c:copper_nuggets) - now works with other mods on both NeoForge and Fabric
- **Copper Golem Crash**: Fixed server crash when Copper Golem tried to evaluate item pickups (missing ATTACK_DAMAGE attribute)
- **Creative Tab Sorting**: Copper tools, armor, and nuggets now appear after their vanilla counterparts (stone tools, chainmail armor, iron nugget) instead of at the bottom of tabs

## [1.21.1] - 0.1.0 - 25.11.2025 Copper Age Backport

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
- NeoForge support added (NeoForge 21.1.115)
- Completely new mod structures
- VanillaBackports now supported