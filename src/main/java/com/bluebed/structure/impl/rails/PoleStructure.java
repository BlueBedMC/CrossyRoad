package com.bluebed.structure.impl.rails;

import com.bluebed.structure.Structure;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

public class PoleStructure implements Structure {
    @Override
    public void generate(Instance instance, GenerationUnit unit, Pos spawn) {
//        boolean white = false;
//
//        for (int i = 0; i < 10; i++) {
//            double length = white ? 0.4 : 0.8;
//            Block block = white ? Block.WHITE_CONCRETE : Block.RED_CONCRETE;
//
//            // move halfway to the center of this segment
//            spawn = spawn.add(0, length / 2, 0);
//            createPolePart(instance, spawn, length, block);
//
//            // move the other half to the top of the segment
//            spawn = spawn.add(0, length / 2, 0);
//
//            white = !white;
//        }

        createPolePart(instance, spawn, 0.8, Block.RED_CONCRETE);
        createPolePart(instance, spawn.add(0, 0.8, 0), 0.4, Block.WHITE_CONCRETE);
        createPolePart(instance, spawn.add(0, 1.2, 0), 0.8, Block.RED_CONCRETE);
        createPolePart(instance, spawn.add(0, 2.0, 0), 0.4, Block.WHITE_CONCRETE);
        createPolePart(instance, spawn.add(0, 2.4, 0), 0.8, Block.RED_CONCRETE);
        createPolePart(instance, spawn.add(0, 3.2, 0), 0.4, Block.WHITE_CONCRETE);
        createPolePart(instance, spawn.add(0, 3.6, 0), 0.8, Block.RED_CONCRETE);
        createPolePart(instance, spawn.add(0, 4.4, 0), 0.4, Block.WHITE_CONCRETE);
        createPolePart(instance, spawn.add(0, 4.8, 0), 0.8, Block.RED_CONCRETE);
        createPolePart(instance, spawn.add(0, 5.6, 0), 0.4, Block.WHITE_CONCRETE);
    }

    private void createPolePart(Instance instance, Pos pos, double length, Block block) {
        Entity blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setScale(meta.getScale().withX(0.4).withZ(0.4).withY(length));
        });
        blockDisplay.setNoGravity(true);

        blockDisplay.setInstance(instance, pos);
    }

}
