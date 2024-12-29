package sylenthuntress.unbreakable.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.TreeMap;


public class NumberHelper {
    private final static TreeMap<Integer, Integer> romanNumerals = new TreeMap<>();

    static {
        romanNumerals.put(1000, 1000);
        romanNumerals.put(900, 900);
        romanNumerals.put(500, 500);
        romanNumerals.put(400, 400);
        romanNumerals.put(100, 100);
        romanNumerals.put(90, 90);
        romanNumerals.put(50, 50);
        romanNumerals.put(40, 40);
        romanNumerals.put(10, 10);
        romanNumerals.put(5, 5);
        romanNumerals.put(4, 4);
        romanNumerals.put(1, 1);
    }

    public static MutableText toRomanOrArabic(int number, String translationKey, boolean doRoman, boolean displayIfOne) {
        if (doRoman) return toRoman(number, translationKey, displayIfOne);
        return toArabic(number, displayIfOne);
    }

    public static MutableText toRoman(int number, String translationKey, boolean displayIfOne) {
        int numeral = romanNumerals.floorKey(number);
        if (number == numeral)
            return Text.translatable("unbreakable.roman_numeral." + (displayIfOne ? romanNumerals.get(number) : "null"));
        return Text.translatable(translationKey + numeral).append(toRoman(number - numeral, translationKey, false));
    }

    public static MutableText toArabic(int number, boolean displayIfOne) {
        return Text.literal(displayIfOne ? Integer.toString(number) : "unbreakable.roman_numeral.null");
    }
}