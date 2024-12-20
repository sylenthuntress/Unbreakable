package sylenthuntress.unbreakable.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.ui.core.Color;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Sync(value = Option.SyncMode.OVERRIDE_CLIENT)
@Modmenu(modId = Unbreakable.MOD_ID)
@Config(name = "unbreakable-config", wrapperName = "UnbreakableConfig")
public class UnbreakableConfigModel {
    @SectionHeader("clientSection")
    @Sync(value = Option.SyncMode.NONE)
    public Color shatteredItemBarColor = Color.ofHsv(0.0F, 1.0F, 0.6F);
    @Nest
    @Sync(value = Option.SyncMode.NONE)
    @Expanded
    public ShatterTooltip shatterTooltip = new ShatterTooltip();
    @SectionHeader("damageSection")
    @RangeConstraint(min = 0.1F, max = 2.0F, decimalPlaces = 1)
    public float maxDamageMultiplier = 0.5F;
    public boolean onlyMultiplyShatterableItems = true;
    @Nest
    @Expanded
    public BonusDamage bonusDamageOnBreak = new BonusDamage();
    @Nest
    public DynamicDamage dynamicDamage = new DynamicDamage();
    @SectionHeader("shatterSection")
    public boolean breakItems = false;
    @PredicateConstraint("shatterLevelConstraint")
    public int maxShatterLevel = 3;
    @PredicateConstraint("shatterLevelConstraint")
    public int enchantmentScaling = 1;
    @RangeConstraint(min = 0.0F, max = 3.0F, decimalPlaces = 1)
    public float negativeDurabilityMultiplier = 1;
    @Nest
    public ShatterPenalties shatterPenalties = new ShatterPenalties();
    @SectionHeader("repairSection")
    public boolean allowRepairingShattered = true;
    public boolean tooExpensiveWarning = false;
    @Nest
    public RepairCost repairCost = new RepairCost();
    @SectionHeader("enchantmentSection")
    public boolean disableBindingWhenShattered = true;
    public boolean shatterCursedItems = true;
    public boolean exclusiveMending = true;

    public static boolean shatterLevelConstraint(int integer) {
        return integer >= 0 && integer <= 255;
    }

    public static class ShatterTooltip {
        public boolean DISPLAY_TOOLTIP = true;
        public boolean DISPLAY_TOOLTIP_DESC = true;
        public boolean SEPARATE_TOOLTIP = true;
        public boolean ROMAN_NUMERALS = true;
        public boolean DISPLAY_LEVEL_AT_ONE = false;
        @ExcludeFromScreen
        public boolean INDEX_OVERRIDE = false;
        @ExcludeFromScreen
        public int INDEX = 0;
    }

    public static class BonusDamage {
        public boolean DO_BONUS = true;
        @RangeConstraint(min = 0.0F, max = 3.0F, decimalPlaces = 1)
        public float BONUS_ATTACK_MULTIPLIER = 1.5F;
        @RangeConstraint(min = 0.0F, max = 6.0F, decimalPlaces = 1)
        public float BONUS_KNOCKBACK = 3;
    }

    public static class DynamicDamage {
        public boolean COMBAT = true;
        public boolean MINING = true;
        public boolean PROJECTILE = true;
        public boolean ELYTRA = true;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float COMBAT_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float MINING_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float PROJECTILE_MULTIPLIER = 1F;
        @RangeConstraint(min = 0.1F, max = 4.0F, decimalPlaces = 1)
        public float ELYTRA_MULTIPLIER = 1F;
    }

    public static class ShatterPenalties {
        public List<String> LIST = new ArrayList<>(
                Arrays.asList(
                        "minecraft:brush",
                        "minecraft:fishing_rod",
                        "minecraft:flint_and_steel",
                        "minecraft:shears",
                        "minecraft:shield",
                        "#minecraft:hoes"
                )
        );
        public boolean INVERT = false;
        public int THRESHOLD = -1;
        @SectionHeader("statSection")
        @RangeConstraint(min = 0F, max = 1F, decimalPlaces = 1)
        public float STAT_MINIMUM = 0.1F;
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

    public static class RepairCost {
        @RangeConstraint(min = 0.0, max = 4.0, decimalPlaces = 1)
        public float MULTIPLIER = 1;
        public boolean SHATTER_SCALING = true;
        public boolean ENCHANTMENT_SCALING = true;
        public boolean ANVIL_SCALING = false;
    }
}