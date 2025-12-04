# GemPlugin Usage Guide

This guide provides detailed instructions on how to use the GemPlugin on your Minecraft server.

## Quick Start

1. **Installation**: Place `GemPlugin-1.0.jar` in your server's `plugins` folder and restart
2. **Give a gem**: `/gem give <player> strength`
3. **Hold in offhand**: Move the gem to your offhand slot
4. **Use abilities**: Press F to activate primary abilities

## For Server Operators

### Giving Gems to Players

Use the `/gem give` command to give gems to players:

```
/gem give Steve strength
/gem give Alex fire
/gem give Notch puff
```

Valid gem types:
- `strength` - Red gem with combat bonuses
- `fire` - Orange gem with fire abilities
- `speed` - Cyan gem with speed/haste buffs
- `puff` - White gem with mobility abilities
- `ice` - Blue gem with ice-based powers
- `invis` - Gray gem with invisibility

### Managing Gems

Enable or disable specific gems:
```
/gem disable fire    # Disables the fire gem
/gem enable fire     # Re-enables the fire gem
```

Reload configuration without restarting:
```
/gem reload
```

### Configuring Cooldowns and Durations

Edit `plugins/GemPlugin/config.yml`:

```yaml
cooldowns:
  strength:
    primary: 60      # 60 seconds cooldown
  fire:
    primary: 45      # 45 seconds cooldown
  puff:
    double-jump: 5   # 5 seconds between double jumps
    primary: 20      # 20 seconds for dash

durations:
  strength:
    primary: 20      # Critical mode lasts 20 seconds
  fire:
    primary: 15      # Fire aura lasts 15 seconds
```

After editing, use `/gem reload` to apply changes.

## For Players

### Basic Usage

1. **Receive a gem** from an operator or find one in-game
2. **Place it in your offhand** (default: F key to swap)
3. **Passive abilities activate automatically** when in offhand
4. **Press F to activate primary ability** (same key that swaps items)

### One Gem Limit

You can only have **one gem** at a time. If you pick up a second gem, it will be automatically removed from your inventory.

### Trust System

Protect your friends from your gem's abilities:

```
/trust Steve       # Trust Steve (your abilities won't affect him)
/untrust Steve     # Remove trust from Steve
```

Benefits of trusting:
- Your attacks won't trigger gem effects on trusted players
- Trusted players won't be affected by your area abilities
- Fire aura, ice cage, and other effects skip trusted players

### Understanding Cooldowns

When you use a primary ability:
- **XP Bar**: Shows cooldown progress (fills up as cooldown decreases)
- **Level Number**: Shows seconds remaining on cooldown
- **Scoreboard**: Displays gem name and cooldown time on the right side

## Gem-Specific Tips

### Strength Gem
- Keep it in offhand during combat for Strength II
- Watch for the 1% double damage proc
- Save primary ability for important fights (critical hits on every attack)
- 60-second cooldown, so use wisely

### Fire Gem
- Great for area control with the fire aura
- Fire resistance lets you walk through lava safely
- Primary ability drains water in a 3-block radius
- Use to counter water-based defenses

### Speed Gem
- Always active Speed I and Haste III for mining/building
- Primary gives Haste V for 15 seconds
- Good for quick mining sessions
- Short 30-second cooldown allows frequent use

### Puff Gem
- Double jump every 5 seconds for mobility
- 5% chance to completely avoid damage
- Dash ability propels you forward 10 blocks
- Great for escaping or closing distance in combat

### Ice Gem
- Walk on ice for Speed IV boost
- Speed persists until you step off ice
- Create ice platforms for continuous speed
- Primary traps enemies in an ice cage
- Enemies inside get Slowness II

### Invis Gem
- Permanent invisibility and Speed II
- Primary ability hides your armor and items too
- Perfect for stealth operations
- 15-second full invisibility for critical moments

## Troubleshooting

**Gem abilities not working?**
- Make sure the gem is in your offhand
- Check if the gem is enabled: `/gem enable <type>`
- Verify you're not on cooldown (check XP bar)

**Can't pick up a second gem?**
- This is intentional - only one gem per player
- Drop your current gem first if you want a different one

**Primary ability not activating?**
- You must have the gem in offhand
- Press F (offhand swap key)
- Check if ability is on cooldown
- Make sure the gem type is enabled

**Passive effects not applying?**
- Move the gem to your offhand
- Wait a moment for effects to apply
- Try moving the gem to main inventory then back to offhand

## Advanced Tips

### Gem Synergies
- **Ice + Speed**: Create ice platforms for Speed IV mobility
- **Invis + Puff**: Stealth approach with dash for surprise attacks
- **Strength + Fire**: Maximize damage output with fire chance

### PvP Strategies
- **Strength**: Best for direct combat
- **Fire**: Area denial and water removal
- **Speed**: Hit-and-run tactics
- **Puff**: Evasive maneuvers and positioning
- **Ice**: Crowd control and escape prevention
- **Invis**: Ambush and reconnaissance

### Building/Utility
- **Speed**: Fastest mining and building
- **Puff**: Mobility for high builds
- **Fire**: Clearing water for underwater builds
- **Ice**: Creating speed highways

## Server Owner Notes

### Performance Considerations
- Ice cages and fire auras run periodic tasks
- Multiple active abilities increase server load
- Consider limiting gems given to players
- Monitor TPS when many players have gems active

### Balancing
- Adjust cooldowns and durations in config.yml
- Disable overpowered gems if needed
- Test changes with `/gem reload`
- All values are in seconds

### Integration
- Works with most permission plugins
- Compatible with other combat/ability plugins
- Gems use persistent data (won't conflict with other items)
- Trust data stored in `plugins/GemPlugin/trusts.yml`

## Support

For issues or questions:
- Check the main README.md
- Review this usage guide
- Open an issue on GitHub
- Contact server administrators
