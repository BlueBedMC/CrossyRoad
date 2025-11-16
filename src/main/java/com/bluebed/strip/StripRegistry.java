package com.bluebed.strip;

import com.bluebed.structure.impl.rails.PoleStructure;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;

import java.util.Map;

public class StripRegistry {
    private final Map<StripType, StripGenerator> generators = Map.of(
            StripType.GRASS, ((game, instance, unit, block, from, to) -> {
                Block newBlock = block == Block.LIME_CONCRETE ? Block.GREEN_CONCRETE : Block.LIME_CONCRETE;
                unit.modifier().fill(from, to, newBlock);
                return newBlock;
            }),
            StripType.ROAD, ((game, instance, unit, block, from, to) -> {
                unit.modifier().fill(from, to, Block.BLACK_CONCRETE);
                return Block.BLACK_CONCRETE;
            }),
            StripType.MOTORWAY, ((game, instance, unit, block, from, to) -> {
                unit.modifier().fill(from, to, Block.BLACK_CONCRETE);
                return Block.BLACK_CONCRETE;
            }),
            StripType.RIVER, ((game, instance, unit, block, from, to) -> {
                unit.modifier().fill(from, to, Block.BLUE_CONCRETE);
                return Block.WATER;
            }),
            StripType.TRAIN, ((game, instance, unit, block, from, to) -> {
                unit.modifier().fill(from, to, Block.BLACK_CONCRETE);

                double width = 0.4,
                        height = 0.4,
                        length = to.x() - from.x();
                double spacing = 4;

                Pos railsFrom = from.add(0, 1, 0).asPos();

                Entity leftRail = new Entity(EntityType.BLOCK_DISPLAY);
                leftRail.editEntityMeta(BlockDisplayMeta.class, meta -> {
                    meta.setBlockState(Block.LIGHT_GRAY_CONCRETE);
                    meta.setScale(meta.getScale().withX(length).withY(height).withZ(width));
                });
                leftRail.setInstance(instance, railsFrom.add(0, height * 2, 0.5));

                Entity rightRail = new Entity(EntityType.BLOCK_DISPLAY);
                rightRail.editEntityMeta(BlockDisplayMeta.class, meta -> {
                    meta.setBlockState(Block.LIGHT_GRAY_CONCRETE);
                    meta.setScale(meta.getScale().withX(length).withY(height).withZ(width));
                });
                rightRail.setInstance(instance, railsFrom.add(0, height * 2, spacing - 0.5 - (width)));

                for (int x = 0; x < length; x++) {
                    // one per big block
                    if (x % 4 != 0) continue;

                    Entity railBase = new Entity(EntityType.BLOCK_DISPLAY);
                    railBase.editEntityMeta(BlockDisplayMeta.class, meta -> {
                        meta.setBlockState(Block.BROWN_CONCRETE);
                        meta.setScale(meta.getScale().withX(width).withY(height / 2).withZ(spacing));
                    });
                    railBase.setInstance(instance, railsFrom.add(x + 2, 0, 0));
                }

                PoleStructure poleStructure = new PoleStructure(railsFrom.withX(-2));
                poleStructure.generate(instance, unit);

                game.addStructure(poleStructure);

                return Block.BLACK_CONCRETE;
            })
    );

    public StripGenerator getGenerator(StripType type) {
        return generators.get(type);
    }
}
