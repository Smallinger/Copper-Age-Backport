# Copper Golem Container Compatibility

## Overview

The Copper Golem uses a flexible tag-based system for container interactions:
- **Source Containers**: Only **Copper Chests** (all oxidation levels + waxed variants)
- **Target Containers**: **Tag-based expandable** - defines which containers the golem can place items into
  - Chests via `golem_target_chests` tag
  - Barrels via `golem_target_barrels` tag

## Tags

### `golem_target_chests`
Defines which chests the golem can place items into.

**File**: `src/main/resources/data/coppergolemlegacy/tags/block/golem_target_chests.json`

```json
{
  "replace": false,
  "values": [
    "minecraft:chest",
    "minecraft:trapped_chest",
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
- IronChest Mod (all 7 chest types + trapped variants)
- Ars Nouveau chests

### `golem_target_barrels`
Defines which barrels the golem can place items into.

**File**: `src/main/resources/data/coppergolemlegacy/tags/block/golem_target_barrels.json`

```json
{
  "replace": false,
  "values": [
    "minecraft:barrel"
  ]
}
```

**Default**: 
- Minecraft Vanilla Barrel

**Note**: Barrels have special handling - they only need their front face (FACING direction) to be accessible, unlike chests which need space above.

## Adding New Mods

### Method 1: Individual Blocks
Add individual block IDs directly to the appropriate tag:

**For Chests** (`golem_target_chests.json`):
```json
{
  "replace": false,
  "values": [
    "minecraft:chest",
    "minecraft:trapped_chest",
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

**For Barrels** (`golem_target_barrels.json`):
```json
{
  "replace": false,
  "values": [
    "minecraft:barrel",
    {
      "id": "othermod:special_barrel",
      "required": false
    },
    {
      "id": "othermod:large_barrel",
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
Players can also add containers via datapack without modifying the mod:

**For Chests:**
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

**For Barrels:**
1. Create a datapack in `saves/<worldname>/datapacks/my_barrel_compatibility/`
2. Add: `data/coppergolemlegacy/tags/blocks/golem_target_barrels.json`
3. Content:
```json
{
  "replace": false,
  "values": [
    {
      "id": "my_mod:my_barrel",
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

- Target tags are defined in `ModTags.java` (`GOLEM_TARGET_CHESTS` and `GOLEM_TARGET_BARRELS`)
- The AI logic uses both tags in `CopperGolemAi.java` (line ~111): `state.is(GOLEM_TARGET_CHESTS) || state.is(GOLEM_TARGET_BARRELS)`
- Targets are expandable via tags
- Implementation is performance-optimized through tag caching
- Sound system automatically detects container type:
  - Copper Chests → Copper Chest sound
  - Barrels in `golem_target_barrels` → Barrel sound
  - Chests in `golem_target_chests` → Chest sound

## Behavior

1. The golem searches within a radius of 32 blocks (horizontal) and 8 blocks (vertical)
2. It takes items **only** from Copper Chests (all oxidation levels + waxed variants)
3. It places items in **all** Target Containers:
   - All chests in `golem_target_chests` tag (Vanilla Chests, IronChest, etc.)
   - All barrels in `golem_target_barrels` tag (Vanilla Barrels + modded)
4. Container-specific blocking checks:
   - **Chests**: Need free space above to open
   - **Barrels**: Only need front face (FACING direction) accessible
   - **Copper Chests**: No blocking check needed
5. Automatically supports all oxidation levels of Copper Chests
6. Works with trapped/regular variants
7. Plays correct sounds for each container type