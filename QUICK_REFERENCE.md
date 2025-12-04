# GemPlugin Quick Reference Card

## Gem Overview

| Gem | Material | Passive | Primary (Cooldown) |
|-----|----------|---------|-------------------|
| **Strength** (Red) | Redstone | Strength II, 1% double damage | Critical hits (60s) |
| **Fire** (Orange) | Blaze Powder | Fire Resistance, 5% ignite | Fire aura 3 blocks (45s) |
| **Speed** (Cyan) | Sugar | Speed I, Haste III | Haste V (30s) |
| **Puff** (White) | Feather | Double jump (5s), 5% dodge | Dash 10 blocks (20s) |
| **Ice** (Blue) | Ice | Speed IV on ice | Ice cage 4 blocks (60s) |
| **Invis** (Gray) | Phantom Membrane | Invisibility, Speed II | Full invisibility (45s) |

## Commands

### Operator Commands
```
/gem give <player> <type>     Give a gem
/gem enable <type>             Enable a gem
/gem disable <type>            Disable a gem
/gem reload                    Reload config
```

### Player Commands
```
/trust <player>                Trust a player
/untrust <player>              Remove trust
```

## Usage Flow

1. **Get Gem** → `/gem give <player> <type>`
2. **Equip** → Place in offhand (F key)
3. **Passive** → Automatic when in offhand
4. **Primary** → Press F to activate
5. **Cooldown** → Watch XP bar and scoreboard

## Gem Types
- `strength` `fire` `speed` `puff` `ice` `invis`

## Key Mechanics

- ✅ **Works in offhand only**
- ✅ **One gem per player**
- ✅ **Trust system prevents friendly fire**
- ✅ **XP bar shows cooldown**
- ✅ **Scoreboard shows timer**
- ✅ **All values configurable**

## Default Cooldowns

| Gem | Primary CD | Duration | Special CD |
|-----|-----------|----------|------------|
| Strength | 60s | 20s | - |
| Fire | 45s | 15s | - |
| Speed | 30s | 15s | - |
| Puff | 20s | Instant | 5s (double jump) |
| Ice | 60s | 20s | - |
| Invis | 45s | 15s | - |

## Configuration File

Location: `plugins/GemPlugin/config.yml`

```yaml
cooldowns:
  gem_name:
    primary: 60
    
durations:
  gem_name:
    primary: 20
    
enabled-gems:
  gem_name: true
```

## Trust System

- Prevents gem effects on trusted players
- Two-way trust recommended for teams
- Stored in `plugins/GemPlugin/trusts.yml`

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Not working | Check offhand + enabled |
| Won't activate | Check cooldown (XP bar) |
| Can't get 2nd gem | Intentional (1 per player) |
| Effects not applying | Move to offhand |

## Tips

### Combat
- **Strength**: Direct DPS
- **Fire**: Area control
- **Puff**: Mobility
- **Ice**: Crowd control

### Utility  
- **Speed**: Mining/Building
- **Invis**: Stealth
- **Ice**: Travel (ice highways)

### Team Play
- Use `/trust` for teammates
- Coordinate gem selections
- Share resources based on gems

## Permissions

- `gemplugin.admin` - Admin commands (default: op)
- `gemplugin.trust` - Trust commands (default: all)

---

**Version:** 1.0  
**Minecraft:** 1.21.8  
**Platform:** Paper
