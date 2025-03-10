package sylenthuntress.unbreakable.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.ui.core.Color;
import sylenthuntress.unbreakable.Unbreakable;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
@Sync(value = Option.SyncMode.OVERRIDE_CLIENT)
@Modmenu(modId = Unbreakable.MOD_ID)
@Config(name = "unbreakable-config", wrapperName = "UnbreakableConfig")
public class UnbreakableConfigModel {
    // Client config
    @SectionHeader("clientSection")
    @Sync(value = Option.SyncMode.NONE)
    @Comment(UnbreakableConfigComments.shatteredItemBarColor)
    public Color shatteredItemBarColor = Color.ofHsv(0.0F, 1.0F, 0.6F);
    @Nest
    @Sync(value = Option.SyncMode.NONE)
    @Expanded
    public ShatterTooltip shatterTooltip = new ShatterTooltip();

    // Item damage config
    @SectionHeader("damageSection")
    @Comment(UnbreakableConfigComments.DAMAGE_SHIELD_COOLDOWN)
    public boolean damageShieldCooldown = true;

    @Comment(UnbreakableConfigComments.DAMAGE_ITEM_ENTITIES)
    public boolean damageItemEntities = true;

    @RangeConstraint(min = 0.1F, max = 2.0F, decimalPlaces = 1)
    @Nest
    @Expanded
    public DurabilityModifier durabilityModifier = new DurabilityModifier();

    @Nest
    public BonusDamage bonusDamageOnBreak = new BonusDamage();

    @Nest
    public DynamicDamage dynamicDamage = new DynamicDamage();


    // Item shatter config
    @SectionHeader("shatterSection")
    @Comment(UnbreakableConfigComments.breakItems)
    public boolean breakItems = false;

    @PredicateConstraint("shatterLevelConstraint")
    @Comment(UnbreakableConfigComments.maxShatterLevel)
    public int maxShatterLevel = 3;

    @PredicateConstraint("shatterLevelConstraint")
    @Comment(UnbreakableConfigComments.enchantmentScaling)
    public int enchantmentScaling = 1;

    @RangeConstraint(min = 0.0F, max = 3.0F, decimalPlaces = 1)
    @Comment(UnbreakableConfigComments.negativeDurabilityMultiplier)
    public float negativeDurabilityMultiplier = 1;

    @Nest
    public ShatterBlacklist shatterBlacklist = new ShatterBlacklist();
    @Nest
    public ShatterPenalties shatterPenalties = new ShatterPenalties();

    // Item repair config
    @SectionHeader("repairSection")
    @Comment(UnbreakableConfigComments.allowRepairingShattered)
    public boolean allowRepairingShattered = true;

    @Nest
    public AnvilRepair anvilRepair = new AnvilRepair();

    @Nest
    @Comment(UnbreakableConfigComments.SMITHING_REPAIR)
    public SmithingRepair smithingRepair = new SmithingRepair();

    @Nest
    @Comment(UnbreakableConfigComments.GRINDSTONE_REPAIR)
    public GrindingRepair grindingRepair = new GrindingRepair();

    // Enchantments config
    @SectionHeader("enchantmentSection")
    @Comment(UnbreakableConfigComments.disableBindingWhenShattered)
    public boolean disableBindingWhenShattered = true;

    @Comment(UnbreakableConfigComments.shatterCursedItems)
    public boolean shatterCursedItems = true;

    @Comment(UnbreakableConfigComments.exclusiveMending)
    public boolean exclusiveMending = true;

    public static boolean shatterLevelConstraint(int integer) {
        return integer >= 0 && integer <= 255;
    }

    public static class AnvilRepair {
        @Comment(UnbreakableConfigComments.ALLOW_REPAIR)
        public boolean ALLOW = true;
        @Comment(UnbreakableConfigComments.CLEAR_MENDING_DEGRADATION)
        public boolean CLEARS_MENDING_DEGRADATION = true;
        @Comment(UnbreakableConfigComments.TOO_EXPENSIVE)
        public boolean TOO_EXPENSIVE = false;
        @Nest
        @Expanded
        public AnvilRepairCost COST = new AnvilRepairCost();
    }

