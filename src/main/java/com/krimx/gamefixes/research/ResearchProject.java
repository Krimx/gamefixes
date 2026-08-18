package com.krimx.gamefixes.research;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class ResearchProject {

    private final String id;
    private final String displayName;

    private final Identifier profession;

    private final Identifier firstInput;
    private final int firstInputCount;
    private final Identifier firstInputEnchantment;

    private final Identifier secondInput;
    private final int secondInputCount;
    private final Identifier secondInputEnchantment;

    private final Identifier outputItem;
    private final int outputCount;

    private final Identifier outputEnchantment;
    private final int outputEnchantmentLevel;

    private final int emeraldCost;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    public ResearchProject(
            String id,
            String displayName,
            Identifier profession,
            Identifier firstInput,
            int firstInputCount,
            Identifier firstInputEnchantment,
            Identifier secondInput,
            int secondInputCount,
            Identifier secondInputEnchantment,
            Identifier outputItem,
            int outputCount,
            Identifier outputEnchantment,
            int outputEnchantmentLevel,
            int emeraldCost,
            int maxUses,
            int villagerXp,
            float priceMultiplier
    ) {
        this.id = id;
        this.displayName = displayName;
        this.profession = profession;
        this.firstInput = firstInput;
        this.firstInputCount = firstInputCount;
        this.firstInputEnchantment = firstInputEnchantment;
        this.secondInput = secondInput;
        this.secondInputCount = secondInputCount;
        this.secondInputEnchantment = secondInputEnchantment;
        this.outputItem = outputItem;
        this.outputCount = outputCount;
        this.outputEnchantment = outputEnchantment;
        this.outputEnchantmentLevel = outputEnchantmentLevel;
        this.emeraldCost = emeraldCost;
        this.maxUses = maxUses;
        this.villagerXp = villagerXp;
        this.priceMultiplier = priceMultiplier;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Identifier getProfession() {
        return profession;
    }

    public Identifier getFirstInputId() {
        return firstInput;
    }

    public int getFirstInputCount() {
        return firstInputCount;
    }

    public Identifier getFirstInputEnchantmentId() {
        return firstInputEnchantment;
    }

    public Identifier getSecondInputId() {
        return secondInput;
    }

    public int getSecondInputCount() {
        return secondInputCount;
    }

    public Identifier getSecondInputEnchantmentId() {
        return secondInputEnchantment;
    }

    public Identifier getOutputItemId() {
        return outputItem;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public Identifier getOutputEnchantmentId() {
        return outputEnchantment;
    }

    public int getOutputEnchantmentLevel() {
        return outputEnchantmentLevel;
    }

    public int getEmeraldCost() {
        return emeraldCost;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getVillagerXp() {
        return villagerXp;
    }

    public float getPriceMultiplier() {
        return priceMultiplier;
    }

    public boolean matchesInputs(
            ItemStack firstStack,
            ItemStack secondStack
    ) {
        boolean normalOrder =
                matchesInput(
                        firstStack,
                        firstInput,
                        firstInputCount,
                        firstInputEnchantment
                )
                        && matchesInput(
                        secondStack,
                        secondInput,
                        secondInputCount,
                        secondInputEnchantment
                );

        boolean reversedOrder =
                matchesInput(
                        firstStack,
                        secondInput,
                        secondInputCount,
                        secondInputEnchantment
                )
                        && matchesInput(
                        secondStack,
                        firstInput,
                        firstInputCount,
                        firstInputEnchantment
                );

        return normalOrder || reversedOrder;
    }

    private boolean matchesInput(
            ItemStack stack,
            Identifier requiredItem,
            int requiredCount,
            Identifier requiredEnchantment
    ) {
        if (stack.getCount() < requiredCount) {
            return false;
        }

        if (requiredItem != null) {
            Identifier actualItem =
                    BuiltInRegistries.ITEM.getKey(stack.getItem());

            if (!requiredItem.equals(actualItem)) {
                return false;
            }
        }

        return matchesEnchantment(
                stack,
                requiredEnchantment
        );
    }

    private boolean matchesEnchantment(
            ItemStack stack,
            Identifier requiredEnchantment
    ) {
        if (requiredEnchantment == null) {
            return true;
        }

        return EnchantmentHelper
                .getEnchantmentsForCrafting(stack)
                .entrySet()
                .stream()
                .anyMatch(entry ->
                        entry.getKey()
                                .getRegisteredName()
                                .equals(requiredEnchantment.toString())
                );
    }
}