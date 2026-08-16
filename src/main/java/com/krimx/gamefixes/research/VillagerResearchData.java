package com.krimx.gamefixes.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record VillagerResearchData(
        int slotCount,
        List<String> slots
) {
    private static final Codec<List<String>> SLOTS_CODEC =
            Codec.STRING.listOf();

    public static final Codec<VillagerResearchData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("slot_count")
                                    .forGetter(
                                            VillagerResearchData::slotCount
                                    ),
                            SLOTS_CODEC.fieldOf("slots")
                                    .forGetter(
                                            VillagerResearchData::slots
                                    )
                    ).apply(
                            instance,
                            VillagerResearchData::new
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            VillagerResearchData
            > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            VillagerResearchData::slotCount,
            ByteBufCodecs.STRING_UTF8.apply(
                    ByteBufCodecs.list()
            ),
            VillagerResearchData::slots,
            VillagerResearchData::new
    );

    public VillagerResearchData {
        if (slotCount < 0) {
            throw new IllegalArgumentException(
                    "Research slot count cannot be negative"
            );
        }

        slots = List.copyOf(slots);

        if (slots.size() != slotCount) {
            throw new IllegalArgumentException(
                    "Research slot count does not match slot list size"
            );
        }
    }

    public static VillagerResearchData empty(
            int slotCount
    ) {
        return new VillagerResearchData(
                slotCount,
                Collections.nCopies(
                        slotCount,
                        "empty"
                )
        );
    }

    public ResearchProject getResearch(
            int slot
    ) {
        if (slot < 0
                || slot >= slots.size()) {
            return null;
        }

        String value =
                slots.get(slot);

        if ("empty".equals(value)) {
            return null;
        }

        return ResearchRegistry.get(value);
    }

    public boolean isEmpty(
            int slot
    ) {
        return getResearch(slot) == null;
    }

    public VillagerResearchData withSlotCount(
            int newSlotCount
    ) {
        if (newSlotCount == slotCount) {
            return this;
        }

        if (newSlotCount < 0) {
            throw new IllegalArgumentException(
                    "Research slot count cannot be negative"
            );
        }

        List<String> updated =
                new ArrayList<>(slots);

        while (updated.size() < newSlotCount) {
            updated.add("empty");
        }

        while (updated.size() > newSlotCount) {
            updated.remove(
                    updated.size() - 1
            );
        }

        return new VillagerResearchData(
                newSlotCount,
                updated
        );
    }

    public VillagerResearchData withResearch(
            int slot,
            ResearchProject project
    ) {
        if (slot < 0
                || slot >= slots.size()) {
            return this;
        }

        List<String> updated =
                new ArrayList<>(slots);

        updated.set(
                slot,
                project == null
                        ? "empty"
                        : project.getId()
        );

        return new VillagerResearchData(
                slotCount,
                updated
        );
    }

    public VillagerResearchData clearResearch(
            int slot
    ) {
        return withResearch(
                slot,
                null
        );
    }
}