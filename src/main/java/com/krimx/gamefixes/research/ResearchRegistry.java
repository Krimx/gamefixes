package com.krimx.gamefixes.research;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.PackType;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ResearchRegistry
        implements SimpleSynchronousResourceReloadListener {

    public static final ResearchRegistry INSTANCE =
            new ResearchRegistry();

    private static final Identifier RESEARCH_FILE =
            Identifier.fromNamespaceAndPath(
                    Gamefixes.MOD_ID,
                    "research.json"
            );

    private volatile Map<String, ResearchProject> projects =
            Collections.emptyMap();

    private ResearchRegistry() {
    }

    public static void initialize() {
        ResourceManagerHelper
                .get(PackType.SERVER_DATA)
                .registerReloadListener(INSTANCE);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(
                Gamefixes.MOD_ID,
                "research_definitions"
        );
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Optional<Resource> resource =
                manager.getResource(RESEARCH_FILE);

        if (resource.isEmpty()) {
            Gamefixes.LOGGER.warn(
                    "Could not find {}. No research definitions were loaded.",
                    RESEARCH_FILE
            );

            projects = Collections.emptyMap();
            return;
        }

        Map<String, ResearchProject> loaded =
                new LinkedHashMap<>();

        try (
                Reader reader =
                        new InputStreamReader(
                                resource.get().open(),
                                StandardCharsets.UTF_8
                        )
        ) {
            JsonObject root =
                    JsonParser.parseReader(reader)
                            .getAsJsonObject();

            for (
                    Map.Entry<String, JsonElement> professionEntry
                    : root.entrySet()
            ) {
                Identifier profession =
                        Identifier.parse(
                                professionEntry.getKey()
                        );

                if (!professionEntry
                        .getValue()
                        .isJsonObject()) {
                    Gamefixes.LOGGER.warn(
                            "Research profession '{}' is not an object.",
                            professionEntry.getKey()
                    );
                    continue;
                }

                JsonObject researchObject =
                        professionEntry
                                .getValue()
                                .getAsJsonObject();

                for (
                        Map.Entry<String, JsonElement> researchEntry
                        : researchObject.entrySet()
                ) {
                    if (!researchEntry
                            .getValue()
                            .isJsonObject()) {
                        Gamefixes.LOGGER.warn(
                                "Research '{}' for profession '{}' is not an object.",
                                researchEntry.getKey(),
                                profession
                        );
                        continue;
                    }

                    String id =
                            profession
                                    + "/"
                                    + researchEntry.getKey();

                    try {
                        ResearchProject project =
                                parseProject(
                                        id,
                                        profession,
                                        researchEntry
                                                .getValue()
                                                .getAsJsonObject()
                                );

                        if (loaded.put(id, project) != null) {
                            Gamefixes.LOGGER.warn(
                                    "Duplicate research definition '{}'.",
                                    id
                            );
                        }
                    } catch (RuntimeException exception) {
                        Gamefixes.LOGGER.error(
                                "Failed to load research definition '{}'.",
                                id,
                                exception
                        );
                    }
                }
            }
        } catch (Exception exception) {
            Gamefixes.LOGGER.error(
                    "Failed to load research definitions from {}.",
                    RESEARCH_FILE,
                    exception
            );

            projects = Collections.emptyMap();
            return;
        }

        projects =
                Collections.unmodifiableMap(
                        loaded
                );

        Gamefixes.LOGGER.info(
                "Loaded {} research definitions.",
                projects.size()
        );
    }

    private ResearchProject parseProject(
            String id,
            Identifier profession,
            JsonObject json
    ) {
        String displayName =
                json.get("name")
                        .getAsString();

        if (!json.has("inputs")
                || !json.get("inputs").isJsonArray()
                || json.getAsJsonArray("inputs").size() != 2) {
            throw new IllegalArgumentException(
                    "Research must contain exactly two inputs."
            );
        }

        JsonObject inputA =
                json.getAsJsonArray("inputs")
                        .get(0)
                        .getAsJsonObject();

        JsonObject inputB =
                json.getAsJsonArray("inputs")
                        .get(1)
                        .getAsJsonObject();

        String firstInputString =
                inputA.get("item")
                        .getAsString();

        boolean firstInputTag =
                firstInputString.startsWith("#");

        Identifier firstInput =
                Identifier.parse(
                        firstInputTag
                                ? firstInputString.substring(1)
                                : firstInputString
                );

        int firstInputCount =
                inputA.get("count")
                        .getAsInt();

        String secondInputString =
                inputB.get("item")
                        .getAsString();

        boolean secondInputTag =
                secondInputString.startsWith("#");

        Identifier secondInput =
                Identifier.parse(
                        secondInputTag
                                ? secondInputString.substring(1)
                                : secondInputString
                );

        int secondInputCount =
                inputB.get("count")
                        .getAsInt();

        if (firstInputCount <= 0
                || secondInputCount <= 0) {
            throw new IllegalArgumentException(
                    "Research input counts must be greater than zero."
            );
        }

        JsonObject output =
                json.getAsJsonObject("output");

        Identifier outputItem =
                Identifier.parse(
                        output.get("item")
                                .getAsString()
                );

        int outputCount =
                output.has("count")
                        ? output.get("count")
                                .getAsInt()
                        : 1;

        Identifier outputEnchantment = null;
        int outputEnchantmentLevel = 0;

        if (output.has("enchantment")) {
            outputEnchantment =
                    Identifier.parse(
                            output.get("enchantment")
                                    .getAsString()
                    );

            outputEnchantmentLevel =
                    output.has("level")
                            ? output.get("level")
                                    .getAsInt()
                            : 1;
        }

        JsonObject trade =
                json.getAsJsonObject("trade");

        int emeraldCost =
                trade.get("emerald_cost")
                        .getAsInt();

        Identifier itemCost = null;
        int itemCostCount = 0;

        if (trade.has("item_cost")) {
            JsonObject itemCostObject =
                    trade.getAsJsonObject("item_cost");

            itemCost =
                    Identifier.parse(
                            itemCostObject
                                    .get("item")
                                    .getAsString()
                    );

            itemCostCount =
                    itemCostObject
                            .get("count")
                            .getAsInt();

            if (itemCostCount <= 0) {
                throw new IllegalArgumentException(
                        "Research item cost count must be greater than zero."
                );
            }

            if (!BuiltInRegistries.ITEM.containsKey(itemCost)) {
                throw new IllegalArgumentException(
                        "Unknown research item cost: "
                                + itemCost
                );
            }
        }

        int maxUses =
                trade.get("max_uses")
                        .getAsInt();

        int villagerXp =
                trade.get("villager_xp")
                        .getAsInt();

        float priceMultiplier =
                trade.get("price_multiplier")
                        .getAsFloat();

        if (outputCount <= 0
                || emeraldCost <= 0
                || maxUses <= 0
                || villagerXp < 0
                || priceMultiplier < 0.0F) {
            throw new IllegalArgumentException(
                    "Invalid output or trade values."
            );
        }

        return new ResearchProject(
                id,
                displayName,
                profession,

                firstInput,
                firstInputTag,
                firstInputCount,

                secondInput,
                secondInputTag,
                secondInputCount,

                outputItem,
                outputCount,

                outputEnchantment,
                outputEnchantmentLevel,

                emeraldCost,
                itemCost,
                itemCostCount,

                maxUses,
                villagerXp,
                priceMultiplier
        );
    }

    public static ResearchProject get(String id) {
        return INSTANCE.projects.get(id);
    }

    public static Collection<ResearchProject> all() {
        return INSTANCE.projects.values();
    }
}
