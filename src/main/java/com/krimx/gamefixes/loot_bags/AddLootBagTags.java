package com.krimx.gamefixes.loot_bags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddLootBagTags {

    private static final Map<Item, Set<Identifier>> ITEM_TAGS =
            new HashMap<>();

    private static final String FILE_PATH =
            "data/gamefixes/tags/lootbags.json";

    public static void initialize() {

        ITEM_TAGS.clear();

        InputStream stream =
                AddLootBagTags.class
                        .getClassLoader()
                        .getResourceAsStream(FILE_PATH);

        if (stream == null) {
            throw new IllegalStateException(
                    "Could not find " + FILE_PATH
            );
        }

        try (
                InputStreamReader reader =
                        new InputStreamReader(
                                stream,
                                StandardCharsets.UTF_8
                        )
        ) {

            JsonObject json =
                    JsonParser
                            .parseReader(reader)
                            .getAsJsonObject();

            for (
                    Map.Entry<String, JsonElement> entry :
                    json.entrySet()
            ) {

                String categoryName =
                        entry.getKey();

                Identifier category =
                        Identifier.fromNamespaceAndPath(
                                Gamefixes.MOD_ID,
                                categoryName
                        );

                JsonArray items =
                        entry.getValue()
                                .getAsJsonArray();

                for (JsonElement element : items) {

                    Identifier itemId =
                            Identifier.parse(
                                    element.getAsString()
                            );

                    Item item =
                            BuiltInRegistries.ITEM
                                    .getOptional(itemId)
                                    .orElse(null);

                    if (item == null) {
                        Gamefixes.LOGGER.warn(
                                "Loot Bag category '{}' references unknown item '{}'",
                                categoryName,
                                itemId
                        );

                        continue;
                    }

                    ITEM_TAGS
                            .computeIfAbsent(
                                    item,
                                    ignored -> new HashSet<>()
                            )
                            .add(category);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to load " + FILE_PATH,
                    e
            );
        }

        Gamefixes.LOGGER.info(
                "Loaded {} Loot Bag item categories for {} items.",
                getCategoryCount(),
                ITEM_TAGS.size()
        );
    }

    public static Set<Identifier> getTags(Item item) {

        return ITEM_TAGS.getOrDefault(
                item,
                Set.of()
        );
    }

    private static int getCategoryCount() {

        Set<Identifier> categories =
                new HashSet<>();

        for (Set<Identifier> tags : ITEM_TAGS.values()) {
            categories.addAll(tags);
        }

        return categories.size();
    }
}