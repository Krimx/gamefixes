package com.krimx.gamefixes;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class LootBagTags {

    public static final TagKey<Item> LOOT_BAG_TRIALS =
            create("loot_bag_trials");

    public static final TagKey<Item> LOOT_BAG_NETHER =
            create("loot_bag_nether");

    public static final TagKey<Item> LOOT_BAG_END =
            create("loot_bag_end");

    public static final TagKey<Item> LOOT_BAG_EXPLOSIVE =
            create("loot_bag_explosive");

    public static final TagKey<Item> LOOT_BAG_VALUABLE =
            create("loot_bag_valuable");

    private static TagKey<Item> create(String name) {
        return TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(
                        Gamefixes.MOD_ID,
                        name
                )
        );
    }

    public static void initialize() {
        // Registration happens when the class is loaded.
    }
}