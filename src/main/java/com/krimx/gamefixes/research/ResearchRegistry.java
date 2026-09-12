package com.krimx.gamefixes.research;

import com.google.gson.JsonArray;
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
import java.util.List;
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

        InputDefinition firstInputDefinition =
                parseInput(inputA);

        InputDefinition secondInputDefinition =
                parseInput(inputB);

        Identifier firstInput =
                firstInputDefinition.id();

        boolean firstInputTag =
                firstInputDefinition.tag();

        boolean firstInputEnchantment =
                firstInputDefinition.enchantment();

        int firstInputCount =
                firstInputDefinition.count();

        Identifier secondInput =
                secondInputDefinition.id();

        boolean secondInputTag =
                secondInputDefinition.tag();

        boolean secondInputEnchantment =
                secondInputDefinition.enchantment();

        int secondInputCount =
                secondInputDefinition.count();

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

        List<ResearchProject.PotionEffectDefinition>
                outputPotionEffects =
                parsePotionEffects(output);

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
                firstInputEnchantment,
                firstInputCount,

                secondInput,
                secondInputTag,
                secondInputEnchantment,
                secondInputCount,

                outputItem,
                outputCount,

                outputEnchantment,
                outputEnchantmentLevel,

                outputPotionEffects,

                emeraldCost,
                itemCost,
                itemCostCount,

                maxUses,
                villagerXp,
                priceMultiplier
        );
    }

    private List<ResearchProject.PotionEffectDefinition>
    parsePotionEffects(JsonObject output) {

        if (!output.has("potion")) {
            return List.of();
        }

        JsonObject potion =
                output.getAsJsonObject("potion");

        if (!potion.has("effects")
                || !potion.get("effects").isJsonArray()) {
            throw new IllegalArgumentException(
                    "Potion output must contain an 'effects' array."
            );
        }

        JsonArray effects =
                potion.getAsJsonArray("effects");

        if (effects.isEmpty()) {
            throw new IllegalArgumentException(
                    "Potion output must contain at least one effect."
            );
        }

        List<ResearchProject.PotionEffectDefinition>
                definitions =
                new java.util.ArrayList<>();

        for (JsonElement effectElement : effects) {
            if (!effectElement.isJsonObject()) {
                throw new IllegalArgumentException(
                        "Potion effect definition must be an object."
                );
            }

            JsonObject effectObject =
                    effectElement.getAsJsonObject();

            if (!effectObject.has("effect")) {
                throw new IllegalArgumentException(
                        "Potion effect definition must contain an 'effect'."
                );
            }

            Identifier effect =
                    Identifier.parse(
                            effectObject
                                    .get("effect")
                                    .getAsString()
                    );

            int duration =
                    effectObject.has("duration")
                            ? effectObject
                            .get("duration")
                            .getAsInt()
                            : 0;

            int amplifier =
                    effectObject.has("amplifier")
                            ? effectObject
                            .get("amplifier")
                            .getAsInt()
                            : 0;

            if (!BuiltInRegistries.MOB_EFFECT.containsKey(effect)) {
                throw new IllegalArgumentException(
                        "Unknown potion effect: "
                                + effect
                );
            }

            if (duration <= 0) {
                throw new IllegalArgumentException(
                        "Potion effect duration must be greater than zero."
                );
            }

            if (amplifier < 0) {
                throw new IllegalArgumentException(
                        "Potion effect amplifier cannot be negative."
                );
            }

            definitions.add(
                    new ResearchProject.PotionEffectDefinition(
                            effect,
                            duration,
                            amplifier
                    )
            );
        }

        return List.copyOf(definitions);
    }

    private InputDefinition parseInput(JsonObject input) {
        boolean enchantment =
                input.has("enchantment");

        if (!input.has("item") && !enchantment) {
            throw new IllegalArgumentException(
                    "Research input must contain either 'item' or 'enchantment'."
            );
        }

        if (input.has("item") && enchantment) {
            throw new IllegalArgumentException(
                    "Research input cannot contain both 'item' and 'enchantment'."
            );
        }

        String inputString =
                input.get(
                        enchantment
                                ? "enchantment"
                                : "item"
                ).getAsString();

        boolean tag =
                !enchantment
                        && inputString.startsWith("#");

        Identifier inputId =
                Identifier.parse(
                        tag
                                ? inputString.substring(1)
                                : inputString
                );

        int count =
                input.has("count")
                        ? input.get("count").getAsInt()
                        : 1;

        if (count <= 0) {
            throw new IllegalArgumentException(
                    "Research input counts must be greater than zero."
            );
        }

        if (!enchantment
                && !tag
                && !BuiltInRegistries.ITEM.containsKey(inputId)) {
            throw new IllegalArgumentException(
                    "Unknown research item: "
                            + inputId
            );
        }

        return new InputDefinition(
                inputId,
                tag,
                enchantment,
                count
        );
    }

    private record InputDefinition(
            Identifier id,
            boolean tag,
            boolean enchantment,
            int count
    ) {
    }

    public static ResearchProject get(String id) {
        return INSTANCE.projects.get(id);
    }

    public static Collection<ResearchProject> all() {
        return INSTANCE.projects.values();
    }
}