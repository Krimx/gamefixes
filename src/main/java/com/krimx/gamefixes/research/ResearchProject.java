package com.krimx.gamefixes.research;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ResearchProject {

    private final String id;
    private final String displayName;
    private final Identifier profession;

    private final Identifier firstInput;
    private final boolean firstInputTag;
    private final int firstInputCount;

    private final Identifier secondInput;
    private final boolean secondInputTag;
    private final int secondInputCount;

    private final Identifier outputItem;
    private final int outputCount;
    private final Identifier outputEnchantment;
    private final int outputEnchantmentLevel;

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
            int firstInputCount,
            Identifier secondInput,
            boolean secondInputTag,
            int secondInputCount,
            Identifier outputItem,
            int outputCount,
            Identifier outputEnchantment,
            int outputEnchantmentLevel,
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
        this.firstInputCount = firstInputCount;
        this.secondInput = secondInput;
        this.secondInputTag = secondInputTag;
        this.secondInputCount = secondInputCount;
        this.outputItem = outputItem;
        this.outputCount = outputCount;
        this.outputEnchantment = outputEnchantment;
        this.outputEnchantmentLevel = outputEnchantmentLevel;
        this.emeraldCost = emeraldCost;
        this.itemCost = itemCost;
        this.itemCostCount = itemCostCount;
        this.maxUses = maxUses;
        this.villagerXp = villagerXp;
        this.priceMultiplier = priceMultiplier;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Identifier getProfession() { return profession; }

    public Identifier getFirstInputId() { return firstInput; }
    public boolean isFirstInputTag() { return firstInputTag; }
    public int getFirstInputCount() { return firstInputCount; }

    public Identifier getSecondInputId() { return secondInput; }
    public boolean isSecondInputTag() { return secondInputTag; }
    public int getSecondInputCount() { return secondInputCount; }

    public Identifier getOutputItemId() { return outputItem; }
    public int getOutputCount() { return outputCount; }

    public Identifier getOutputEnchantmentId() { return outputEnchantment; }
    public int getOutputEnchantmentLevel() { return outputEnchantmentLevel; }

    public int getEmeraldCost() { return emeraldCost; }

    public Identifier getItemCostId() { return itemCost; }
    public int getItemCostCount() { return itemCostCount; }
    public boolean hasItemCost() { return itemCost != null; }

    public int getMaxUses() { return maxUses; }
    public int getVillagerXp() { return villagerXp; }
    public float getPriceMultiplier() { return priceMultiplier; }

    public boolean matchesInputs(
            ItemStack firstStack,
            ItemStack secondStack
    ) {
        boolean normalOrder =
                matchesInput(
                        firstStack,
                        firstInput,
                        firstInputTag,
                        firstInputCount
                )
                        && matchesInput(
                        secondStack,
                        secondInput,
                        secondInputTag,
                        secondInputCount
                );

        boolean reversedOrder =
                matchesInput(
                        firstStack,
                        secondInput,
                        secondInputTag,
                        secondInputCount
                )
                        && matchesInput(
                        secondStack,
                        firstInput,
                        firstInputTag,
                        firstInputCount
                );

        return normalOrder || reversedOrder;
    }

    private boolean matchesInput(
            ItemStack stack,
            Identifier requiredInput,
            boolean requiredInputTag,
            int requiredCount
    ) {
        if (stack.isEmpty() || stack.getCount() < requiredCount) {
            return false;
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
}
