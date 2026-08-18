package com.krimx.gamefixes.research;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.enchantment.Enchantment;

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
                        net.minecraft.resources.ResourceKey.create(
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
                        net.minecraft.core.registries.BuiltInRegistries.ITEM
                                .getValue(
                                        project.getOutputItemId()
                                ),
                        project.getOutputCount()
                );

        if (project.getOutputEnchantmentId() != null) {

            Holder<Enchantment> enchantment =
                    villager.registryAccess()
                            .lookupOrThrow(
                                    Registries.ENCHANTMENT
                            )
                            .get(
                                    net.minecraft.resources.ResourceKey.create(
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

        return new MerchantOffer(
                new ItemCost(
                        Items.EMERALD,
                        project.getEmeraldCost()
                ),
                Optional.empty(),
                result,
                project.getMaxUses(),
                project.getVillagerXp(),
                project.getPriceMultiplier()
        );
    }
}