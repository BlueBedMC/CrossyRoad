package com.bluebed;

import com.bluebed.font.GoldFont;
import com.bluebed.font.NormalFont;
import com.bluebed.font.SeparatorFont;
import com.bluebed.structure.Structure;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.client.play.ClientInputPacket;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.network.packet.server.play.EntityHeadLookPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Game {
    private final Player player;
    private final Instance instance;
    private Entity armorStand;
    private Entity chicken;
    private BossBar scoreBossBar;
    private BossBar coinsBossBar;

    private boolean removed = false;
    private boolean started = false;

    private int currentScore = 0,
        bestScore = 0;
    private int coins = 0;

    private float chickenYaw = 0;
    private long lastInput = System.currentTimeMillis();

    private final Set<Structure> structures = ConcurrentHashMap.newKeySet();

    public Game(Player player, Instance instance) {
        this.player = player;
        this.instance = instance;
    }

    public void init() {
        // for stats and shi ig
        scoreBossBar = BossBar.bossBar(
                Component.text(""),
                0.5F,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );

        coinsBossBar = BossBar.bossBar(
                Component.text(""),
                0.5F,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );

        player.showBossBar(scoreBossBar);
        player.showBossBar(coinsBossBar);

        // chicken
        chicken = new Entity(EntityType.CHICKEN);
        Pos spawnPosition = new Pos(0, 72, -2).withYaw(-180);
        chicken.setInstance(instance, spawnPosition);
        chickenYaw = -180;

        // armor stand
        Pos playerPos = chicken.getPosition().add(-13, 35, -27).withYaw((float) -23).withPitch(42);
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
        player.setGameMode(GameMode.SPECTATOR);
        player.spectate(armorStand);

        List<EntityAttributesPacket.Property> properties = List.of(
                new EntityAttributesPacket.Property(Attribute.SCALE, 3.6, new ArrayList<>())
        );
        player.sendPacket(new EntityAttributesPacket(chicken.getEntityId(), properties));

        startTitleAnimation();
    }

    public void tick() {
        if (removed) return;

        Pos playerPos = chicken.getPosition().add(-13, 35, -27).withYaw((float) -23).withPitch(42);
        armorStand.teleport(playerPos);

        chickenLookAndRotation(player);
        updateBossBar();

        for (Structure structure : structures) {
            structure.tick(instance, instance.generator());
        }
    }

    private void updateBossBar() {
        String scoreStr = Integer.toString(currentScore);
        StringBuilder score = new StringBuilder();

        for (char c : scoreStr.toCharArray()) {
            int digit = c - '0';
            score.append(NormalFont.getUnicodeFromNumber(digit));
        }

        String scoreString = SeparatorFont.SCORE_LEFT.getUnicode() +
                SeparatorFont.getUnicodeAmountForScore(score.length(), currentScore) +
                score;

        scoreBossBar.name(Component.text(scoreString));

        // update two different boss bars because FUCK UNICODE CHARACTERS BRO
        String coinsStr = Integer.toString(coins);
        StringBuilder coinsBuilder = new StringBuilder();

        for (char c : coinsStr.toCharArray()) {
            int digit = c - '0';
            coinsBuilder.append(GoldFont.getUnicodeFromNumber(digit));
        }

        String coinsString = SeparatorFont.COINS_RIGHT.getUnicode() +
                SeparatorFont.getUnicodeAmountForScore(coinsBuilder.length(), coins) +
                coinsBuilder +
                GoldFont.COIN.getUnicode();

        coinsBossBar.name(Component.text(coinsString));
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

        boolean pressed = packet.left() || packet.right() || packet.forward() || packet.backward();
        if (!pressed) return;

        if (lastInput > System.currentTimeMillis() - 150) return;
        lastInput = System.currentTimeMillis();

        if (!started) started = true;

        Pos pos = chicken.getPosition();
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

        if (packet.forward()) currentScore++;

        for (Player p : chicken.getViewers()) {
            chickenLookAndRotation(p);
        }
    }

    public void remove() {
        this.chicken.remove();
        this.armorStand.remove();
        this.removed = true;
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    private void startTitleAnimation() {
        final Task[] holder = new Task[2];

        player.showTitle(Title.title(
                Component.text("\uE05A"),
                Component.text("\uE05B"),
                0,
                5,
                0
        ));

        holder[0] = instance.scheduler().scheduleTask(new Runnable() {
            int i = 100;

            @Override
            public void run() {
                if (i == 120) {
                    player.sendActionBar(Component.text("\uE05A"));
                    player.showTitle(Title.title(
                            Component.text(""),
                            Component.text("\uE05B"),
                            0,
                            Integer.MAX_VALUE,
                            0
                    ));
                    holder[0].cancel();
                }

                int max = 20;
                int count = max - (i - 100);

                StringBuilder builder = new StringBuilder();
                for (int x = 0; x < count; x++) {
                    builder.append("\uE05C").append("\uE05D");
                }

                player.sendActionBar(Component.text("\uE05A"));
                player.showTitle(Title.title(
                        Component.text(""),
                        Component.text(builder + "\uE05B"),
                                0,
                                200,
                        0
                        ));

                i++;
            }
        }, TaskSchedule.tick(20), TaskSchedule.tick(2));


        holder[1] = instance.scheduler().scheduleTask(() -> {
            player.showTitle(Title.title(
                    Component.text(""),
                    Component.text("\uE05B"),
                    0,
                    Integer.MAX_VALUE,
                    0
            ));

            player.sendActionBar(Component.text("\uE04A"));

            if (!started) return;
            holder[1].cancel();
            endTitleAnimation();
        }, TaskSchedule.tick(110), TaskSchedule.tick(1));
    }

    private void endTitleAnimation() {
        player.showTitle(Title.title(
                Component.text(""),
                Component.text(""),
                0,
                Integer.MAX_VALUE,
                0
        ));
        player.sendActionBar(Component.text(""));
    }
}
