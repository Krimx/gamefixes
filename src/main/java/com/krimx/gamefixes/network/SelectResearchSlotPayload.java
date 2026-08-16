package com.krimx.gamefixes.network;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SelectResearchSlotPayload(
        int containerId,
        int slot
) implements CustomPacketPayload {

    public static final Identifier ID =
            Identifier.fromNamespaceAndPath(
                    Gamefixes.MOD_ID,
                    "select_research_slot"
            );

    public static final Type<SelectResearchSlotPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SelectResearchSlotPayload
            > CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SelectResearchSlotPayload::containerId,

            ByteBufCodecs.VAR_INT,
            SelectResearchSlotPayload::slot,

            SelectResearchSlotPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}