package com.krimx.gamefixes.research;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VillagerResearch {

    private VillagerResearch() {
    }

    public static int getResearchSlotCount(
            Villager villager
    ) {
        return Math.max(
                0,
                villager.getVillagerData().level() - 1
        );
    }

    public static VillagerResearchData getData(
            Villager villager
    ) {
        int slotCount =
                getResearchSlotCount(villager);

        VillagerResearchData data =
                villager.getAttachedOrSet(
                        ResearchAttachments.VILLAGER_RESEARCH,
                        VillagerResearchData.empty(slotCount)
                );

        if (data.slotCount() != slotCount) {
            data =
                    data.withSlotCount(slotCount);

            villager.setAttached(
                    ResearchAttachments.VILLAGER_RESEARCH,
                    data
            );
        }

        return data;
    }

    public static boolean isResearchSlotAvailable(
            Villager villager,
            int slot
    ) {
        VillagerResearchData data =
                getData(villager);

        return slot >= 0
                && slot < data.slotCount()
                && data.isEmpty(slot);
    }

    public static void setResearch(
            Villager villager,
            int slot,
            ResearchProject project
    ) {
        VillagerResearchData data =
                getData(villager);

        if (slot < 0
                || slot >= data.slotCount()) {
            return;
        }

        villager.setAttached(
                ResearchAttachments.VILLAGER_RESEARCH,
                data.withResearch(
                        slot,
                        project
                )
        );
    }

    public static void clearResearch(
            Villager villager,
            int slot
    ) {
        VillagerResearchData data =
                getData(villager);

        if (slot < 0
                || slot >= data.slotCount()) {
            return;
        }

        villager.setAttached(
                ResearchAttachments.VILLAGER_RESEARCH,
                data.clearResearch(slot)
        );
    }

    public static ResearchProject findResearch(
            ItemStack firstInput,
            ItemStack secondInput
    ) {
        if (firstInput.isEmpty()
                || secondInput.isEmpty()) {
            return null;
        }

        for (ResearchProject project :
                ResearchRegistry.all()) {

            if (project.matchesInputs(
                    firstInput,
                    secondInput
            )) {
                return project;
            }
        }

        return null;
    }

    public static boolean canResearch(
            Villager villager,
            ResearchProject project
    ) {
        return villager.getVillagerData()
                .profession()
                .is(
                        ResourceKey.create(
                                Registries.VILLAGER_PROFESSION,
                                project.getProfession()
                        )
                );
    }

    public static MerchantOffer createTrade(
            Villager villager,
            ResearchProject project
    ) {
        ItemStack result =
                new ItemStack(
                        BuiltInRegistries.ITEM
                                .getValue(
                                        project.getOutputItemId()
                                ),
                        project.getOutputCount()
                );

        if (project.hasPotionEffects()) {
            List<MobEffectInstance> effects =
                    new ArrayList<>();

            for (
                    ResearchProject.PotionEffectDefinition
                            effectDefinition
                    : project.getOutputPotionEffects()
            ) {
                Holder<MobEffect> effect =
                        villager.registryAccess()
                                .lookupOrThrow(
                                        Registries.MOB_EFFECT
                                )
                                .get(
                                        ResourceKey.create(
                                                Registries.MOB_EFFECT,
                                                effectDefinition.effect()
                                        )
                                )
                                .orElseThrow(
                                        () -> new IllegalArgumentException(
                                                "Unknown potion effect: "
                                                        + effectDefinition.effect()
                                        )
                                );

                effects.add(
                        new MobEffectInstance(
                                effect,
                                effectDefinition.duration(),
                                effectDefinition.amplifier()
                        )
                );
            }

            result.set(
                    DataComponents.POTION_CONTENTS,
                    new PotionContents(
                            Optional.empty(),
                            Optional.empty(),
                            List.copyOf(effects),
                            Optional.empty()
                    )
            );
        }

        if (project.getOutputEnchantmentId() != null) {

            var enchantment =
                    villager.registryAccess()
                            .lookupOrThrow(
                                    Registries.ENCHANTMENT
                            )
                            .get(
                                    ResourceKey.create(
                                            Registries.ENCHANTMENT,
                                            project.getOutputEnchantmentId()
                                    )
                            )
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "Unknown enchantment: "
                                                    + project.getOutputEnchantmentId()
                                    )
                            );

            result.enchant(
                    enchantment,
                    project.getOutputEnchantmentLevel()
            );
        }

        Optional<ItemCost> itemCost =
                project.hasItemCost()
                        ? Optional.of(
                        new ItemCost(
                                BuiltInRegistries.ITEM.getValue(
                                        project.getItemCostId()
                                ),
                                project.getItemCostCount()
                        )
                )
                        : Optional.empty();

        return new MerchantOffer(
                new ItemCost(
                        Items.EMERALD,
                        project.getEmeraldCost()
                ),
                itemCost,
                result,
                project.getMaxUses(),
                project.getVillagerXp(),
                project.getPriceMultiplier()
        );
    }
}