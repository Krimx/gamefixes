package com.krimx.gamefixes.advancement;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModCriteria {

    public static final ResearchCriterion RESEARCH = register(
            "research",
            new ResearchCriterion()
    );

    private static <T extends CriterionTrigger<?>> T register(
            String name,
            T criterion
    ) {
        return Registry.register(
                BuiltInRegistries.TRIGGER_TYPES,
                Identifier.fromNamespaceAndPath(
                        "gamefixes",
                        name
                ),
                criterion
        );
    }

    public static void init() {
    }
}