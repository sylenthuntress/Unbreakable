package sylenthuntress.unbreakable.client.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.NumberHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;

import java.util.List;


public class DisplayShatterTooltip implements ItemTooltipCallback {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (!ShatterHelper.isShattered(stack) || !Unbreakable.CONFIG.shatterTooltip.DISPLAY_TOOLTIP()) {
            return;
        }

        boolean advancedToolTips = !Unbreakable.CONFIG.shatterTooltip.INDEX_OVERRIDE() && client.options.advancedItemTooltips;

        int shatterLevel = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int tooltipIndex = getTooltipIndex(lines, advancedToolTips);

        displayTooltip(lines, stack, tooltipIndex, shatterLevel, advancedToolTips);
    }

    public int getTooltipIndex(List<Text> lines, boolean advancedToolTips) {
        int tooltipIndex = lines.size();

        if (advancedToolTips) {
            for (Text text : lines) {
                if (text.getContent() instanceof TranslatableTextContent content && content.getKey().equals("item.components")) {
                    tooltipIndex = lines.indexOf(text) - 2;
                }
            }
        } else if (Unbreakable.CONFIG.shatterTooltip.INDEX_OVERRIDE()) {
            tooltipIndex = Math.clamp(Unbreakable.CONFIG.shatterTooltip.INDEX(), 0, lines.size());
        }

        return tooltipIndex;
    }

    public void displayTooltip(List<Text> lines, ItemStack stack, int tooltipIndex, int shatterLevel, boolean advancedToolTips) {
        if ((advancedToolTips && lines.size() > 2 || lines.size() > 4) && Unbreakable.CONFIG.shatterTooltip.SEPARATE_TOOLTIP()) {
            lines.add(tooltipIndex, Text.empty());
            tooltipIndex += 1;
        }

        MutableText levelText = Text.translatable("unbreakable.numbers.null");
        if (ShatterHelper.isMaxShatterLevel(stack) && Unbreakable.CONFIG.shatterTooltip.DISPLAY_TEXT_AT_MAX()) {
            levelText = Text.translatable("unbreakable.numbers.max");
        } else if (shatterLevel > 1 || Unbreakable.CONFIG.shatterTooltip.DISPLAY_LEVEL_AT_ONE()) {
            levelText = NumberHelper.toRomanOrArabic(shatterLevel,
                    "unbreakable.roman_numeral.",
                    Unbreakable.CONFIG.shatterTooltip.ROMAN_NUMERALS()
            );
        }
        lines.add(tooltipIndex, Text.translatable("unbreakable.shatter.level", levelText).formatted(Formatting.DARK_RED));
        tooltipIndex += 1;

        if (Unbreakable.CONFIG.shatterTooltip.DISPLAY_TOOLTIP_DESC()) {
            lines.add(tooltipIndex,
                    Text.translatable("unbreakable.shatter.info", shatterLevel)
                            .formatted(Formatting.GRAY, Formatting.ITALIC)
            );
        }
    }
}
