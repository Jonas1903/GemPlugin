# GemPlugin Testing Checklist

Use this checklist to verify all features work correctly after deployment.

## Pre-Testing Setup

- [ ] Server is running Paper 1.21.3+
- [ ] Plugin is installed in plugins folder
- [ ] Server has been restarted
- [ ] You have operator permissions
- [ ] Test in creative mode initially
- [ ] Have a second player (or alt account) for multiplayer tests

## Basic Functionality

### Plugin Installation
- [ ] Plugin loads without errors
- [ ] Console shows "GemPlugin has been enabled!"
- [ ] config.yml is created in plugins/GemPlugin/
- [ ] Default config values are present

### Commands
- [ ] `/gem` shows help message
- [ ] `/gem reload` works without errors
- [ ] Tab completion works for `/gem`
- [ ] `/trust` command is recognized
- [ ] `/untrust` command is recognized

## Gem Creation and Distribution

### Creating Gems
- [ ] `/gem give <player> strength` creates strength gem
- [ ] `/gem give <player> fire` creates fire gem
- [ ] `/gem give <player> speed` creates speed gem
- [ ] `/gem give <player> puff` creates puff gem
- [ ] `/gem give <player> ice` creates ice gem
- [ ] `/gem give <player> invis` creates invis gem

### Gem Properties
- [ ] Each gem has correct material (color)
- [ ] Gem names are colored and bold
- [ ] Gems have complete lore descriptions
- [ ] Gems stack properly (should be unique items)

### One Gem Limit
- [ ] Giving a second gem removes the new one
- [ ] Player gets error message about one gem limit
- [ ] Original gem is kept
- [ ] Dropping and picking up works correctly

## Strength Gem Tests

### Passive Effects
- [ ] Placing in offhand applies Strength II
- [ ] 1% double damage proc occurs (test with many hits)
- [ ] Removing from offhand removes Strength II
- [ ] Effects reapply when moved back to offhand

### Primary Ability
- [ ] Press F to activate
- [ ] "Critical Mode activated!" message appears
- [ ] XP bar shows cooldown
- [ ] Scoreboard shows timer
- [ ] Every hit deals critical damage (visual sparks)
- [ ] Effect lasts 20 seconds
- [ ] "Critical Mode ended" message appears
- [ ] 60 second cooldown activates
- [ ] Cannot activate while on cooldown

## Fire Gem Tests

### Passive Effects
- [ ] Placing in offhand applies Fire Resistance
- [ ] Player can stand in lava safely
- [ ] 5% chance to ignite targets (test with many hits)
- [ ] Target burns even with fire resistance
- [ ] Burns last 3 seconds
- [ ] Removing from offhand removes Fire Resistance

### Primary Ability
- [ ] Press F to activate
- [ ] "Fire Aura activated!" message appears
- [ ] Players within 3 blocks catch fire
- [ ] Mobs within 3 blocks catch fire
- [ ] Water blocks within aura are removed
- [ ] Aura follows player as they move
- [ ] Effect lasts 15 seconds
- [ ] 45 second cooldown activates

### Trust System Integration
- [ ] Trusted players don't catch fire
- [ ] `/trust <player>` works
- [ ] Aura skips trusted player
- [ ] `/untrust <player>` removes trust

## Speed Gem Tests

### Passive Effects
- [ ] Placing in offhand applies Speed I
- [ ] Placing in offhand applies Haste III
- [ ] Mining speed is noticeably faster
- [ ] Movement speed is increased
- [ ] Effects persist while in offhand
- [ ] Removing from offhand removes both effects

### Primary Ability
- [ ] Press F to activate
- [ ] "Haste Boost activated!" message appears
- [ ] Haste V is applied (very fast mining)
- [ ] Effect lasts 15 seconds
- [ ] Reverts to Haste III after 15s
- [ ] 30 second cooldown activates

## Puff Gem Tests

