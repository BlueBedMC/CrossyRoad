package com.bluebed.strip;

import com.bluebed.Game;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

public interface StripGenerator {
    Block generate(Game game, Instance instance, GenerationUnit unit, Block block, Point from, Point to);
}
