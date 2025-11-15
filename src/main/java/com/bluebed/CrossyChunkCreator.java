package com.bluebed;

import com.bluebed.strip.StripType;
import lombok.RequiredArgsConstructor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class CrossyChunkCreator implements Generator {
    private final int seed;

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        final Point start = unit.absoluteStart().withY(70);
        final Point size = unit.size();

        if (start.chunkX() < -2 || start.chunkX() > 2) return;

        StripType oldType = null;
        Block block = null;
        for (int z = 0; z < size.blockZ(); z++) {
            if (z % 4 != 0) continue;

            Point from = start.add(0, 0, z);
            Point to = from.add(size.blockX(), 1, 4);

            StripType type = getStripTypeAt(oldType, from.blockZ());

            if (oldType == type) {
                block = getNextBlock(block);
            } else {
                block = getBlockForStrip(type, 0);
            }

            unit.modifier().fill(from, to, block);

            oldType = type;
        }

        /*if (start.chunkZ() < 2) return;

        for (int z = 0; z <= size.blockZ(); z++) {
            StripType type = getStripTypeAt(z);
            for (int x = 0; x < size.blockX(); x++) {
                for (int y = 0; y < 5; y++) {
                    unit.modifier().setBlock(start.add(x, y, z), getBlockForStrip(type, y));
                }
            }
        }*/




        /*for (int z = 0; z <= size.blockZ(); z++) {
            StripType type = getStripTypeAt(z);
            for (int x = 0; x < size.blockX(); x++) {
                Point current = start.add(x, 0, z);

                // ((start.chunkX() > 0 && start.chunkX() < -4) || (start.chunkZ() > 0 && start.chunkZ() < -4))

                if ((start.chunkX() > -2 && start.chunkX() < 2) && (start.chunkZ() > -2 && start.chunkZ() < 2)) {
                    unit.modifier().setBlock(start.add(x, 0, z), z % 8 < 4 ? Block.LIME_CONCRETE_POWDER : Block.LIME_CONCRETE);
                }


                if (current.chunkZ() >= 2 && (current.chunkX() <= 1 && current.chunkX() >= -1)) {
                    for (int y = 0; y < 5; y++) {
                        unit.modifier().setBlock(start.add(x, y, z), getBlockForStrip(type, y));
                    }
                }
            }
        }*/
    }

    private Block getNextBlock(@Nullable Block block) {
        if (block == Block.LIME_CONCRETE) return Block.GREEN_CONCRETE;
        if (block == Block.GREEN_CONCRETE) return Block.LIME_CONCRETE;
        if (block == Block.BLACK_CONCRETE) return Block.GRAY_CONCRETE;
        if (block == Block.GRAY_CONCRETE) return Block.BLACK_CONCRETE;
        if (block == Block.LIGHT_GRAY_CONCRETE) return Block.WHITE_CONCRETE;
        if (block == Block.WHITE_CONCRETE) return Block.LIGHT_GRAY_CONCRETE;
        if (block == Block.BLUE_CONCRETE) return Block.LIGHT_BLUE_CONCRETE;
        if (block == Block.LIGHT_BLUE_CONCRETE) return Block.BLUE_CONCRETE;
        return Block.LIME_CONCRETE;
    }

    private StripType getStripTypeAt(@Nullable StripType type, int z) {
        Random random = new Random(seed + z);

        if (type != null && random.nextInt(5) < 3) return type;

        StripType newType;

        int roll = random.nextInt(100);
        if (roll < 15) {
            newType = StripType.TRAIN;
        } else if (roll < 45) {
            newType = StripType.GRASS;
        } else if (roll < 70) {
            newType = StripType.RIVER;
        } else {
            newType = StripType.ROAD;
        }

        if (newType == type) return getStripTypeAt(type, z + 1);
        return newType;
    }

    private Block getBlockForStrip(StripType type, int y) {
        return switch (type) {
            case ROAD -> y == 0 ? Block.BLACK_CONCRETE : Block.AIR;
            case TRAIN -> y == 0 ? Block.LIGHT_GRAY_CONCRETE : Block.AIR;
            case RIVER -> y == 0 ? Block.BLUE_CONCRETE : Block.AIR;
            case GRASS -> y == 0 ? Block.LIME_CONCRETE : Block.AIR;
        };
    }
}
