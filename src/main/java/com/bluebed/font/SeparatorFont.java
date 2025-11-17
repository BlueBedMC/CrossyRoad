package com.bluebed.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum SeparatorFont {
    SCORE_LEFT("\uE02A"),
    SCORE_DOUBLE("\uE02B"),
    SCORE_TRIPLE("\uE02C"),
    SCORE_QUADRUPLE("\uE02D"),
    SCORE_ONE("\uE02E"),
    COINS_RIGHT("\uE03A"),
    COINS_DOUBLE("\uE03B"),
    COINS_TRIPLE("\uE03C"),
    COINS_QUADRUPLE("\uE03D"),
    COINS_ONE("\uE03E");

    private final String unicode;

    public static String getUnicodeAmountForScore(int number, int currentScore) {
        StringBuilder result = new StringBuilder();

        if (currentScore > 999) {
            result.append(SCORE_DOUBLE.getUnicode()).append(SCORE_TRIPLE.getUnicode()).append(SCORE_QUADRUPLE.getUnicode());
        } else if (currentScore > 99) {
            result.append(SCORE_DOUBLE.getUnicode()).append(SCORE_TRIPLE.getUnicode());
        } else if (currentScore > 9) {
            result.append(SCORE_DOUBLE.getUnicode());
        }

        int count = String.valueOf(currentScore).replaceAll("[^1]", "").length();
        result.append(String.valueOf(SCORE_ONE.getUnicode()).repeat(count));

        return result.toString();
    }

    public static String getUnicodeAmountForCoins(int number, int coins) {
        StringBuilder result = new StringBuilder();

        if (coins > 999) {
            result.append(COINS_DOUBLE.getUnicode()).append(COINS_TRIPLE.getUnicode()).append(COINS_QUADRUPLE.getUnicode());
        } else if (coins > 99) {
            result.append(COINS_DOUBLE.getUnicode()).append(COINS_TRIPLE.getUnicode());
        } else if (coins > 9) {
            result.append(COINS_DOUBLE.getUnicode());
        }

        int count = String.valueOf(coins).replaceAll("[^1]", "").length();
        result.append(String.valueOf(COINS_ONE.getUnicode()).repeat(count));

        return result.toString();
    }
}
