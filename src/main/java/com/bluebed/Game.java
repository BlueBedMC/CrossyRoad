package com.bluebed;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.network.packet.server.play.EntityHeadLookPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Player player;
    private final Instance instance;
    private Entity armorStand;
    private Entity chicken;

    private boolean removed = false;

    private float chickenYaw = 0;
    private long lastInput = System.currentTimeMillis();

    public Game(Player player, Instance instance) {
        this.player = player;
        this.instance = instance;
    }

    public void init() {
        // chicken
        chicken = new Entity(EntityType.CHICKEN);
        Pos spawnPosition = new Pos(0, 72, -2);
        chicken.setInstance(instance, spawnPosition);

        List<EntityAttributesPacket.Property> properties = List.of(
                new EntityAttributesPacket.Property(Attribute.SCALE, 3.6, new ArrayList<>())
        );
        player.sendPacket(new EntityAttributesPacket(chicken.getEntityId(), properties));

        // armor stand
        Pos playerPos = chicken.getPosition().add(-8.5, 14, -11).withPitch(45).withYaw((float) -37.5);
        player.teleport(playerPos);
        player.lookAt(chicken);
        player.setNoGravity(true);

        armorStand = new Entity(EntityType.ARMOR_STAND);
        armorStand.setNoGravity(true);
        armorStand.setInvisible(true);
        armorStand.setSilent(true);
        armorStand.editEntityMeta(ArmorStandMeta.class, meta -> {
            meta.setMarker(true);
        });

        armorStand.setInstance(instance, playerPos);
        armorStand.addPassenger(player);
    }

    public void tick() {
        if (removed) return;
        Pos playerPos = chicken.getPosition().add(-8.5, 14, -11).withPitch(45).withYaw((float) -37.5);
        armorStand.teleport(playerPos);

        chickenLookAndRotation(player);
    }

    private void chickenLookAndRotation(Player player) {
        float yaw = chickenYaw;

        player.sendPacket(new EntityRotationPacket(
                chicken.getEntityId(),
                chickenYaw,
                chicken.getPosition().pitch(),
                chicken.isOnGround()
        ));

        player.sendPacket(new EntityHeadLookPacket(chicken.getEntityId(), yaw));
    }

    public void onInputPacket(ClientInputPacket packet) {
        if (removed) return;
        Pos pos = chicken.getPosition();

        if (lastInput > System.currentTimeMillis() - 150) return;
        lastInput = System.currentTimeMillis();

        float yaw = pos.yaw();
        if (packet.left()) yaw = -90;
        if (packet.forward()) yaw = 0;
        if (packet.backward())  yaw = -180;
        if (packet.right()) yaw = 90;

        chickenYaw = yaw;
        pos = pos.withYaw(chickenYaw);

        Vec dir = pos.direction().normalize();
        dir = dir.mul(4);
        pos = pos.add(dir);

        chicken.teleport(pos);

        for (Player p : chicken.getViewers()) {
            chickenLookAndRotation(p);
        }
    }

    public void remove() {
        this.chicken.remove();
        this.armorStand.remove();
        this.removed = true;
    }
}
