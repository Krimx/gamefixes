package com.krimx.gamefixes.research;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public final class ResearchProject {

    private final String id;
    private final String displayName;
    private final Identifier profession;

    private final Identifier firstInput;
    private final boolean firstInputTag;
    private final boolean firstInputEnchantment;
    private final int firstInputCount;

    private final Identifier secondInput;
    private final boolean secondInputTag;
    private final boolean secondInputEnchantment;
    private final int secondInputCount;

    private final Identifier outputItem;
    private final int outputCount;
    private final Identifier outputEnchantment;
    private final int outputEnchantmentLevel;
    private final List<PotionEffectDefinition> outputPotionEffects;

    private final int emeraldCost;
    private final Identifier itemCost;
    private final int itemCostCount;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    public ResearchProject(
            String id,
            String displayName,
            Identifier profession,
            Identifier firstInput,
            boolean firstInputTag,
            boolean firstInputEnchantment,
            int firstInputCount,
            Identifier secondInput,
            boolean secondInputTag,
            boolean secondInputEnchantment,
            int secondInputCount,
            Identifier outputItem,
            int outputCount,
            Identifier outputEnchantment,
            int outputEnchantmentLevel,
            List<PotionEffectDefinition> outputPotionEffects,
            int emeraldCost,
            Identifier itemCost,
            int itemCostCount,
            int maxUses,
            int villagerXp,
            float priceMultiplier
    ) {
        this.id = id;
        this.displayName = displayName;
        this.profession = profession;
        this.firstInput = firstInput;
        this.firstInputTag = firstInputTag;
        this.firstInputEnchantment = firstInputEnchantment;
        this.firstInputCount = firstInputCount;
        this.secondInput = secondInput;
        this.secondInputTag = secondInputTag;
        this.secondInputEnchantment = secondInputEnchantment;
        this.secondInputCount = secondInputCount;
        this.outputItem = outputItem;
        this.outputCount = outputCount;
        this.outputEnchantment = outputEnchantment;
        this.outputEnchantmentLevel = outputEnchantmentLevel;
        this.outputPotionEffects =
                List.copyOf(outputPotionEffects);
        this.emeraldCost = emeraldCost;
        this.itemCost = itemCost;
        this.itemCostCount = itemCostCount;
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

    public boolean isFirstInputTag() {
        return firstInputTag;
    }

    public boolean isFirstInputEnchantment() {
        return firstInputEnchantment;
    }

    public int getFirstInputCount() {
        return firstInputCount;
    }

    public Identifier getSecondInputId() {
        return secondInput;
    }

    public boolean isSecondInputTag() {
        return secondInputTag;
    }

    public boolean isSecondInputEnchantment() {
        return secondInputEnchantment;
    }

    public int getSecondInputCount() {
        return secondInputCount;
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

    public List<PotionEffectDefinition>
    getOutputPotionEffects() {
        return outputPotionEffects;
    }

    public boolean hasPotionEffects() {
        return !outputPotionEffects.isEmpty();
    }

    public int getEmeraldCost() {
        return emeraldCost;
    }

    public Identifier getItemCostId() {
        return itemCost;
    }

    public int getItemCostCount() {
        return itemCostCount;
    }

    public boolean hasItemCost() {
        return itemCost != null;
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
                        firstInputTag,
                        firstInputEnchantment,
                        firstInputCount
                )
                        && matchesInput(
                        secondStack,
                        secondInput,
                        secondInputTag,
                        secondInputEnchantment,
                        secondInputCount
                );

        boolean reversedOrder =
                matchesInput(
                        firstStack,
                        secondInput,
                        secondInputTag,
                        secondInputEnchantment,
                        secondInputCount
                )
                        && matchesInput(
                        secondStack,
                        firstInput,
                        firstInputTag,
                        firstInputEnchantment,
                        firstInputCount
                );

        return normalOrder || reversedOrder;
    }

    private boolean matchesInput(
            ItemStack stack,
            Identifier requiredInput,
            boolean requiredInputTag,
            boolean requiredInputEnchantment,
            int requiredCount
    ) {
        if (stack.isEmpty()
                || stack.getCount() < requiredCount) {
            return false;
        }

        if (requiredInputEnchantment) {
            return hasEnchantment(
                    stack,
                    requiredInput
            );
        }

        if (requiredInputTag) {
            TagKey<Item> tag =
                    TagKey.create(
                            Registries.ITEM,
                            requiredInput
                    );

            return stack.is(tag);
        }

        return BuiltInRegistries.ITEM
                .getKey(stack.getItem())
                .equals(requiredInput);
    }

    private boolean hasEnchantment(
            ItemStack stack,
            Identifier requiredEnchantment
    ) {
        ItemEnchantments enchantments =
                stack.getOrDefault(
                        DataComponents.ENCHANTMENTS,
                        ItemEnchantments.EMPTY
                );

        for (Holder<Enchantment> enchantment
                : enchantments.keySet()) {

            if (enchantment.unwrapKey()
                    .map(key ->
                            key.identifier()
                                    .equals(requiredEnchantment)
                    )
                    .orElse(false)) {

                return enchantments.getLevel(enchantment) > 0;
            }
        }

        ItemEnchantments storedEnchantments =
                stack.getOrDefault(
                        DataComponents.STORED_ENCHANTMENTS,
                        ItemEnchantments.EMPTY
                );

        for (Holder<Enchantment> enchantment
                : storedEnchantments.keySet()) {

            if (enchantment.unwrapKey()
                    .map(key ->
                            key.identifier()
                                    .equals(requiredEnchantment)
                    )
                    .orElse(false)) {

                return storedEnchantments.getLevel(enchantment) > 0;
            }
        }

        return false;
    }

    public record PotionEffectDefinition(
            Identifier effect,
            int duration,
            int amplifier
    ) {
    }
}