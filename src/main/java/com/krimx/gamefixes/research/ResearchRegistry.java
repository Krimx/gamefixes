package com.krimx.gamefixes.research;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ResearchRegistry
        implements ResourceManagerReloadListener {

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

    /*
     * ============================================================
     * REGISTRATION
     * ============================================================
     */

    public static void initialize() {

        ResourceLoader
                .get(PackType.SERVER_DATA)
                .registerReloadListener(
                        Identifier.fromNamespaceAndPath(
                                Gamefixes.MOD_ID,
                                "research_definitions"
                        ),
                        INSTANCE
                );
    }

    /*
     * ============================================================
     * RESOURCE RELOAD
     * ============================================================
     */

    @Override
    public void onResourceManagerReload(
            ResourceManager resourceManager
    ) {
        load(resourceManager);
    }

    /*
     * ============================================================
     * LOAD RESEARCH JSON
     * ============================================================
     */

    private void load(
            ResourceManager manager
    ) {
        Optional<Resource> resource =
                manager.getResource(
                        RESEARCH_FILE
                );

        if (resource.isEmpty()) {

            Gamefixes.LOGGER.warn(
                    "Could not find {}. No research definitions were loaded.",
                    RESEARCH_FILE
            );

            projects =
                    Collections.emptyMap();

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

            /*
             * ====================================================
             * PROFESSIONS
             * ====================================================
             *
             * Example:
             *
             * "minecraft:librarian": {
             *     "wind_burst": { ... }
             * }
             */

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

                /*
                 * =================================================
                 * RESEARCH DEFINITIONS
                 * =================================================
                 */

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

                        if (loaded.put(
                                id,
                                project
                        ) != null) {

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

            projects =
                    Collections.emptyMap();

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

    /*
     * ============================================================
     * PARSE ONE RESEARCH PROJECT
     * ============================================================
     */

    private ResearchProject parseProject(
            String id,
            Identifier profession,
            JsonObject json
    ) {

        /*
         * --------------------------------------------------------
         * Name
         * --------------------------------------------------------
         */

        if (!json.has("name")) {

            throw new IllegalArgumentException(
                    "Research is missing 'name'."
            );
        }

        String displayName =
                json.get("name")
                        .getAsString();

        /*
         * --------------------------------------------------------
         * Inputs
         * --------------------------------------------------------
         */

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

        Identifier firstInput =
                inputA.has("item")
                        ? Identifier.parse(
                                inputA.get("item")
                                        .getAsString()
                        )
                        : null;

        int firstInputCount =
                inputA.get("count")
                        .getAsInt();

        Identifier firstInputEnchantment =
                inputA.has("enchantment")
                        ? Identifier.parse(
                                inputA.get("enchantment")
                                        .getAsString()
                        )
                        : null;

        Identifier secondInput =
                inputB.has("item")
                        ? Identifier.parse(
                                inputB.get("item")
                                        .getAsString()
                        )
                        : null;

        int secondInputCount =
                inputB.get("count")
                        .getAsInt();

        Identifier secondInputEnchantment =
                inputB.has("enchantment")
                        ? Identifier.parse(
                                inputB.get("enchantment")
                                        .getAsString()
                        )
                        : null;

        if (firstInputCount <= 0
                || secondInputCount <= 0) {

            throw new IllegalArgumentException(
                    "Research input counts must be greater than zero."
            );
        }

        /*
         * --------------------------------------------------------
         * Output
         * --------------------------------------------------------
         */

        if (!json.has("output")
                || !json.get("output").isJsonObject()) {

            throw new IllegalArgumentException(
                    "Research is missing an output object."
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

        /*
         * --------------------------------------------------------
         * Optional enchantment
         * --------------------------------------------------------
         */

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

            if (outputEnchantmentLevel <= 0) {

                throw new IllegalArgumentException(
                        "Enchantment level must be greater than zero."
                );
            }
        }

        /*
         * --------------------------------------------------------
         * Trade
         * --------------------------------------------------------
         */

        if (!json.has("trade")
                || !json.get("trade").isJsonObject()) {

            throw new IllegalArgumentException(
                    "Research is missing a trade object."
            );
        }

        JsonObject trade =
                json.getAsJsonObject("trade");

        int emeraldCost =
                trade.get("emerald_cost")
                        .getAsInt();

        int maxUses =
                trade.get("max_uses")
                        .getAsInt();

        int villagerXp =
                trade.get("villager_xp")
                        .getAsInt();

        float priceMultiplier =
                trade.get("price_multiplier")
                        .getAsFloat();

        if (outputCount <= 0) {

            throw new IllegalArgumentException(
                    "Output count must be greater than zero."
            );
        }

        if (emeraldCost <= 0) {

            throw new IllegalArgumentException(
                    "Emerald cost must be greater than zero."
            );
        }

        if (maxUses <= 0) {

            throw new IllegalArgumentException(
                    "Max uses must be greater than zero."
            );
        }

        if (villagerXp < 0) {

            throw new IllegalArgumentException(
                    "Villager XP cannot be negative."
            );
        }

        if (priceMultiplier < 0.0F) {

            throw new IllegalArgumentException(
                    "Price multiplier cannot be negative."
            );
        }

        /*
         * --------------------------------------------------------
         * Create definition
         * --------------------------------------------------------
         */

        return new ResearchProject(
                id,
                displayName,
                profession,

                firstInput,
                firstInputCount,
                firstInputEnchantment,

                secondInput,
                secondInputCount,
                secondInputEnchantment,

                outputItem,
                outputCount,

                outputEnchantment,
                outputEnchantmentLevel,

                emeraldCost,
                maxUses,
                villagerXp,
                priceMultiplier
        );
    }

    /*
     * ============================================================
     * LOOKUP
     * ============================================================
     */

    public static ResearchProject get(
            String id
    ) {
        return INSTANCE.projects.get(id);
    }

    public static Collection<ResearchProject> all() {
        return INSTANCE.projects.values();
    }
}