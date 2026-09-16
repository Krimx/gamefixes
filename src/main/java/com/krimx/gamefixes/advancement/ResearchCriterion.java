package com.krimx.gamefixes.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

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
            Optional<Holder<LootItemCondition>> player,
            String researchId
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        LootItemCondition.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),
                        Codec.STRING
                                .fieldOf("research")
                                .forGetter(Conditions::researchId)
                ).apply(instance, Conditions::new));

        public Conditions(String researchId) {
            this(Optional.empty(), researchId);
        }
    }
}