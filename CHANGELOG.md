- Tweaked mending behavior. Now, mending will lose effectiveness with use, only resetting when an item is repaired
  normally.
- Added `/item damage (add|get|set) (block|entity)` subcommand
- Reworked how degradation components work. Now, it's stored in a list of repair methods mapped to an integer. Example:
  `iron_sword=[unbreakable:degradation{smithing_table:20,grindstone:0}]`
- Refactored some more code