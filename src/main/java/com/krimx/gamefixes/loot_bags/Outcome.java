package com.krimx.gamefixes.loot_bags;

import com.krimx.gamefixes.LootBagProfile;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class Outcome {

    private final Identifier id;
    private final Map<Identifier, Integer> affinities;

    public Outcome(
            Identifier id,
            Map<Identifier, Integer> affinities
    ) {
        this.id = id;
        this.affinities = Map.copyOf(affinities);
    }

    public Identifier getId() {
        return id;
    }

    public Map<Identifier, Integer> getAffinities() {
        return affinities;
    }

    public boolean isDefault() {
        return affinities.isEmpty();
    }

    public boolean isEligible(
            LootBagProfile profile
    ) {

        for (
                Map.Entry<Identifier, Integer> entry :
                affinities.entrySet()
        ) {

            int score =
                    profile.getScore(
                            entry.getKey()
                    );

            int affinity =
                    entry.getValue();

            if (affinity > 0 && score <= 0) {
                return false;
            }

            if (affinity < 0 && score > 0) {
                return false;
            }
        }

        return true;
    }
}