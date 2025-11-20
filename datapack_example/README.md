# Example: Datapack for Extended Chest Compatibility

This folder shows how players or modpack creators can add additional chests via datapack without modifying the mod.

## Structure
```
datapacks/
  copper_golem_extended_chests/
    pack.mcmeta
    data/
      coppergolemlegacy/
        tags/
          block/
            golem_target_chests.json
```

## Usage Examples

### Example 1: Adding Storage Drawers
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
```json
{
  "replace": false,
  "values": [
    {"id": "sophisticatedstorage:barrel", "required": false},
    {"id": "sophisticatedstorage:chest", "required": false},
    {"id": "sophisticatedstorage:limited_barrel", "required": false}
  ]
}
```

### Example 3: Combining Multiple Mods
```json
{
  "replace": false,
  "values": [
    {"id": "storagedrawers:oak_full_drawers_1", "required": false},
    {"id": "sophisticatedstorage:barrel", "required": false},
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
