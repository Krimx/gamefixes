package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class MaceNetworking {

    public static void registerCommon() {
        PayloadTypeRegistry.clientboundPlay().register(
                MaceSwingPayload.TYPE,
                MaceSwingPayload.CODEC
        );

        PayloadTypeRegistry.clientboundPlay().register(
                MaceChargePayload.TYPE,
                MaceChargePayload.CODEC
        );
    }

    public static void sendChargeStart(ServerPlayer source) {
        MaceChargePayload payload =
                new MaceChargePayload(source.getUUID());

        for (ServerPlayer player : PlayerLookup.tracking(source)) {
            ServerPlayNetworking.send(player, payload);
        }

        ServerPlayNetworking.send(source, payload);
    }

    public static void sendSwing(ServerPlayer source) {
        MaceSwingPayload payload =
                new MaceSwingPayload(source.getUUID());

        for (ServerPlayer player : PlayerLookup.tracking(source)) {
            ServerPlayNetworking.send(player, payload);
        }

        ServerPlayNetworking.send(source, payload);
    }
}