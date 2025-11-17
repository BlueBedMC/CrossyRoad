package com.bluebed.structure.impl.rails;

import com.bluebed.structure.Structure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class PoleStructure extends Structure {
    private Entity leftLight;
    private Entity rightLight;
    private boolean ringing = false;
    private long lastRung = System.currentTimeMillis();

    public PoleStructure(Pos spawn) {
        super(spawn);
    }

    @Override
    public void generate(Instance instance, GenerationUnit unit) {
        boolean white = false;
        double offset = 0;

        for (int i = 0; i < 10; i++) {
            double height = white ? 0.4 : 0.8;
            Block block = white ? Block.WHITE_CONCRETE : Block.RED_CONCRETE;

            createPolePart(instance, spawn.add(0, offset, 0), height, block);

            offset += height;
            white = !white;
        }

        // big box
        createPolePart(instance, spawn.add(-1.25, 3.8, -0.4), 3, 1.5, 0.4, Block.BLACK_CONCRETE);

        // small boxes
        createPolePart(instance, spawn.add(0.65, 4.8, -0.8), 0.4, 0.4, 0.4, Block.BLACK_CONCRETE);
        createPolePart(instance, spawn.add(-0.65, 4.8, -0.8), 0.4, 0.4, 0.4, Block.BLACK_CONCRETE);

        leftLight = createPolePart(instance, spawn.add(0.65, 4.4, -0.8), 0.4, 0.4, 0.4, Block.BLACK_STAINED_GLASS);
        rightLight = createPolePart(instance, spawn.add(-0.65, 4.4, -0.8), 0.4, 0.4, 0.4, Block.BLACK_STAINED_GLASS);
    }

    @Override
    public void tick(Instance instance, Generator generator) {
        if (spawn == null) return;
        if (ringing) return;
        if (leftLight == null || rightLight == null) return;
        if (leftLight.getViewers().isEmpty()) return;

        int num = random.nextInt(2000);
        if (num != 67) return;

        if (!(lastRung > System.currentTimeMillis() - 4000)) return;

        ringing = true;

        final Task[] holder = new Task[1];

        for (Player viewer : leftLight.getViewers()) {
            viewer.playSound(
                    Sound.sound(
                            Key.key("minecraft:train.alarm"),
                            Sound.Source.MASTER,
                            1.0f,
                            1.0f
                    ),
                    viewer.getPosition()
            );
        }

        holder[0] = instance.scheduler().scheduleTask(new Runnable() {
            int runs = 0;

            @Override
            public void run() {
                System.out.println("run");
                // 2.5s
                if (runs > 6) {
                    // send train after a small wait

                    for (Player viewer : leftLight.getViewers()) {
                        viewer.playSound(
                                Sound.sound(
                                        Key.key("minecraft:train.pass"),
                                        Sound.Source.MASTER,
                                        1.0f,
                                        1.0f
                                ),
                                viewer.getPosition()
                        );
                    }

                    setDisplayBlock(leftLight, Block.BLACK_CONCRETE);
                    setDisplayBlock(rightLight, Block.BLACK_CONCRETE);
                    lastRung = System.currentTimeMillis();
                    holder[0].cancel();
                    return;
                }

                if (runs == 0) {
                    setDisplayBlock(leftLight, Block.RED_STAINED_GLASS);
                } else if (runs < 5) {
                    switchLights();
                }

                runs++;
            }
        }, TaskSchedule.immediate(), TaskSchedule.tick(5));
    }

    private void switchLights() {
        if (leftLight == null || rightLight == null) return;
        BlockDisplayMeta leftMeta = (BlockDisplayMeta) leftLight.getEntityMeta();

        if (leftMeta.getBlockStateId() == Block.RED_STAINED_GLASS) {
            setDisplayBlock(leftLight, Block.BLACK_STAINED_GLASS);
            setDisplayBlock(rightLight, Block.RED_STAINED_GLASS);
        } else {
            setDisplayBlock(rightLight, Block.BLACK_STAINED_GLASS);
            setDisplayBlock(leftLight, Block.RED_STAINED_GLASS);
        }
    }

    private void setDisplayBlock(Entity entity, Block block) {
        if (!(entity.getEntityMeta() instanceof BlockDisplayMeta)) return;
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
        });
    }

    private Entity createPolePart(Instance instance, Pos pos, double width, double height, double length, Block block) {
        Entity blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setScale(meta.getScale().withX(width).withZ(length).withY(height));
        });
        blockDisplay.setNoGravity(true);

        blockDisplay.setInstance(instance, pos);
        return blockDisplay;
    }

    private Entity createPolePart(Instance instance, Pos pos, double height, Block block) {
        Entity blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setScale(meta.getScale().withX(0.4).withZ(0.4).withY(height));
        });
        blockDisplay.setNoGravity(true);

        blockDisplay.setInstance(instance, pos);
        return blockDisplay;
    }

}
