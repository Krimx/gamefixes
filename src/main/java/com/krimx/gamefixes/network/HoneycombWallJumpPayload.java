package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HoneycombWallJumpPayload()
        implements CustomPacketPayload {

    public static final Identifier ID =
            Identifier.parse(
                    Gamefixes.MOD_ID + ":honeycomb_wall_jump"
            );

    public static final Type<HoneycombWallJumpPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            HoneycombWallJumpPayload
            > CODEC =
            StreamCodec.unit(
                    new HoneycombWallJumpPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}