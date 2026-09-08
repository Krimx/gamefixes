package com.krimx.gamefixes.enchantment;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    private static ResourceKey<Enchantment> key(String path) {
        Identifier id =
                Identifier.fromNamespaceAndPath(
                        Gamefixes.MOD_ID,
                        path
                );

        return ResourceKey.create(
                Registries.ENCHANTMENT,
                id
        );
    }

    public static final ResourceKey<Enchantment> ABUNDANCE =
            key("abundance");
    public static final ResourceKey<Enchantment> HYDRATION =
            key("hydration");
}