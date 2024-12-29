package sylenthuntress.unbreakable.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.NumberHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.List;


public class DisplayShatterTooltip implements ItemTooltipCallback {
    @Override
    public void getTooltip(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (ItemShatterHelper.isShattered(stack) && Unbreakable.CONFIG.shatterTooltip.DISPLAY_TOOLTIP()) {
            boolean advancedToolTips = MinecraftClient.getInstance().options.advancedItemTooltips;
            int shatterLevel = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            int tooltipIndex = getTooltipIndex(lines, advancedToolTips);
            displayTooltip(lines, tooltipIndex, shatterLevel, advancedToolTips);
        }
    }

    public int getTooltipIndex(List<Text> lines, boolean advancedToolTips) {
        int tooltipIndex = 0;
        if (!Unbreakable.CONFIG.shatterTooltip.INDEX_OVERRIDE() && advancedToolTips) for (Text text : lines) {
            if (!(text.getContent() instanceof TranslatableTextContent translatableContent)) continue;
            if (translatableContent.getKey().equalsIgnoreCase("item.durability"))
                tooltipIndex = lines.indexOf(text);
        }
        else if (Unbreakable.CONFIG.shatterTooltip.INDEX_OVERRIDE())
            tooltipIndex = Math.clamp(Unbreakable.CONFIG.shatterTooltip.INDEX(), 0, lines.size());
        return tooltipIndex;
    }

    public void displayTooltip(List<Text> lines, int tooltipIndex, int shatterLevel, boolean advancedToolTips) {
        if (Unbreakable.CONFIG.shatterTooltip.DISPLAY_TOOLTIP_DESC()) lines.add(
                tooltipIndex,
                Text.translatable(
                        "unbreakable.shatter.info",
                        shatterLevel
                ));
        lines.add(
                tooltipIndex,
                Text.translatable("unbreakable.shatter.level").append(
                        NumberHelper.toRomanOrArabic(
                                shatterLevel, "unbreakable.roman_numeral.",
                                Unbreakable.CONFIG.shatterTooltip.ROMAN_NUMERALS(), Unbreakable.CONFIG.shatterTooltip.DISPLAY_LEVEL_AT_ONE()
                        )
                ));
        if (((advancedToolTips && lines.size() > 2) || lines.size() > 4) && Unbreakable.CONFIG.shatterTooltip.SEPARATE_TOOLTIP())
            lines.add(tooltipIndex, Text.of(" "));
    }
}
