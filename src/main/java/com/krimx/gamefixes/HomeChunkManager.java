package com.krimx.gamefixes;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HomeChunkManager {

    /**
     * Number of chunks outward from the player's spawn chunk.
     *
     * 1 = 3x3 chunks
     * 2 = 5x5 chunks
     */
    private static final int CHUNK_RADIUS = 1;

    /**
     * Check for spawnpoint changes once every second.
     */
    private static final int CHECK_INTERVAL = 20;

    private static int tickCounter = 0;

    /**
     * Stores the home location currently associated with each player.
     */
    private static final Map<UUID, HomeLocation> PLAYER_HOMES =
            new HashMap<>();

    private HomeChunkManager() {
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(
                HomeChunkManager::onServerTick
        );

        Gamefixes.LOGGER.info(
                "Home chunk loading initialized."
        );
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;

        if (tickCounter < CHECK_INTERVAL) {
            return;
        }

        tickCounter = 0;

        updateHomes(server);
    }

    private static void updateHomes(MinecraftServer server) {
        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            updatePlayerHome(
                    server,
                    player
            );
        }
    }

    private static void updatePlayerHome(
            MinecraftServer server,
            ServerPlayer player
    ) {
        UUID uuid = player.getUUID();

        /*
         * Minecraft 26.2 stores the player's spawn information
         * inside ServerPlayer.RespawnConfig.
         */
        ServerPlayer.RespawnConfig respawnConfig =
                player.getRespawnConfig();

        /*
         * No personal spawnpoint.
         *
         * If this player previously had a home, remove it.
         */
        if (respawnConfig == null) {
            removePlayerHome(
                    server,
                    uuid
            );

            return;
        }

        ResourceKey<Level> respawnDimension =
                respawnConfig.respawnData().dimension();

        /*
         * Home chunks are only maintained in the Overworld.
         */
        if (respawnDimension != Level.OVERWORLD) {
            removePlayerHome(
                    server,
                    uuid
            );

            return;
        }

        BlockPos respawnPosition =
                respawnConfig.respawnData().pos();

        /*
         * Convert the respawn block position into the chunk
         * containing that position.
         *
         * Block coordinates can be negative, so using
         * Math.floorDiv is important here.
         */
        int spawnChunkX =
                Math.floorDiv(
                        respawnPosition.getX(),
                        16
                );

        int spawnChunkZ =
                Math.floorDiv(
                        respawnPosition.getZ(),
                        16
                );

        HomeLocation newHome =
                new HomeLocation(
                        Level.OVERWORLD,
                        spawnChunkX,
                        spawnChunkZ
                );

        HomeLocation oldHome =
                PLAYER_HOMES.get(uuid);

        /*
         * Nothing changed.
         */
        if (newHome.equals(oldHome)) {
            return;
        }

        /*
         * Remove the player's old home area.
         */
        if (oldHome != null) {
            unforceHomeChunks(
                    server,
                    oldHome
            );
        }

        ServerLevel overworld =
                server.getLevel(
                        Level.OVERWORLD
                );

        if (overworld == null) {
            return;
        }

        /*
         * Force the new home area.
         */
        forceHomeChunks(
                overworld,
                spawnChunkX,
                spawnChunkZ
        );

        PLAYER_HOMES.put(
                uuid,
                newHome
        );

        Gamefixes.LOGGER.info(
                "Home chunks updated for {} at chunk [{}, {}].",
                player.getGameProfile().name(),
                spawnChunkX,
                spawnChunkZ
        );
    }

    private static void forceHomeChunks(
            ServerLevel level,
            int centerX,
            int centerZ
    ) {
        for (
                int x = centerX - CHUNK_RADIUS;
                x <= centerX + CHUNK_RADIUS;
                x++
        ) {
            for (
                    int z = centerZ - CHUNK_RADIUS;
                    z <= centerZ + CHUNK_RADIUS;
                    z++
            ) {
                level.setChunkForced(
                        x,
                        z,
                        true
                );
            }
        }
    }

    private static void unforceHomeChunks(
            MinecraftServer server,
            HomeLocation home
    ) {
        ServerLevel level =
                server.getLevel(
                        home.dimension()
                );

        if (level == null) {
            return;
        }

        for (
                int x = home.chunkX() - CHUNK_RADIUS;
                x <= home.chunkX() + CHUNK_RADIUS;
                x++
        ) {
            for (
                    int z = home.chunkZ() - CHUNK_RADIUS;
                    z <= home.chunkZ() + CHUNK_RADIUS;
                    z++
            ) {
                level.setChunkForced(
                        x,
                        z,
                        false
                );
            }
        }
    }

    private static void removePlayerHome(
            MinecraftServer server,
            UUID uuid
    ) {
        HomeLocation oldHome =
                PLAYER_HOMES.remove(uuid);

        if (oldHome == null) {
            return;
        }

        unforceHomeChunks(
                server,
                oldHome
        );
    }

    private record HomeLocation(
            ResourceKey<Level> dimension,
            int chunkX,
            int chunkZ
    ) {
    }
}