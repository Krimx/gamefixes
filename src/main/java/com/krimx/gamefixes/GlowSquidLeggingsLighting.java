package com.krimx.gamefixes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GlowSquidLeggingsLighting {

    private static final int LIGHT_LEVEL = 6;

    private static final Map<UUID, LightSource> SERVER_LIGHT_SOURCES =
            new ConcurrentHashMap<>();

    private static LightSource clientLightSource;

    private GlowSquidLeggingsLighting() {
    }

    public static boolean shouldEmitLight(Player player) {
        return player.getItemBySlot(EquipmentSlot.LEGS)
                .is(Gamefixes.GLOW_SQUID_LEGGINGS);
    }

    public static BlockPos getLightPosition(Player player) {
        return player.blockPosition();
    }

    public static boolean isLightSource(
            ServerLevel level,
            BlockPos pos
    ) {
        for (LightSource source : SERVER_LIGHT_SOURCES.values()) {
            if (source.level() == level
                    && source.pos().equals(pos)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isClientLightSource(BlockPos pos) {
        return clientLightSource != null
                && clientLightSource.pos().equals(pos);
    }

    public static int getLightLevel() {
        return LIGHT_LEVEL;
    }

    public static void tick(ServerPlayer player) {
        UUID uuid = player.getUUID();
        LightSource oldSource =
                SERVER_LIGHT_SOURCES.get(uuid);

        if (!shouldEmitLight(player)) {
            if (oldSource != null) {
                SERVER_LIGHT_SOURCES.remove(uuid);

                oldSource.level()
                        .getLightEngine()
                        .checkBlock(oldSource.pos());
            }

            return;
        }

        ServerLevel level = player.level();
        BlockPos newPos =
                getLightPosition(player);

        if (oldSource != null
                && oldSource.level() == level
                && oldSource.pos().equals(newPos)) {
            return;
        }

        if (oldSource != null) {
            SERVER_LIGHT_SOURCES.remove(uuid);

            oldSource.level()
                    .getLightEngine()
                    .checkBlock(oldSource.pos());
        }

        SERVER_LIGHT_SOURCES.put(
                uuid,
                new LightSource(
                        level,
                        newPos
                )
        );

        level.getLightEngine()
                .checkBlock(newPos);
    }

    public static void tick(Player player) {
        LightSource oldSource =
                clientLightSource;

        if (!shouldEmitLight(player)) {
            if (oldSource != null) {
                clientLightSource = null;

                oldSource.level()
                        .getLightEngine()
                        .checkBlock(oldSource.pos());
            }

            return;
        }

        Level level = player.level();
        BlockPos newPos =
                getLightPosition(player);

        if (oldSource != null
                && oldSource.level() == level
                && oldSource.pos().equals(newPos)) {
            return;
        }

        if (oldSource != null) {
            oldSource.level()
                    .getLightEngine()
                    .checkBlock(oldSource.pos());
        }

        clientLightSource =
                new LightSource(
                        level,
                        newPos
                );

        level.getLightEngine()
                .checkBlock(newPos);
    }

    public static void removePlayer(ServerPlayer player) {
        LightSource oldSource =
                SERVER_LIGHT_SOURCES.remove(
                        player.getUUID()
                );

        if (oldSource != null) {
            oldSource.level()
                    .getLightEngine()
                    .checkBlock(oldSource.pos());
        }
    }

    public static void clearClientLight() {
        LightSource oldSource =
                clientLightSource;

        if (oldSource != null) {
            clientLightSource = null;

            oldSource.level()
                    .getLightEngine()
                    .checkBlock(oldSource.pos());
        }
    }

    private record LightSource(
            Level level,
            BlockPos pos
    ) {
    }
}