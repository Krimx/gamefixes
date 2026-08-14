package com.krimx.gamefixes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.HashMap;
import java.util.Map;

public class FuelSpeedRegistry {
    private static final Map<Item, Integer> FUEL_SPEEDS = new HashMap<>();

    static {
        // --- TIER 3: EXTREME HEAT (3x Cooking Speed) ---
        registerSpeed(Items.LAVA_BUCKET, 3);

        // --- TIER 2: HIGH HEAT & DENSE FUELS (2x Cooking Speed) ---
        registerSpeed(Items.BLAZE_ROD, 2);
        registerSpeed(Items.BLAZE_POWDER, 2);
        registerSpeed(Items.MAGMA_BLOCK, 2);
        registerSpeed(Items.COAL_BLOCK, 2);
        registerSpeed(Items.DRIED_KELP_BLOCK, 2);

        // --- TIER 1: STANDARD FUELS (1x Speed) ---
        // Coal, Charcoal, Logs, Planks, Sticks, Saplings, Bamboo,
        // Boats, Wooden Tools, Wool, Carpets, etc. default to 1x via getOrDefault().
    }

    /**
     * Registers or overrides a fuel's cooking speed multiplier.
     */
    public static void registerSpeed(Item item, int speed) {
        if (item != null) {
            FUEL_SPEEDS.put(item, speed);
        }
    }

    /**
     * Gets the cooking speed multiplier for a fuel item.
     * Defaults to 1x for standard fuels.
     */
    public static int getSpeed(Item item) {
        return FUEL_SPEEDS.getOrDefault(item, 1);
    }
}