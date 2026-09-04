package com.krimx.gamefixes;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.List;

public class LootBagComponents {

    public static final DataComponentType<List<Identifier>> LOOT_BAG_CONTENTS =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            "loot_bag_contents"
                    ),
                    DataComponentType.<List<Identifier>>builder()
                            .persistent(
                                    Codec.list(Identifier.CODEC)
                            )
                            .build()
            );

    public static void initialize() {
        // Registration happens when this class is loaded.
    }
}