### Passive Effects - Double Jump
- [ ] Placing in offhand enables flight mode
- [ ] Jump once normally
- [ ] Press jump again in air to double jump
- [ ] 5 second cooldown between double jumps
- [ ] Double jump works repeatedly
- [ ] Removing gem disables flight mode

### Passive Effects - Damage Negation
- [ ] 5% chance to avoid damage (test with many hits)
- [ ] "Damage negated!" message appears
- [ ] No damage taken when proc occurs
- [ ] Health stays at same value

### Primary Ability
- [ ] Press F to activate
- [ ] Player dashes forward 10 blocks
- [ ] Direction follows where player is looking
- [ ] Slight upward arc prevents ground collision
- [ ] 20 second cooldown activates

### Edge Cases
- [ ] Double jump doesn't work in creative mode (bypass)
- [ ] Double jump doesn't work in spectator mode (bypass)
- [ ] Flight is disabled when gem removed

## Ice Gem Tests

### Passive Effects
- [ ] Standing on ice gives Speed IV
- [ ] Standing on packed ice gives Speed IV
- [ ] Standing on blue ice gives Speed IV
- [ ] Speed persists until stepping off ice
- [ ] Stepping on non-ice removes speed
- [ ] Speed reapplies when stepping back on ice
- [ ] Effect works with all ice types

### Primary Ability
- [ ] Press F to activate
- [ ] Ice cage appears as blue ice sphere
- [ ] Cage is 4 blocks radius
- [ ] Only air blocks are replaced
- [ ] Players inside get Slowness II
- [ ] Mobs inside get Slowness II
- [ ] Cage lasts 20 seconds
- [ ] Cage disappears after timer
- [ ] 60 second cooldown activates

### Trust System Integration
- [ ] Trusted players don't get Slowness II
- [ ] Cage still forms around them
- [ ] Slowness skips trusted players

### Edge Cases
- [ ] Cage doesn't replace solid blocks
- [ ] Cage disappears if player logs out
- [ ] Multiple cages don't conflict

## Invis Gem Tests

### Passive Effects
- [ ] Placing in offhand applies Invisibility
- [ ] Placing in offhand applies Speed II
- [ ] Player body is invisible
- [ ] Armor is still visible (normal invisibility)
- [ ] Held items are still visible
- [ ] Particles are hidden
- [ ] Effects persist while in offhand

### Primary Ability
- [ ] Press F to activate
- [ ] "Full Invisibility activated!" message appears
- [ ] Player body is invisible
- [ ] Armor is invisible
- [ ] Held items are invisible
- [ ] Completely hidden from other players
- [ ] Effect lasts 15 seconds
- [ ] Player becomes visible again after
- [ ] 45 second cooldown activates

### Edge Cases
- [ ] Invisibility works properly after respawn
- [ ] Effect cleans up on logout
- [ ] Other players can see gem holder again after effect

## Trust System Tests

### Basic Trust
- [ ] `/trust <player>` shows success message
- [ ] `/untrust <player>` shows success message
- [ ] Can't trust yourself (error message)
- [ ] Trust persists after logout
- [ ] Trust data saved in trusts.yml

### Combat Trust
- [ ] Strength gem effects don't apply to trusted
- [ ] Fire gem ignite doesn't apply to trusted
- [ ] Puff gem damage negation works against trusted

### Area Effect Trust
- [ ] Fire aura doesn't ignite trusted players
- [ ] Ice cage doesn't slow trusted players
- [ ] Trust works in both directions

### Edge Cases
- [ ] Trust works with offline players
- [ ] Trust persists through server restart
- [ ] Invalid player names handled gracefully

## Configuration Tests

### Enable/Disable Gems
- [ ] `/gem disable strength` disables strength gem
- [ ] Disabled gem doesn't work when in offhand
- [ ] Disabled gem shows no effects
- [ ] `/gem enable strength` re-enables gem
- [ ] Re-enabled gem works again

### Config Reload
- [ ] Edit config.yml cooldown values
- [ ] `/gem reload` applies changes
- [ ] New cooldowns take effect
- [ ] No server restart needed
- [ ] Invalid config handled gracefully

