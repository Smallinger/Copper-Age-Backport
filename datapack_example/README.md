# Example: Datapack for Extended Container Compatibility

This folder shows how players or modpack creators can add additional containers via datapack without modifying the mod.

## Structure
```
datapacks/
  copper_golem_extended_containers/
    pack.mcmeta
    data/
      coppergolemlegacy/
        tags/
          block/
            golem_target_chests.json
            golem_target_barrels.json
```

## Available Tags

### `golem_target_chests.json`
For chest-like containers that need space above to open.

### `golem_target_barrels.json`
For barrel-like containers that only need the front face accessible.

## Usage Examples

### Example 1: Adding Storage Drawers (Chests)
```json
{
  "replace": false,
  "values": [
    {"id": "storagedrawers:oak_full_drawers_1", "required": false},
    {"id": "storagedrawers:oak_full_drawers_2", "required": false},
    {"id": "storagedrawers:oak_full_drawers_4", "required": false}
  ]
}
```

### Example 2: Adding Sophisticated Storage
**For Chests** (`golem_target_chests.json`):
```json
{
  "replace": false,
  "values": [
    {"id": "sophisticatedstorage:chest", "required": false},
    {"id": "sophisticatedstorage:limited_chest", "required": false}
  ]
}
```

**For Barrels** (`golem_target_barrels.json`):
```json
{
  "replace": false,
  "values": [
    {"id": "sophisticatedstorage:barrel", "required": false},
    {"id": "sophisticatedstorage:limited_barrel", "required": false}
  ]
}
```

### Example 3: Combining Multiple Mods
**golem_target_chests.json:**
```json
{
  "replace": false,
  "values": [
    {"id": "storagedrawers:oak_full_drawers_1", "required": false},
    {"id": "sophisticatedstorage:chest", "required": false},
    {"id": "ae2:chest", "required": false}
  ]
}
```

## Installation for Players

1. Create the folder `.minecraft/saves/<your_world_name>/datapacks/copper_golem_extended_chests/`
2. Copy the `pack.mcmeta` and `data/` structure into it
3. Restart the world or use `/reload`
4. The Copper Golem can now place items into these chests!

**Important**: Always set `"replace": false`, otherwise the default chests (Vanilla + IronChest) will be overwritten!

**Note**: Use `"required": false` for optional mod compatibility. This prevents errors if the referenced mod is not installed.