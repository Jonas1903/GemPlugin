# GemPlugin
Minecraft 1.21.8 Paper Gems Plugin - YouTuber-style Gems System

## Overview
A comprehensive Minecraft plugin that implements a YouTuber-style gems system with unique abilities, cooldowns, and a trust system. Each gem provides passive abilities and a powerful primary ability that can be activated using the offhand swap key (F by default).

## Features

### Core Mechanics
- **Offhand Activation**: Gems only work when held in the offhand
- **One Gem Per Player**: Players can only have one gem in their inventory at a time
- **Primary Abilities**: Activated by pressing the offhand swap key (F)
- **Cooldown Display**: 
  - XP bar shows primary ability cooldown progress
  - Scoreboard timer on the right side of the hotbar
- **Configurable**: All cooldowns and durations can be adjusted in `config.yml`
- **Trust System**: Players can trust others to prevent gem abilities from affecting them
- **Enable/Disable**: Individual gems can be enabled or disabled

## Gems

### 1. Strength Gem (Red)
**Passive Abilities:**
- Permanent Strength II effect
- 1% chance on each hit to deal double damage

**Primary Ability (60s cooldown, 20s duration):**
- Every hit becomes a critical hit

### 2. Fire Gem (Orange)
**Passive Abilities:**
- Permanent Fire Resistance
- 5% chance on hit to ignite target for 3 seconds (bypasses fire resistance)

**Primary Ability (45s cooldown, 15s duration):**
- Creates a 3-block radius fire aura
- Sets enemies on fire (bypasses fire resistance)
- Automatically drains enemy-placed water in the aura
- Does not affect gem holder's water

### 3. Speed Gem (Cyan)
**Passive Abilities:**
- Permanent Speed I
- Permanent Haste III

**Primary Ability (30s cooldown, 15s duration):**
- Grants Haste V

### 4. Puff Gem (White)
**Passive Abilities:**
- Double jump every 5 seconds
- 5% chance to completely negate incoming damage

**Primary Ability (20s cooldown):**
- Dash forward 10 blocks in the direction the player is facing

### 5. Ice Gem (Blue)
**Passive Abilities:**
- Grants Speed IV when touching ice blocks
- Speed IV persists until touching a non-ice block

**Primary Ability (60s cooldown, 20s duration):**
- Creates an indestructible ice cage with 4-block radius
- All enemies inside receive Slowness II
- Cage disappears after 20 seconds

### 6. Invis Gem (Gray)
**Passive Abilities:**
- Permanent Invisibility effect
- Permanent Speed II

**Primary Ability (45s cooldown, 15s duration):**
- Full invisibility mode
- Hides armor and held items
- Complete visual concealment

## Commands

### Operator Commands
- `/gem give <player> <gem_type>` - Give a gem to a player
- `/gem enable <gem_type>` - Enable a specific gem
- `/gem disable <gem_type>` - Disable a specific gem
- `/gem reload` - Reload configuration

**Valid gem types**: `strength`, `fire`, `speed`, `puff`, `ice`, `invis`

### Player Commands
- `/trust <player>` - Trust a player (gem abilities won't affect them)
- `/untrust <player>` - Remove trust from a player

## Permissions
- `gemplugin.admin` - Access to all gem commands (default: op)
- `gemplugin.trust` - Allows players to trust/untrust others (default: true)

## Installation

1. Download the `GemPlugin-1.0.jar` from the releases
2. Place the JAR file in your server's `plugins` folder
3. Start or restart your server
4. The plugin will generate a `config.yml` in the `plugins/GemPlugin` folder
5. Configure cooldowns and durations as desired
6. Use `/gem reload` to apply changes without restarting

## Configuration

The `config.yml` file allows you to customize:
- Cooldowns for all gem abilities (in seconds)
- Durations for timed abilities (in seconds)
- Enable/disable individual gems

Example configuration:
```yaml
cooldowns:
  strength:
    primary: 60
  fire:
    primary: 45
  # ... more settings

durations:
  strength:
    primary: 20
  # ... more settings

enabled-gems:
  strength: true
  fire: true
  # ... more gems
```

## Building from Source

### Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (to download Paper API)

### Build Instructions
```bash
git clone https://github.com/Jonas1903/GemPlugin.git
cd GemPlugin
mvn clean package
```

The compiled JAR will be in the `target` folder as `GemPlugin-1.0.jar`.

## Technical Details

### Project Structure
```
src/main/java/com/jonas/gemplugin/
├── GemPlugin.java (Main class)
├── commands/
│   ├── GemCommand.java
│   ├── TrustCommand.java
│   └── UntrustCommand.java
├── gems/
│   ├── Gem.java (Abstract base class)
│   ├── StrengthGem.java
│   ├── FireGem.java
│   ├── SpeedGem.java
│   ├── PuffGem.java
│   ├── IceGem.java
│   └── InvisGem.java
├── listeners/
│   ├── GemListener.java
│   ├── PlayerListener.java
│   └── InventoryListener.java
├── managers/
│   ├── GemManager.java
│   ├── CooldownManager.java
│   ├── TrustManager.java
│   └── ConfigManager.java
└── utils/
    ├── CooldownDisplay.java
    └── MessageUtils.java
```

### Technologies Used
- **Minecraft Version**: 1.21.8
- **Server Software**: Paper
- **Build System**: Maven
- **Java Version**: 17
- **API**: Paper API 1.21.3-R0.1-SNAPSHOT

## Support

For issues, questions, or feature requests, please open an issue on the [GitHub repository](https://github.com/Jonas1903/GemPlugin/issues).

## License

This project is provided as-is for educational and entertainment purposes.
