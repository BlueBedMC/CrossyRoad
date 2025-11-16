package com.bluebed;

import com.bluebed.strip.StripRegistry;
import io.github.togar2.fluids.MinestomFluids;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.play.ClientInputPacket;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Main {
    private static final HashMap<UUID, Game> games = new HashMap<>();
    private static final StripRegistry stripRegistry = new StripRegistry();

    private static BossBar bossBar;

    static void main() {
        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        bossBar = BossBar.bossBar(
                Component.text("MSPT: 0"),
                1,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );

        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkSupplier(LightingChunk::new);
        instance.setGenerator(new CrossyChunkCreator(instance, 123, stripRegistry));

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        registerEvents(eventHandler, instance);

        MinestomFluids.init();
        MinecraftServer.getGlobalEventHandler().addChild(MinestomFluids.events());

        server.start("0.0.0.0", 25565);
    }

    private static void registerEvents(GlobalEventHandler eventHandler, Instance instance) {
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.showBossBar(bossBar);

            Game game = new Game(player, instance);
            game.init();

            games.put(player.getUuid(), game);
        });

        MinecraftServer.getPacketListenerManager().setListener(
                ConnectionState.PLAY,
                ClientInputPacket.class,
                (packet, connection) -> {
                    Player player = connection.getPlayer();
                    if (player == null) return;
                    UUID uuid = player.getUuid();
                    Game game = games.get(uuid);
                    if (game == null) return;
                    game.onInputPacket(packet);
                }
        );


        eventHandler.addListener(ServerTickMonitorEvent.class, new Consumer<>() {
            long lastBossBarUpdate = System.currentTimeMillis();

            @Override
            public void accept(ServerTickMonitorEvent event) {
                if (System.currentTimeMillis() - lastBossBarUpdate < 500) return;
                lastBossBarUpdate = System.currentTimeMillis();

                double mspt = event.getTickMonitor().getTickTime();

                TextComponent bossBarMessage = Component
                        .text("MSPT: ")
                        .color(TextColor.color(0x00FF00))
                        .append(Component
                                .text("%.2fms".formatted(mspt))
                                .color(TextColor.color(0xFFFF00))
                        );
                bossBar.name(bossBarMessage);
                bossBar.progress(mspt >= 50 ? 1 : (float) mspt / 50);
            }
        });

        eventHandler.addListener(EntityTickEvent.class, _ -> {
            for (Game game : games.values()) {
                game.tick();
            }
        });

        eventHandler.addListener(PlayerDisconnectEvent.class, event -> {
           Player player = event.getPlayer();
           Game game = games.get(player.getUuid());
           if (game == null) return;
           game.remove();
        });
    }
}
