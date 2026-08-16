package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AttemptResearchPayload(
        int containerId,
        int researchSlot
) implements CustomPacketPayload {

    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(
                    Gamefixes.MOD_ID,
                    "attempt_research"
            );

    public static final Type<AttemptResearchPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            AttemptResearchPayload
            > CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            AttemptResearchPayload::containerId,

            ByteBufCodecs.VAR_INT,
            AttemptResearchPayload::researchSlot,

            AttemptResearchPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}