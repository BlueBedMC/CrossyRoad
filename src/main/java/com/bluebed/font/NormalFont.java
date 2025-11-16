package com.bluebed.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NormalFont {
    ZERO(0, "zero", "\uE000"),
    ONE(1, "one", "\uE001"),
    TWO(2, "two", "\uE002"),
    THREE(3, "three", "\uE003"),
    FOUR(4, "four", "\uE004"),
    FIVE(5, "five", "\uE005"),
    SIX(6, "six", "\uE006"),
    SEVEN(7, "seven", "\uE007"),
    EIGHT(8, "eight", "\uE008"),
    NINE(9, "nine", "\uE009"),
    COIN(-1, "coin", "\uE01A");

    private final int number;
    private final String identifier;
    private final String unicode;

    public static String getUnicodeFromNumber(int number) {
        for (NormalFont font : NormalFont.values()) {
            if (font.getNumber() == number) return font.getUnicode();
        }
        return null;
    }

    public static String getUnicodeFromIdentifier(String identifier) {
        for (NormalFont font : NormalFont.values()) {
            if (font.getIdentifier().equalsIgnoreCase(identifier)) return font.getUnicode();
        }
        return null;
    }
}
