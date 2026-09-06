package com.krimx.gamefixes.loot_bags;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.LootBagProfile;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashMap;
import java.util.Map;

public class LootBagOutcomeExecutors {

    private static final Map<Identifier, LootBagOutcomeExecutor> EXECUTORS =
            new LinkedHashMap<>();

    public static void initialize() {
        EXECUTORS.clear();

        register(
                Identifier.fromNamespaceAndPath(
                        Gamefixes.MOD_ID,
                        "ore_vomit"
                ),
                LootBagOutcomeExecutors::oreVomit
        );
    }

    public static void register(
            Identifier outcomeId,
            LootBagOutcomeExecutor executor
    ) {

        if (EXECUTORS.containsKey(outcomeId)) {
            throw new IllegalArgumentException(
                    "An executor for Loot Bag outcome '"
                            + outcomeId
                            + "' is already registered."
            );
        }

        EXECUTORS.put(
                outcomeId,
                executor
        );

        Gamefixes.LOGGER.debug(
                "Registered Loot Bag outcome executor '{}'.",
                outcomeId
        );
    }

    public static LootBagOutcomeExecutor get(
            Identifier outcomeId
    ) {
        return EXECUTORS.get(outcomeId);
    }

    public static boolean hasExecutor(
            Identifier outcomeId
    ) {
        return EXECUTORS.containsKey(outcomeId);
    }

    public static Map<Identifier, LootBagOutcomeExecutor> getAll() {
        return Map.copyOf(EXECUTORS);
    }

    public static void oreVomit(
            ServerLevel level,
            ServerPlayer player,
            LootBagProfile profile
    ) {

        int amountToShootFactor = 7;
        int amountToShoot = amountToShootFactor * profile.getScore(
                Identifier.fromNamespaceAndPath(
                Gamefixes.MOD_ID,
                "valuable"
        )) + level.getRandom().nextIntBetweenInclusive(-2, 2);
        for (int i = 0; i < amountToShoot; i++) {

            int delay = i * 2;

            OutcomeFunctions.scheduleAfterTicks(
                    level,
                    delay,
                    () -> {

                        Item item =
                                OutcomeFunctions.getRandomItem(
                                        level,
                                        Items.DIAMOND,
                                        Items.IRON_INGOT,
                                        Items.GOLD_INGOT,
                                        Items.LAPIS_LAZULI,
                                        Items.EMERALD,
                                        Items.COPPER_INGOT
                                );

                        double speed =
                                OutcomeFunctions.getRandomDouble(
                                        level,
                                        0.35,
                                        0.50
                                );

                        OutcomeFunctions.shootItemFromPlayerFace(
                                level,
                                player,
                                item,
                                speed,
                                0.25,
                                0.20,
                                0.25,
                                0.15,
                                20
                        );
                    }
            );
        }
    }
}