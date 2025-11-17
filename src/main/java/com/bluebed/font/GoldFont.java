package com.bluebed.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoldFont {
    ZERO(0, "zero", "\uE010"),
    ONE(1, "one", "\uE011"),
    TWO(2, "two", "\uE012"),
    THREE(3, "three", "\uE013"),
    FOUR(4, "four", "\uE014"),
    FIVE(5, "five", "\uE015"),
    SIX(6, "six", "\uE016"),
    SEVEN(7, "seven", "\uE017"),
    EIGHT(8, "eight", "\uE018"),
    NINE(9, "nine", "\uE019"),
    COIN(-1, "coin", "\uE01B");

    private final int number;
    private final String identifier;
    private final String unicode;

    public static String getUnicodeFromNumber(int number) {
        for (GoldFont font : GoldFont.values()) {
            if (font.getNumber() == number) return font.getUnicode();
        }
        return null;
    }

    public static String getUnicodeFromIdentifier(String identifier) {
        for (GoldFont font : GoldFont.values()) {
            if (font.getIdentifier().equalsIgnoreCase(identifier)) return font.getUnicode();
        }
        return null;
    }
}
