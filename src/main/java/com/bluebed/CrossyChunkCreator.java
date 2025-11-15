package com.bluebed;

import com.bluebed.strip.StripType;
import lombok.RequiredArgsConstructor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@RequiredArgsConstructor
public class CrossyChunkCreator implements Generator {
    private final Random random;
    private final int chunkWidth = 16; // standard
    private final int chunkDepth = 16;
    private final int lobbyLength = 8;

    public CrossyChunkCreator(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        final Point start = unit.absoluteStart().withY(70);
        final Point size = unit.size();

        if (start.chunkX() < -2 || start.chunkX() > 2) return;

        Block currentBlock = getNextBlock(null);
        for (int z = 0; z < size.blockZ(); z++) {
            if (z % 4 != 0) continue;

            Point from = start.add(0, 0, z);
            Point to = from.add(size.blockX(), 1, 4);
            unit.modifier().fill(from, to, currentBlock);

            currentBlock = getNextBlock(currentBlock);
        }

        if (start.chunkZ() < 2) return;

        for (int z = 0; z <= size.blockZ(); z++) {
            StripType type = getStripTypeAt(z);
            for (int x = 0; x < size.blockX(); x++) {
                for (int y = 0; y < 5; y++) {
                    unit.modifier().setBlock(start.add(x, y, z), getBlockForStrip(type, y));
                }
            }
        }




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
        if (block == Block.LIME_CONCRETE) return Block.LIME_CONCRETE_POWDER;
        return Block.LIME_CONCRETE;
    }

    private StripType getStripTypeAt(int z) {
        if (z < lobbyLength) return StripType.GRASS;

        random.setSeed(z);
        int roll = random.nextInt(100);

        if (roll < 50) return StripType.ROAD;
        else if (roll < 70) return StripType.TRAIN;
        else if (roll < 90) return StripType.RIVER;
        else return StripType.GRASS;
    }

    private Block getBlockForStrip(StripType type, int y) {
        return switch (type) {
            case ROAD -> y == 0 ? Block.BLACK_CONCRETE : Block.AIR;
            case TRAIN -> y == 0 ? Block.IRON_BLOCK : Block.AIR;
            case RIVER -> y == 0 ? Block.WATER : Block.AIR;
            case GRASS -> y == 0 ? Block.GRASS_BLOCK : Block.AIR;
        };
    }
}
