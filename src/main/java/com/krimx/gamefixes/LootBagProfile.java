package com.krimx.gamefixes;

import com.krimx.gamefixes.loot_bags.AddLootBagTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBagProfile {

    private final Map<Identifier, Integer> scores;

    public LootBagProfile(ItemStack bag) {

        this.scores = new HashMap<>();

        List<Identifier> contents =
                bag.getOrDefault(
                        LootBagComponents.LOOT_BAG_CONTENTS,
                        List.of()
                );

        for (Identifier identifier : contents) {

            Item item =
                    BuiltInRegistries.ITEM
                            .getOptional(identifier)
                            .orElse(null);

            if (item == null) {
                continue;
            }

            for (
                    Identifier category :
                    AddLootBagTags.getTags(item)
            ) {

                addScore(
                        category,
                        1
                );
            }
        }
    }

    public int getScore(Identifier category) {

        return scores.getOrDefault(
                category,
                0
        );
    }

    public void addScore(
            Identifier category,
            int amount
    ) {

        scores.merge(
                category,
                amount,
                Integer::sum
        );
    }

    public Map<Identifier, Integer> getScores() {

        return Map.copyOf(scores);
    }
}