package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record MaceChargePayload(UUID playerId)
        implements CustomPacketPayload {

    public static final Type<MaceChargePayload> TYPE =
            new Type<>(Identifier.parse(
                    Gamefixes.MOD_ID + ":mace_charge"
            ));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            MaceChargePayload
            > CODEC =
            StreamCodec.of(
                    (buf, payload) ->
                            buf.writeUUID(payload.playerId()),
                    buf ->
                            new MaceChargePayload(buf.readUUID())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}