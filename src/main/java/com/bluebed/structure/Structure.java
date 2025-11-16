package com.bluebed.structure;

import lombok.RequiredArgsConstructor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;

import java.util.Random;

@RequiredArgsConstructor
public abstract class Structure {
    protected final static Random random = new Random();

    protected final Pos spawn;

    public abstract void generate(Instance instance, GenerationUnit unit);
    public abstract void tick(Instance instance, Generator generator);
}
