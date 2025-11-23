# Copper Golem Legacy

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.215-blue.svg)](https://neoforged.net/)
[![Discord](https://img.shields.io/discord/1441831886719811654?label=Discord&logo=discord&color=5865F2)](https://discord.gg/hGrWUW9vSb)
[![Crowdin](https://badges.crowdin.net/copper-golem-legacy/localized.svg)](https://crowdin.com/project/copper-golem-legacy)

![Demo](./src/main/resources/logo.png)

A Minecraft mod that brings back the Copper Golem from the Minecraft Live 2021 mob vote. This lovable copper companion oxidizes over time, can hold items, press copper buttons, and eventually becomes a statue.

![transport](./.github/media/transport.gif)

## Support

If you enjoy this mod and would like to support further development, consider buying me a coffee! Your support helps keep the project alive and encourages new features and updates.

By supporting the mod, you get **direct access to all new Preview versions** as they are released for **Fabric, Forge, and NeoForge**, letting you test new features early, report bugs, and help shape the mod.  

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/smallinger)

### Localization

Anyone can help with translations! By contributing, you make the mod accessible to players all around the world.  
You can submit translations here: [Crowdin](https://crowdin.com/project/copper-golem-legacy)

## Features

### Copper Golem Entity
- **Natural spawning**: Place a carved pumpkin on a copper block to summon your golem
- **Oxidation system**: Copper golems age through four stages (normal, exposed, weathered, oxidized)
- **Item interactions**: Give items to your golem - they'll store them in their chest and show them off
- **Button pressing**: Golems randomly press copper buttons, acting as natural randomizers
- **Item transport**: Golems can transport items from Copper Chests to other chests (see [Chest Compatibility](CHEST_COMPATIBILITY.md))
- **Player-oriented spawning**: Golems and their chests face toward you when spawned

### Copper Chest
- **Four oxidation levels**: Copper chests age naturally alongside your golem
- **Double chest support**: Combine two copper chests for double storage
- **Copper-specific sounds**: Each oxidation level has unique opening and closing sounds
- **Waxing support**: Preserve your chest's oxidation level with honeycomb

### Copper Buttons
- **8 variants**: (4 oxidation stages + waxed versions) with 1.5s activation time
- **Button Interactions**: Scrape with axe, wax with honeycomb, oxidized buttons won't work (unless waxed)
- **Golem Button AI**: Golems now intelligently search for and press nearby copper buttons

### Copper Statue
- **Four poses**: Standing, running, sitting, and star pose
- **Interactive**: Right-click with an empty hand to cycle through poses
- **Restoration**: Use an axe to bring oxidized statues back to life as golems
- **Custom sounds**: Unique audio for placing, breaking, and interacting

### Audio Design
- **50+ custom sounds**: Complete sound system for entities, blocks, and interactions
- **Oxidation-specific audio**: Different sounds for each weathering stage
- **Item interaction feedback**: Audio cues when golems pick up, drop, or interact with items

### Configurable
- **Enable/disable**: Golem button pressing in config file

## Spawn
![spawn](./.github/media/spawn.gif)
- Place a carved pumpkin on a copper block

## Golem Button press
![button2](./.github/media/button-press2.gif)
![button1](./.github/media/button-press1.gif)
- Golems now intelligently search for and press nearby copper buttons with animation.
- This feature can be turned off in the configuration menu or via the server configuration.

## Installation

1. Download the latest release from the [Releases](https://github.com/Smallinger/Copper-Golem-Legacy/releases) page
2. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1
3. Place the downloaded `.jar` file in your `mods` folder
4. Launch Minecraft and enjoy!

## Building from Source

```bash
# Clone the repository
git clone https://github.com/Smallinger/Copper-Golem-Legacy.git
cd Copper-Golem-Legacy

# Build the mod
.\gradlew build

# The compiled JAR will be in build/libs/
```

## Development

### Requirements
- Java 21 or higher
- Gradle (included via wrapper)
- NeoForge 21.1.215 for Minecraft 1.21.1

### Setup
```bash
# Refresh dependencies
.\gradlew --refresh-dependencies

# Run the client
.\gradlew runClient

# Run the server
.\gradlew runServer
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Copyright (c) 2025 Marc Schirrmann

## Credits

- **Author**: Marc Schirrmann (Smallinger)
- **Inspired by**: The Copper Golem from Minecraft Live 2021 mob vote
- **Built with**: [NeoForge](https://neoforged.net/)

## Links

- [GitHub Repository](https://github.com/Smallinger/Copper-Golem-Legacy)
- [Issue Tracker](https://github.com/Smallinger/Copper-Golem-Legacy/issues)
- [Changelog](CHANGELOG.md)

## Support Development

If you encounter any issues or have questions, please open an issue on the [GitHub issue tracker](https://github.com/Smallinger/Copper-Golem-Legacy/issues).
