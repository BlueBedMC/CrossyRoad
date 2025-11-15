package com.bluebed.strip;

import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StripRegistry {
    private final int HEIGHT = 20;
    private final int WIDTH = 40;
    private final Map<StripType, StripGenerator> generators = new HashMap<>();
    private final Random RANDOM = new Random();

    public StripGenerator getRandomGenerator() {
        return generators.get(randomType());
    }

    public StripGenerator getGenerator(StripType type) {
        return generators.get(type);
    }

    public void init() {
        generators.put(StripType.GRASS, ((unit, start, startZ, length) -> {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < length; z++) {
                    int finalZ = startZ + z;
                    unit.modifier().setBlock(
                            start.blockX() + x,
                            HEIGHT,
                            start.blockZ() + finalZ,
                            finalZ % 2 == 0 ? Block.GREEN_CONCRETE : Block.LIME_CONCRETE
                    );
                }
            }
        }));

        generators.put(StripType.ROAD, ((unit, start, startZ, length) -> {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < length; z++) {
                    int finalZ = startZ + z;
                    unit.modifier().setBlock(
                            start.blockX() + x,
                            HEIGHT,
                            start.blockZ() + finalZ,
                            Block.BLACK_CONCRETE
                    );
                }
            }
        }));

        generators.put(StripType.RIVER, ((unit, start, startZ, length) -> {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < length; z++) {
                    int finalZ = startZ + z;
                    unit.modifier().setBlock(
                            start.blockX() + x,
                            HEIGHT,
                            start.blockZ() + finalZ,
                            Block.WATER
                    );
                }
            }
        }));

        generators.put(StripType.TRAIN, ((unit, start, startZ, length) -> {
            for (int x = 0; x < WIDTH; x++) {
                for (int z = 0; z < length; z++) {
                    int finalZ = startZ + z;
                    unit.modifier().setBlock(
                            start.blockX() + x,
                            HEIGHT,
                            start.blockZ() + finalZ,
                            Block.BLACK_CONCRETE
                    );

                    unit.modifier().setBlock(
                            start.blockX() + x,
                            HEIGHT + 1,
                            start.blockZ() + finalZ,
                            Block.RAIL
                    );
                }
            }
        }));
    }

    public StripType randomType() {
        return switch (RANDOM.nextInt(3)) {
            case 0 -> StripType.RIVER;
            case 1 -> StripType.TRAIN;
            default -> StripType.ROAD;
        };
    }

    public int randomLength(StripType type) {
        return switch (type) {
            case GRASS -> 1;
            case ROAD -> RANDOM.nextInt(2) + 3;
            case RIVER -> RANDOM.nextInt(3) + 3;
            case TRAIN -> RANDOM.nextInt(2) + 1;
        };
    }

}