    public static class SmithingRepair {
        @Comment(UnbreakableConfigComments.ALLOW_REPAIR)
        public boolean ALLOW = true;
        @Comment(UnbreakableConfigComments.CLEAR_MENDING_DEGRADATION)
        public boolean CLEARS_MENDING_DEGRADATION = true;
        @Nest
        @Expanded
        public SmithingRepairCost COST = new SmithingRepairCost();
    }

    public static class GrindingRepair {
        @Comment(UnbreakableConfigComments.ALLOW_REPAIR)
        public boolean ALLOW = true;
        @Comment(UnbreakableConfigComments.CLEAR_MENDING_DEGRADATION)
        public boolean CLEARS_MENDING_DEGRADATION = true;
        @Nest
        @Expanded
        public GrindingRepairCost COST = new GrindingRepairCost();
    }

    public static class ShatterTooltip {
        @Comment(UnbreakableConfigComments.DISPLAY_TOOLTIP)
        public boolean DISPLAY_TOOLTIP = true;
        @Comment(UnbreakableConfigComments.DISPLAY_TOOLTIP_DESC)
        public boolean DISPLAY_TOOLTIP_DESC = true;
        @Comment(UnbreakableConfigComments.SEPARATE_TOOLTIP)
        public boolean SEPARATE_TOOLTIP = true;
        @Comment(UnbreakableConfigComments.ROMAN_NUMERALS)
        public boolean ROMAN_NUMERALS = true;
        @Comment(UnbreakableConfigComments.DISPLAY_TEXT_AT_MAX)
        public boolean DISPLAY_TEXT_AT_MAX = false;
        @Comment(UnbreakableConfigComments.DISPLAY_LEVEL_AT_ONE)
        public boolean DISPLAY_LEVEL_AT_ONE = false;
        @ExcludeFromScreen
        @Comment(UnbreakableConfigComments.INDEX_OVERRIDE)
        public boolean INDEX_OVERRIDE = false;
        @ExcludeFromScreen
        public int INDEX = 0;
    }

    public static class DurabilityModifier {
        @Comment(UnbreakableConfigComments.DurabilityModifier_MULTIPLIER)
        public float MULTIPLIER = 0.5F;
        @Comment(UnbreakableConfigComments.DurabilityModifier_LIST)
        @Expanded
        public List<String> LIST = new ArrayList<>(
                List.of(
                        "#unbreakable:exclude_modifier",
                        "#unbreakable:shatter_blacklist"
                )
        );
        @Comment(UnbreakableConfigComments.INVERT)
        public boolean INVERT = false;
    }

    public static class BonusDamage {
        @Comment(UnbreakableConfigComments.DO_BONUS)
        public boolean DO_BONUS = true;
        @Comment(UnbreakableConfigComments.BONUS_ATTACK_MULTIPLIER)
        @RangeConstraint(min = 0.0F, max = 3.0F, decimalPlaces = 1)
        public float BONUS_ATTACK_MULTIPLIER = 1.5F;
        @Comment(UnbreakableConfigComments.BONUS_KNOCKBACK)
        @RangeConstraint(min = 0.0F, max = 6.0F, decimalPlaces = 1)
        public float BONUS_KNOCKBACK = 3;
    }

    public static class DynamicDamage {
        @Comment(UnbreakableConfigComments.COMBAT)
        public boolean COMBAT = true;
        @Comment(UnbreakableConfigComments.MINING)
        public boolean MINING = true;
        @Comment(UnbreakableConfigComments.PROJECTILE)
        public boolean PROJECTILE = true;
        @Comment(UnbreakableConfigComments.ELYTRA)
        public boolean ELYTRA = true;
        @Comment(UnbreakableConfigComments.DYNAMIC_MULTIPLIERS)
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float COMBAT_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float MINING_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float PROJECTILE_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float ELYTRA_MULTIPLIER = 1F;
    }

    public static class ShatterBlacklist {
        @Comment(UnbreakableConfigComments.ShatterBlacklist_LIST)
        @Expanded
        public List<String> LIST = new ArrayList<>(
                List.of(
                        "#unbreakable:shatter_blacklist"
                )
        );
        @Comment(UnbreakableConfigComments.INVERT)
        public boolean INVERT = false;
    }

