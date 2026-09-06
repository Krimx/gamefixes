package com.krimx.gamefixes.loot_bags;

import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootBagOutcomes {

    private static final Map<Identifier, Outcome> OUTCOMES =
            new LinkedHashMap<>();

    private static final List<String> VALIDATION_ERRORS =
            new ArrayList<>();

    public static void initialize() {
        OUTCOMES.clear();
        VALIDATION_ERRORS.clear();

        registerOutcome(
                "ore_vomit",
                likes("valuable"),
                dislikes()
        );

        registerValidationMessages();
    }

    private static void registerValidationMessages() {

        ServerLevelEvents.LOAD.register(
                (MinecraftServer server, net.minecraft.server.level.ServerLevel level) -> {

                    if (VALIDATION_ERRORS.isEmpty()) {
                        return;
                    }

                    for (String error : VALIDATION_ERRORS) {

                        for (var player :
                                server.getPlayerList().getPlayers()) {

                            player.sendSystemMessage(
                                    Component.literal(
                                            "[GameFixes] Loot Bag outcome error: "
                                                    + error
                                    )
                            );
                        }
                    }
                }
        );
    }

    public static void registerOutcome(
            String id,
            String[] likes,
            String[] dislikes
    ) {

        String[] safeLikes =
                likes == null
                        ? new String[0]
                        : likes;

        String[] safeDislikes =
                dislikes == null
                        ? new String[0]
                        : dislikes;

        Set<String> likeCategories =
                new LinkedHashSet<>();

        Set<String> dislikeCategories =
                new LinkedHashSet<>();

        for (String category : safeLikes) {

            if (!likeCategories.add(category)) {

                VALIDATION_ERRORS.add(
                        "Outcome '"
                                + id
                                + "' lists category '"
                                + category
                                + "' more than once in its likes."
                );
            }
        }

        for (String category : safeDislikes) {

            if (!dislikeCategories.add(category)) {

                VALIDATION_ERRORS.add(
                        "Outcome '"
                                + id
                                + "' lists category '"
                                + category
                                + "' more than once in its dislikes."
                );
            }
        }

        for (String category : likeCategories) {

            if (dislikeCategories.contains(category)) {

                VALIDATION_ERRORS.add(
                        "Outcome '"
                                + id
                                + "' lists category '"
                                + category
                                + "' as both a like and a dislike."
                );
            }
        }

        if (
                likeCategories.stream()
                        .anyMatch(dislikeCategories::contains)
        ) {
            return;
        }

        Map<Identifier, Integer> affinities =
                new LinkedHashMap<>();

        for (String category : likeCategories) {

            Identifier categoryId =
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            category
                    );

            affinities.put(
                    categoryId,
                    1
            );
        }

        for (String category : dislikeCategories) {

            Identifier categoryId =
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            category
                    );

            affinities.put(
                    categoryId,
                    -1
            );
        }

        register(
                new Outcome(
                        Identifier.fromNamespaceAndPath(
                                Gamefixes.MOD_ID,
                                id
                        ),
                        affinities
                )
        );
    }

    public static String[] likes(String... categories) {
        return categories;
    }

    public static String[] dislikes(String... categories) {
        return categories;
    }

    public static void register(Outcome outcome) {

        if (OUTCOMES.containsKey(outcome.getId())) {
            throw new IllegalArgumentException(
                    "Loot Bag outcome '"
                            + outcome.getId()
                            + "' is already registered."
            );
        }

        OUTCOMES.put(
                outcome.getId(),
                outcome
        );

        Gamefixes.LOGGER.debug(
                "Registered Loot Bag outcome '{}'.",
                outcome.getId()
        );
    }

    public static Outcome get(Identifier id) {
        return OUTCOMES.get(id);
    }

    public static Map<Identifier, Outcome> getAll() {
        return Map.copyOf(OUTCOMES);
    }
}