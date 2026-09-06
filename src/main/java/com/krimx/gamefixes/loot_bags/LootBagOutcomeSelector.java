package com.krimx.gamefixes.loot_bags;

import com.krimx.gamefixes.LootBagProfile;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class LootBagOutcomeSelector {

    public static Outcome select(
            LootBagProfile profile,
            RandomSource random
    ) {

        List<Outcome> eligibleOutcomes =
                new ArrayList<>();

        List<Outcome> defaultOutcomes =
                new ArrayList<>();

        for (Outcome outcome :
                LootBagOutcomes.getAll().values()) {

            if (outcome.isDefault()) {
                defaultOutcomes.add(outcome);
                continue;
            }

            if (outcome.isEligible(profile)) {
                eligibleOutcomes.add(outcome);
            }
        }

        if (!eligibleOutcomes.isEmpty()) {

            return eligibleOutcomes.get(
                    random.nextInt(
                            eligibleOutcomes.size()
                    )
            );
        }

        if (!defaultOutcomes.isEmpty()) {

            return defaultOutcomes.get(
                    random.nextInt(
                            defaultOutcomes.size()
                    )
            );
        }

        return null;
    }
}