package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.HoneycombBootsWallJump;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class HoneycombNetworking {

    private HoneycombNetworking() {
    }

    public static void initialize() {
        PayloadTypeRegistry.serverboundPlay().register(
                HoneycombWallJumpPayload.TYPE,
                HoneycombWallJumpPayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                HoneycombWallJumpPayload.TYPE,
                (payload, context) -> {
                    ServerPlayer player = context.player();

                    context.server().execute(() -> {
                        boolean canWallJump =
                                HoneycombBootsWallJump.canWallJump(player);

                        if (!canWallJump) {
                            return;
                        }

                        HoneycombBootsWallJump.performWallJump(player);
                    });
                }
        );
    }
}