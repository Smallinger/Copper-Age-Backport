# Copper Golem Chest Compatibility

## Overview

The Copper Golem uses a flexible system for chest interactions:
- **Source Chests**: Only **Copper Chests**
- **Target Chests**: **Tag-based expandable** - defines which chests the golem can place items into

## Tags
### `golem_target_chests`
Defines which chests the golem can place items into.

**File**: `src/main/resources/data/coppergolemlegacy/tags/blocks/golem_target_chests.json`

```json
{
  "replace": false,
  "values": [
    "minecraft:chest",
    "minecraft:trapped_chest",
    "minecraft:barrel",
    {
      "id": "ironchest:iron_chest",
      "required": false
    },
    {
      "id": "ironchest:gold_chest",
      "required": false
    },
    {
      "id": "ironchest:diamond_chest",
      "required": false
    },
    {
      "id": "ironchest:copper_chest",
      "required": false
    },
    {
      "id": "ironchest:crystal_chest",
      "required": false
    },
    {
      "id": "ironchest:obsidian_chest",
      "required": false
    },
    {
      "id": "ironchest:dirt_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_iron_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_gold_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_diamond_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_copper_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_crystal_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_obsidian_chest",
      "required": false
    },
    {
      "id": "ironchest:trapped_dirt_chest",
      "required": false
    },
    {
      "id": "ars_nouveau:archwood_chest",
      "required": false
    },
    {
      "id": "ars_nouveau:repository",
      "required": false
    }
  ]
}
```

**Default**: 
- Minecraft Vanilla Chests (normal & trapped)
- Minecraft Barrel
- IronChest Mod (all 7 chest types + trapped variants)

## Adding New Mods

### Method 1: Individual Blocks
Add individual block IDs directly:

```json
{
  "replace": false,
  "values": [
    "minecraft:chest",
    "minecraft:trapped_chest",
    "minecraft:barrel",
    {
      "id": "othermod:special_chest",
      "required": false
    },
    {
      "id": "othermod:large_chest",
      "required": false
    }
  ]
}
```

**Note**: Use `"required": false` for optional mod compatibility.

### Method 2: Using Mod Tags
If another mod already defines tags for their chests, use those:

```json
{
  "replace": false,
  "values": [
    "minecraft:chest",
    "minecraft:trapped_chest",
    "minecraft:barrel",
    {
      "id": "#othermod:chests",
      "required": false
    }
  ]
}
```

**Note**: Tag references with `#` can also be optional.

### Method 3: Datapack
Players can also add chests via datapack without modifying the mod:

1. Create a datapack in `saves/<worldname>/datapacks/my_chest_compatibility/`
2. Add: `data/coppergolemlegacy/tags/blocks/golem_target_chests.json`
3. Content:
```json
{
  "replace": false,
  "values": [
    {
      "id": "my_mod:my_chest",
      "required": false
    }
  ]
}
```

**Important**: 
- `"replace": false` - Keeps default values
- `"required": false` - Optional mod dependency (won't error if mod is not installed)

## Examples for Other Mods

### Storage Drawers
```json
{
  "id": "#storagedrawers:drawers",
  "required": false
}
```

### Sophisticated Storage
```json
{
  "id": "#sophisticatedstorage:barrels",
  "required": false
},
{
  "id": "#sophisticatedstorage:chests",
  "required": false
}
```

### Applied Energistics 2
```json
{
  "id": "ae2:chest",
  "required": false
}
```

## Developer Notes

- Target tag is defined in `ModTags.java` (`GOLEM_TARGET_CHESTS`)
- The AI logic uses the tag in `CopperGolemAi.java` (line ~111)
- Target is expandable via tag
- Implementation is performance-optimized through tag caching

## Behavior

1. The golem searches within a radius of 32 blocks (horizontal) and 8 blocks (vertical)
2. It takes items **only** from Copper Chests (hardcoded, not changeable)
3. It places items in **all** Target Chests (expandable via tag: Vanilla Chests, Barrels, IronChest, etc.)
4. Automatically supports all oxidation levels of Copper Chests
5. Works with trapped/regular variants
6. Plays correct sounds for each container type (Chest/Barrel/Copper Chest)
