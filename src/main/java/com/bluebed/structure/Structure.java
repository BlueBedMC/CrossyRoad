package com.bluebed.structure;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.generator.GenerationUnit;

public interface Structure {
    void generate(Instance instance, GenerationUnit unit, Pos spawn);
}
