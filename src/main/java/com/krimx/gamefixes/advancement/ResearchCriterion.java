package com.krimx.gamefixes.advancement;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class ResearchCriterion
        extends SimpleCriterionTrigger<ResearchCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, String researchId) {
        trigger(
                player,
                conditions -> conditions.researchId().equals(researchId)
        );
    }

    public record Conditions(
            Optional<ContextAwarePredicate> player,
            String researchId
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ContextAwarePredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),

                        Codec.STRING
                                .fieldOf("research")
                                .forGetter(Conditions::researchId)
                ).apply(instance, Conditions::new));
    }
}