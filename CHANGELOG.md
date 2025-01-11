# Version v1.1.0+1.21.4

Internal rewrite, new features, and more!

**Features**:
* Players can now repair items without XP using smithing tables
    * Conversely, players can now use the grindstone to repair items with _only_ XP levels
    * These options become less effective on a given item until you repair it using other means
        * Anvils completely reset their effectiveness, but are expensive
* Added plenty of new config options

**Tweaks**:

* Tweaked tool damage calculations based on block hardness

**Technical**:

* Added a registry for ALL items used as `repairIngredients` in any `RepairableComponent` for the smithing table rework
* Changed how items are disabled when shattered
* Fixed calculation errors when displaying shatter level
* Refactored a LOT of code, and overhauled parts of config
    * Other mods can add their items to the new tags for extra mod compatibility