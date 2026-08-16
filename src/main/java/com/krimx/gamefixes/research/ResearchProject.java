package com.krimx.gamefixes.research;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public final class ResearchProject {

    private final String id;
    private final String displayName;

    private final Identifier profession;

    private final Identifier firstInput;
    private final int firstInputCount;

    private final Identifier secondInput;
    private final int secondInputCount;

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
            Identifier secondInput,
            int secondInputCount,
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
        this.secondInput = secondInput;
        this.secondInputCount = secondInputCount;
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

    public Identifier getSecondInputId() {
        return secondInput;
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
            Identifier firstItem,
            int firstCount,
            Identifier secondItem,
            int secondCount
    ) {
        boolean normalOrder =
                firstItem.equals(firstInput)
                        && firstCount >= firstInputCount
                        && secondItem.equals(secondInput)
                        && secondCount >= secondInputCount;

        boolean reversedOrder =
                firstItem.equals(secondInput)
                        && firstCount >= secondInputCount
                        && secondItem.equals(firstInput)
                        && secondCount >= firstInputCount;

        return normalOrder || reversedOrder;
    }
}