    public static class ShatterPenalties {
        @Comment(UnbreakableConfigComments.ShatterPenalty_LIST)
        @Expanded
        public List<String> LIST = new ArrayList<>(
                List.of(
                        "#unbreakable:prevent_use_when_shattered"
                )
        );
        @Comment(UnbreakableConfigComments.INVERT)
        public boolean INVERT = false;
        @Comment(UnbreakableConfigComments.FUNCTIONS)
        public boolean ITEM_USE = true;
        public boolean ENTITY_USE = true;
        public boolean ENTITY_HIT = false;
        public boolean BLOCK_USE = true;
        public boolean BLOCK_HIT = false;
        @Comment(UnbreakableConfigComments.THRESHOLD)
        public int THRESHOLD = -1;
        @SectionHeader("statSection")
        @RangeConstraint(min = 0F, max = 1F, decimalPlaces = 1)
        @Comment(UnbreakableConfigComments.STAT_MINIMUM)
        public float STAT_MINIMUM = 0.1F;
        @Comment(UnbreakableConfigComments.PENALTIES)
        public boolean ARMOR = true;
        public boolean ARMOR_EFFECTS = true;
        public boolean ARMOR_TOUGHNESS = true;
        public boolean ATTACK_DAMAGE = true;
        public boolean ATTACK_SPEED = true;
        public boolean DURABILITY_LOSS = true;
        public boolean GLIDING_VELOCITY = true;
        public boolean FISHING_LUCK = true;
        public boolean KNOCKBACK_RESISTANCE = true;
        public boolean MINING_SPEED = true;
        public boolean PROJECTILES = true;
        public boolean RIPTIDE = true;
        public boolean SHIELD_ARC = true;
    }

    public static class AnvilRepairCost {
        @RangeConstraint(min = 0.0, max = 4.0, decimalPlaces = 1)
        @Comment(UnbreakableConfigComments.COST_MULTIPLIER)
        public float MULTIPLIER = 1;
        @Comment(UnbreakableConfigComments.SHATTER_SCALING)
        public boolean SHATTER_SCALING = true;
        @Comment(UnbreakableConfigComments.ENCHANTMENT_SCALING)
        public boolean ENCHANTMENT_SCALING = true;
        @Comment(UnbreakableConfigComments.REPAIR_SCALING)
        public boolean REPAIR_SCALING = false;
    }

    public static class SmithingRepairCost {
        @RangeConstraint(min = 0.0, max = 4.0, decimalPlaces = 1)
        @Comment(UnbreakableConfigComments.COST_MULTIPLIER)
        public float MULTIPLIER = 1;
        @Comment(UnbreakableConfigComments.DEGRADE_REPAIR_FACTOR)
        public boolean DEGRADE_REPAIR_FACTOR = true;
        @Comment(UnbreakableConfigComments.GRINDING_DECREMENTS_DEGRADATION)
        public boolean GRINDING_DECREMENTS_DEGRADATION = true;
        @Comment(UnbreakableConfigComments.ANVILS_CLEAR_DEGRADATION)
        public boolean ANVILS_CLEAR_DEGRADATION = true;
        @Comment(UnbreakableConfigComments.SHATTER_SCALING)
        public boolean SHATTER_SCALING = false;
        @Comment(UnbreakableConfigComments.ENCHANTMENT_SCALING)
        public boolean ENCHANTMENT_SCALING = false;
        @Comment(UnbreakableConfigComments.REPAIR_SCALING)
        public boolean REPAIR_SCALING = false;
    }

    public static class GrindingRepairCost {
        @RangeConstraint(min = 0.0, max = 4.0, decimalPlaces = 1)
        @Comment(UnbreakableConfigComments.COST_MULTIPLIER)
        public float MULTIPLIER = 1;
        @Comment(UnbreakableConfigComments.DEGRADE_REPAIR_FACTOR)
        public boolean DEGRADE_REPAIR_FACTOR = true;
        @Comment(UnbreakableConfigComments.SMITHING_DECREMENTS_DEGRADATION)
        public boolean SMITHING_DECREMENTS_DEGRADATION = true;
        @Comment(UnbreakableConfigComments.ANVILS_CLEAR_DEGRADATION)
        public boolean ANVILS_CLEAR_DEGRADATION = true;
        @Comment(UnbreakableConfigComments.SHATTER_SCALING)
        public boolean SHATTER_SCALING = false;
        @Comment(UnbreakableConfigComments.ENCHANTMENT_SCALING)
        public boolean ENCHANTMENT_SCALING = false;
        @Comment(UnbreakableConfigComments.REPAIR_SCALING)
        public boolean REPAIR_SCALING = false;
    }
}