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
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.play.ClientInputPacket;

import java.util.HashMap;
import java.util.UUID;

public class Main {

    private static final HashMap<UUID, Game> games = new HashMap<>();
    private static final StripRegistry stripRegistry = new StripRegistry();

    private static BossBar bossBar;
    private static InstanceContainer lobbyInstance;

    static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        bossBar = BossBar.bossBar(
                Component.text("MSPT: 0"),
                1,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );

        lobbyInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        lobbyInstance.setChunkSupplier(LightingChunk::new);
        lobbyInstance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();

        registerPlayerEvents(events);
        registerTickEvents(events);

        MinestomFluids.init();
        MinecraftServer.getGlobalEventHandler().addChild(MinestomFluids.events());

        server.start("0.0.0.0", 25565);
    }


    private static void registerPlayerEvents(GlobalEventHandler events) {
        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(lobbyInstance);
        });

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            if (!event.isFirstSpawn()) return;

            InstanceContainer gameInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
            gameInstance.setChunkSupplier(LightingChunk::new);

            Game game = new Game(player, gameInstance);
            gameInstance.setGenerator(new CrossyChunkCreator(game, gameInstance, 123, stripRegistry));

            games.put(player.getUuid(), game);

            player.setInstance(gameInstance);
            player.setGameMode(GameMode.CREATIVE);
            //player.showBossBar(bossBar);

            game.init();
        });

        MinecraftServer.getPacketListenerManager().setListener(
                ConnectionState.PLAY,
                ClientInputPacket.class,
                (packet, connection) -> {
                    Player player = connection.getPlayer();
                    if (player == null) return;

                    Game game = games.get(player.getUuid());
                    if (game != null) game.onInputPacket(packet);
                }
        );

        events.addListener(PlayerDisconnectEvent.class, event -> {
            Game game = games.remove(event.getPlayer().getUuid());
            if (game == null) return;

            Instance instance = game.getInstance();

            game.remove();

            MinecraftServer.getInstanceManager().unregisterInstance(instance);
        });

        events.addListener(PlayerChatEvent.class, event -> {
            Player player = event.getPlayer();
            String rawMessage = event.getRawMessage();
            if (rawMessage.equalsIgnoreCase("titlesequence")) {
                Game game = games.get(event.getPlayer().getUuid());
                if (game == null) return;
                event.setCancelled(true);
                game.startTitleAnimation();
            }
        });
    }


    private static void registerTickEvents(GlobalEventHandler events) {
        events.addListener(ServerTickMonitorEvent.class, new java.util.function.Consumer<>() {
            long last = 0;

            @Override
            public void accept(ServerTickMonitorEvent event) {
                long now = System.currentTimeMillis();
                if (now - last < 500) return;
                last = now;

                double mspt = event.getTickMonitor().getTickTime();

                TextComponent text = Component.text("MSPT: ")
                        .color(TextColor.color(0x00FF00))
                        .append(Component.text("%.2fms".formatted(mspt))
                                .color(TextColor.color(0xFFFF00))
                        );

                bossBar.name(text);
                bossBar.progress(mspt >= 50 ? 1 : (float) mspt / 50);
            }
        });

        events.addListener(InstanceTickEvent.class, e -> {
            for (Game game : games.values()) {
                game.tick();
            }
        });
    }
}