### Cooldown Display
- [ ] XP bar shows cooldown progress
- [ ] XP bar fills as cooldown decreases
- [ ] Level shows seconds remaining
- [ ] Scoreboard shows gem name + timer
- [ ] Display clears when cooldown ends
- [ ] Multiple gems don't conflict

## Inventory Management

### Gem Movement
- [ ] Moving gem to offhand applies effects
- [ ] Moving gem out of offhand removes effects
- [ ] Dropping gem removes effects
- [ ] Picking up gem doesn't auto-apply
- [ ] Gem in main inventory doesn't work

### Item Stacking
- [ ] Gems don't stack with each other
- [ ] Each gem is unique item
- [ ] Gems can be stored in chests
- [ ] Gems can be traded between players

## Player Lifecycle

### Login/Logout
- [ ] Gem effects removed on logout
- [ ] Cooldowns cleared on logout
- [ ] Active abilities cancelled on logout
- [ ] Ice cages removed on logout
- [ ] Fire auras stopped on logout
- [ ] No errors in console on logout

### Death/Respawn
- [ ] Gems drop on death (if configured)
- [ ] Effects removed on death
- [ ] Can pick up gem after respawn
- [ ] Effects reapply when picked up
- [ ] No lingering effects after respawn

## Performance Tests

### Single Player
- [ ] No lag with one gem active
- [ ] Cooldown tracking is smooth
- [ ] Effects apply instantly
- [ ] XP bar updates smoothly

### Multiple Players
- [ ] Multiple gems work simultaneously
- [ ] No TPS drop with several active gems
- [ ] Ice cages don't lag server
- [ ] Fire auras don't lag server
- [ ] Cooldowns independent per player

### Stress Test
- [ ] 10+ players with gems active
- [ ] Multiple ice cages active
- [ ] Multiple fire auras active
- [ ] Server TPS remains stable
- [ ] No memory leaks over time

## Edge Cases and Error Handling

### Invalid Commands
- [ ] `/gem` with no args shows help
- [ ] `/gem invalid` shows help
- [ ] `/gem give` with missing args shows usage
- [ ] `/gem give invalid_player` shows error
- [ ] `/gem give player invalid_gem` shows error

### Permission Tests
- [ ] Non-op can't use `/gem` commands
- [ ] Non-op can use `/trust` and `/untrust`
- [ ] Proper permission error messages
- [ ] Tab completion respects permissions

### Cleanup Tests
- [ ] Plugin disables cleanly
- [ ] All tasks cancelled on disable
- [ ] Trust data saved on disable
- [ ] No errors in console on disable
- [ ] Server restarts successfully

## Integration Tests

### With Other Plugins
- [ ] Works with permission plugins
- [ ] Works with economy plugins
- [ ] Works with combat plugins
- [ ] No conflicts with other items
- [ ] No conflicts with other potion effects

### World Compatibility
- [ ] Works in Overworld
- [ ] Works in Nether
- [ ] Works in End
- [ ] Works in custom worlds
- [ ] Ice gem works with natural ice

## Documentation Verification

- [ ] README matches implementation
- [ ] USAGE_GUIDE is accurate
- [ ] QUICK_REFERENCE is correct
- [ ] All cooldowns match config
- [ ] All commands work as documented

## Final Checks

- [ ] All tests passed
- [ ] No console errors
- [ ] No warnings logged
- [ ] Performance is acceptable
- [ ] Players can use gems effectively
- [ ] Documentation is clear
- [ ] Ready for production use

## Test Results

**Date Tested**: _______________  
**Tester**: _______________  
**Server Version**: _______________  
**Plugin Version**: 1.0  
**Tests Passed**: ____ / ____  
**Issues Found**: _______________  

**Overall Status**: ⬜ PASS  ⬜ FAIL  ⬜ NEEDS WORK

## Notes

Use this space to document any issues, unexpected behavior, or suggestions for improvement:

```
[Your notes here]
```
