package com.bluebed.strip;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.generator.GenerationUnit;

public interface StripGenerator {
    void generate(GenerationUnit unit, Point start, int startZ, int length);
}
