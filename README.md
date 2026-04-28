# MinicraftLobbyTreasure

A Minecraft Spigot plugin designed for lobby environments that allows players to find hidden treasures represented by signs.

## Features

- Custom treasure creation using signs.
- Three difficulty levels: Easy, Normal, and Hard.
- Reward system via console commands (supports any economy or item plugin).
- Interactive GUI to track found treasures.
- Support for both SQLite and MySQL databases.
- PlaceholderAPI support.
- Fully translatable messages.

## How it works

1. **Creating a Treasure:**
   - Place a sign and type `[treasure]` on the first line.
   - On the second line, type the difficulty level as a number: `1` (Easy), `2` (Normal), or `3` (Hard).
   - (Optional) On the third line, you can specify a unique ID for the treasure. If left empty, an ID will be assigned automatically.
   - Once placed, the sign will automatically format itself according to the configuration.

2. **Finding Treasures:**
   - Players find the hidden signs and right-click them.
   - The plugin checks if the player has already found this specific treasure.
   - If not, it executes the configured reward commands and marks the treasure as found in the database.

3. **Tracking Progress:**
   - Players can use `/mctreasure gui` to open a menu showing which treasures they have found and which are still missing.

## Commands

- `/mctreasure gui` - Opens the treasure tracking menu.
- `/mctreasure gui [player]` - Opens another player's treasure menu (Admin).
- `/mctreasure tp <ID>` - Teleports to a specific treasure (Admin).
- `/mctreasure confirm` - Confirms the deletion of a treasure sign after breaking it (Admin).

**Note:** `/mcpoklad`, `/poklad`, and `/treasure` can be used as aliases.

## Permissions

- `mctreasure.admin` - Access to all administrative features, including creating/deleting signs and teleporting.

## Placeholders

Requires PlaceholderAPI:
- `%mctreasure_found_count%` - Number of treasures found by the player.
- `%mctreasure_total_count%` - Total number of treasures available on the server.

## Installation

1. Place the JAR file in your `plugins` folder.
2. Restart the server to generate the configuration files.
3. Configure your rewards and database settings in `config.yml`.
4. Translate or customize messages in `lang.yml`.
5. Restart or reload the plugin.

## License

This project is licensed under the MIT License - see the LICENSE file for details.