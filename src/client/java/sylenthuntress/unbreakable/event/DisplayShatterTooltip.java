package sylenthuntress.unbreakable.event;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
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
            boolean advancedToolTips = false;
            int shatterLevel = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            int appendIndex = lines.size();
            if (!Unbreakable.CONFIG.shatterTooltip.INDEX_OVERRIDE()) for (Text text : lines) {
                if (!(text.getContent() instanceof TranslatableTextContent translatableContent)) continue;
                if (translatableContent.getKey().equalsIgnoreCase("item.durability"))
                    appendIndex = lines.indexOf(text);
                else if (translatableContent.getKey().equalsIgnoreCase("item.components"))
                    advancedToolTips = true;
            }
            else appendIndex = Math.clamp(Unbreakable.CONFIG.shatterTooltip.INDEX(), 0, lines.size());
            if (advancedToolTips && appendIndex == lines.size())
                appendIndex -= 2;
            if (Unbreakable.CONFIG.shatterTooltip.DISPLAY_TOOLTIP_DESC()) lines.add(
                    appendIndex,
                    Text.translatable(
                            "unbreakable.shatter.info",
                            shatterLevel
                    ));
            lines.add(
                    appendIndex,
                    Text.translatable("unbreakable.shatter.level").append(
                            NumberHelper.toRomanOrArabic(
                                    shatterLevel, "unbreakable.roman_numeral.",
                                    Unbreakable.CONFIG.shatterTooltip.ROMAN_NUMERALS()
                            )
                    ));
            if (((advancedToolTips && lines.size() > 2) || lines.size() > 4) && Unbreakable.CONFIG.shatterTooltip.SEPARATE_TOOLTIP())
                lines.add(appendIndex, Text.of(" "));
        }
    }
}
