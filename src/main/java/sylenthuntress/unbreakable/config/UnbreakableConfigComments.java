package sylenthuntress.unbreakable.config;

public abstract class UnbreakableConfigComments {
    // Client-side
    public final static String clientOnly = " (Client-only)";
    public final static String shatteredItemBarColor = "Color for the item bar when shattered" + clientOnly;
    // -> Shatter item tooltip
    public final static String DISPLAY_TOOLTIP = "Display tooltip for shattered items" + clientOnly;
    public final static String DISPLAY_TOOLTIP_DESC = "Display description on tooltip for shattered items" + clientOnly;
    public final static String DISPLAY_LEVEL_AT_ONE = "Prevents the shatter level of items for displaying when equal to 1, for parity with vanilla enchantments" + clientOnly;
    public final static String SEPARATE_TOOLTIP = "Separate shattered item tooltip from the rest of an item's tooltips" + clientOnly;
    public final static String ROMAN_NUMERALS = "Separate shattered item tooltip from the rest of an item's tooltips" + clientOnly;
    public final static String INDEX_OVERRIDE = "\n! [ UNSTABLE ] !\nOverrides the display index for the shattered item tooltip" + clientOnly;
    // Item Durability
    public final static String maxDamageMultiplier = "The base durability multiplier for all items";
    public final static String onlyMultiplyShatterableItems = "Whether the above option only applies to items that can be shattered";
    // -> Bonus damage on weapon break
    public final static String DO_BONUS = "Enable bonus damage and knockback on weapon break";
    public final static String BONUS_ATTACK_MULTIPLIER = "Bonus attack damage multiplier";
    public final static String BONUS_KNOCKBACK = "Bonus knockback to be applied";
    // -> Dynamic durability loss
    public final static String COMBAT = "Dynamically damage weapons based on dealt damage";
    public final static String MINING = "Dynamically damage tools based on block hardness";
    public final static String PROJECTILE = "Dynamically damage projectile weapons based on projectile power";
    public final static String ELYTRA = "Dynamically damage elytra based on flight speed";
    public final static String DYNAMIC_MULTIPLIERS = "Multipliers for dynamic durability loss";
    // Shattered Items
    public final static String breakItems = "Whether to break items like normal at their maximum shatter level";
    public final static String maxShatterLevel = "The maximum shatter level an item can reach";
    public final static String enchantmentScaling = "The amount that enchanting an item with unbreaking will scale an item's maximum shatter level by";
    public final static String negativeDurabilityMultiplier = "Multiplier for the lowest negative durability value an item can reach";
    // -> Item Penalties
    public final static String LIST = "The list of items to completely disable when shattered";
    public final static String INVERT = "Whether to invert the above list";
    public final static String THRESHOLD = "The shatter level threshhold to enable the above list";
    // --> Stat Penalties
    public final static String STAT_MINIMUM = "The minimum effectiveness for a penalized stat";
    public final static String PENALTIES = "Which stats to penalize for shattered items";
    // Item Repairing
    public final static String allowRepairingShattered = "Whether repairing items should decrease their shatter level";
    public final static String tooExpensiveWarning = "Whether to prevent items from being repaired at 40+ level cost";
    // -> Anvil Repairing
    public final static String MULTIPLIER = "Anvil repair cost multiplier";
    public final static String SHATTER_SCALING = "Whether to scale repair cost when repairing stacks of shattered";
    public final static String ENCHANTMENT_SCALING = "Whether to scale repair cost based on the item's enchantments";
    public final static String ANVIL_SCALING = "Whether to scale repair cost based on prior anvil uses";
    // Enchantments
    public final static String disableBindingWhenShattered = "Whether curse of binding should work on shattered items";
    public final static String shatterCursedItems = "Whether curse of vanishing should shatter items instead of deleting them";
    public final static String exclusiveMending = "Whether to mending and unbreaking should be incompatible";
}
