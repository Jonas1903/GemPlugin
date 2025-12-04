# GemPlugin Deployment Guide

This guide covers deploying GemPlugin to a production Minecraft server.

## Prerequisites

✅ Paper Minecraft Server 1.21.3+ (or compatible)  
✅ Java 17 or higher  
✅ Server operator access  
✅ Ability to restart the server

## Installation Steps

### 1. Download the Plugin

**Option A: From Releases**
- Download `GemPlugin-1.0.jar` from the GitHub releases page

**Option B: Build from Source**
```bash
git clone https://github.com/Jonas1903/GemPlugin.git
cd GemPlugin
mvn clean package
# JAR will be in target/GemPlugin-1.0.jar
```

### 2. Install on Server

1. Stop your Minecraft server
2. Place `GemPlugin-1.0.jar` in the `plugins/` folder
3. Start your Minecraft server
4. Check console for: `[GemPlugin] GemPlugin has been enabled!`

### 3. Verify Installation

In-game as an operator:
```
/gem reload
```

You should see: "Configuration reloaded!"

## Initial Configuration

### 1. Review Default Settings

The plugin creates: `plugins/GemPlugin/config.yml`

Default values are production-ready, but review:
- Cooldown timings
- Ability durations
- Which gems are enabled

### 2. Customize (Optional)

Edit `plugins/GemPlugin/config.yml`:

```yaml
cooldowns:
  strength:
    primary: 60        # Adjust as needed
  # ... more settings
```

Apply changes:
```
/gem reload
```

### 3. Test Gems

Give yourself a test gem:
```
/gem give YourName strength
```

1. Place in offhand
2. Verify passive effects
3. Press F to test primary ability
4. Check cooldown display (XP bar)

## Production Deployment

### Pre-Launch Checklist

- [ ] Plugin installed and enabled
- [ ] Configuration reviewed and customized
- [ ] Test gems in creative/test world first
- [ ] Decide which gems to enable for launch
- [ ] Document rules for your server
- [ ] Train moderators on commands
- [ ] Backup server before launch

### Launch Strategy

**Option 1: Soft Launch**
1. Enable only 2-3 gems initially
2. Give gems to select players for testing
3. Monitor for issues and balance
4. Enable more gems gradually

**Option 2: Full Launch**
1. Enable all gems from start
2. Announce gem system to players
3. Distribute gems via events/rewards
4. Monitor and adjust as needed

### Distribution Methods

**Manual Distribution**
```
/gem give PlayerName gem_type
```

**Event-Based**
- PvP tournament rewards
- Building competition prizes
- Server milestones

**Economy Integration** (requires additional plugin)
- Add gems to shop
- Trade gems with villagers
- Loot from boss fights

## Maintenance

### Regular Tasks

**Daily:**
- Monitor server performance
- Check for gem-related exploits
- Review player feedback

**Weekly:**
- Analyze gem usage statistics
- Adjust balance if needed
- Update trust relationships as needed

**Monthly:**
- Review configuration
- Consider adding/removing gems
- Update documentation

### Performance Monitoring

Watch for:
- TPS drops during heavy gem usage
- Ice cage and fire aura performance
- Cooldown manager efficiency

Optimize by:
- Adjusting task frequencies in code
- Limiting concurrent active abilities
- Disabling problematic gems temporarily

## Troubleshooting

### Common Issues

**Plugin Not Loading**
1. Check Paper version compatibility
2. Verify Java 17+
3. Look for errors in console
4. Check plugin.yml syntax

**Gems Not Working**
1. Verify gem is enabled: `/gem enable <type>`
2. Check player has gem in offhand
3. Ensure no cooldown conflicts
4. Test in safe area first

**Performance Issues**
1. Disable ice and fire gems temporarily
2. Reduce number of active gems
3. Increase server resources
4. Check for conflicting plugins

**Trust System Not Working**
1. Check `trusts.yml` exists
2. Verify permissions
3. Test with `/trust` command
4. Restart server to reload

## Backup and Recovery

### Backup Files
```
plugins/GemPlugin/config.yml       # Configuration
plugins/GemPlugin/trusts.yml       # Trust relationships
plugins/GemPlugin.jar              # Plugin file
```

### Recovery Steps

**Configuration Reset:**
1. Delete `config.yml`
2. Restart server
3. Default config will be recreated

**Trust Data Recovery:**
1. Restore `trusts.yml` from backup
2. Restart server or use `/gem reload`

**Full Reset:**
1. Stop server
2. Delete `plugins/GemPlugin/` folder
3. Reinstall plugin
4. Start server

## Upgrading

### Update Process

1. **Backup** current installation
2. **Stop** the server
3. **Replace** old JAR with new version
4. **Review** changelog for config changes
5. **Update** config.yml if needed
6. **Start** server
7. **Test** in creative mode first
8. **Monitor** for issues

### Version Compatibility

- Always check Paper version compatibility
- Test on staging server first
- Review breaking changes in release notes
- Update configuration for new features

## Security

### Permission Management

Restrict sensitive commands:
```yaml
# In your permissions plugin
gemplugin.admin: [admin, moderator]
gemplugin.trust: [default]
```

### Preventing Exploits

- Monitor for gem duplication attempts
- Log all `/gem give` commands
- Set limits on gem distribution
- Review trust relationships regularly

### Best Practices

1. Only give gems to trusted players
2. Monitor gem usage patterns
3. Disable gems if exploits found
4. Keep plugin updated
5. Regular backups

## Support Resources

### Getting Help

1. **GitHub Issues**: Report bugs and issues
2. **Documentation**: README.md and USAGE_GUIDE.md
3. **Quick Reference**: QUICK_REFERENCE.md
4. **Community**: Server forums/Discord

### Reporting Issues

Include:
- Plugin version
- Paper version
- Java version
- Server log excerpt
- Steps to reproduce
- Expected vs actual behavior

## Post-Deployment

### Server Announcements

Example announcement:
```
🎮 NEW: Gem System!

Six powerful gems are now available:
- Strength (Combat)
- Fire (Area Control)
- Speed (Utility)
- Puff (Mobility)
- Ice (Control)
- Invis (Stealth)

Get gems via: [your distribution method]
Hold in offhand, press F to activate!

/trust players to team up!
```

### Player Education

1. Share QUICK_REFERENCE.md
2. Create video tutorials
3. Host demonstration events
4. Answer questions patiently
5. Gather feedback for improvements

## Conclusion

✅ Installation complete  
✅ Configuration optimized  
✅ Testing successful  
✅ Ready for production

Monitor your server closely for the first few days and be ready to make adjustments based on player feedback and performance metrics.

Good luck with your gem-powered Minecraft server!
