package com.bluebed;

import com.bluebed.strip.StripRegistry;
import com.bluebed.strip.StripType;
import lombok.RequiredArgsConstructor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@RequiredArgsConstructor
public class CrossyChunkCreator implements Generator {
    private final Game game;
    private final Instance instance;
    private final int seed;
    private final StripRegistry registry;

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        final Point start = unit.absoluteStart().withY(70);
        final Point size = unit.size();

        if (start.chunkX() < -4 || start.chunkX() > 4) return;

        StripType oldType = null;
        Block block = null;
        for (int z = 0; z < size.blockZ(); z++) {
            if (z % 4 != 0) continue;

            Point from = start.add(0, 0, z);
            Point to = from.add(size.blockX(), 1, 4);

            StripType type = getStripTypeAt(oldType, from.blockZ());
            block = registry.getGenerator(type).generate(game, instance, unit, block, from, to);

            oldType = type;
        }
    }

    private StripType getStripTypeAt(@Nullable StripType type, int z) {
        Random random = new Random(seed + z);

        if (type != null && random.nextInt(5) < 3) return type;

        int roll = random.nextInt(100);
        StripType newType;

        if (roll < 15) {
            newType = StripType.TRAIN;
        } else if (roll < 45) {
            newType = StripType.GRASS;
        } else if (roll < 70) {
            newType = StripType.RIVER;
        } else if (roll < 75) {
            newType = StripType.KINDA_HOMELESS;
        } else {
            newType = StripType.ROAD;
        }

        if (newType == type) return getStripTypeAt(type, z + 1);
        return newType;
    }

    @Deprecated
    private Block getNextBlock(@NotNull StripType type, @Nullable Block block) {
        if (block == Block.LIME_CONCRETE) return Block.GREEN_CONCRETE;
        if (block == Block.GREEN_CONCRETE) return Block.LIME_CONCRETE;
        if (block == Block.BLACK_CONCRETE) return Block.GRAY_CONCRETE;
        if (block == Block.GRAY_CONCRETE) return Block.BLACK_CONCRETE;
        if (block == Block.LIGHT_GRAY_CONCRETE) return Block.WHITE_CONCRETE;
        if (block == Block.WHITE_CONCRETE) return Block.LIGHT_GRAY_CONCRETE;
        if (block == Block.WATER) return Block.WATER;
        return Block.LIME_CONCRETE;
    }
}
