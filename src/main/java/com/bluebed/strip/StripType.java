package com.bluebed.strip;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum StripType {
    GRASS(25, 20),
    PARK(5, 70),
    ROAD(25, 50),
    KINDA_HOMELESS(1, 1),
    MOTORWAY(5, 90),
    RIVER(25, 60),
    TRAIN(15, 30);

    // Values out of 100
    private final int spawnChance; // Must add to 100
    private final int repetitionChance; // Not required to add to 100
}
