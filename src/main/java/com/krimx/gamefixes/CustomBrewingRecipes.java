package com.krimx.gamefixes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class CustomBrewingRecipes {

    private static final List<Recipe> RECIPES =
            new ArrayList<>();

    static {
        /*
         * Resin Clump + Ghast Tear -> Ghast Resin
         *
         * The recipe behaves like normal brewing:
         *
         * - Each occupied lower slot is processed independently.
         * - Empty lower slots are ignored.
         * - One ingredient is consumed for the entire brew cycle.
         * - Each matching lower slot produces its own result stack.
         */
        register(
                Items.RESIN_CLUMP,
                Items.GHAST_TEAR,
                Gamefixes.GHAST_RESIN,
                1
        );
    }

    private CustomBrewingRecipes() {
    }

    private static void register(
            Item input,
            Item ingredient,
            Item output,
            int outputCount
    ) {
        RECIPES.add(
                new Recipe(
                        input,
                        ingredient,
                        output,
                        outputCount
                )
        );
    }

    public static Recipe find(
            ItemStack input,
            ItemStack ingredient
    ) {
        if (input.isEmpty()
                || ingredient.isEmpty()) {
            return null;
        }

        for (Recipe recipe : RECIPES) {
            if (input.is(recipe.input())
                    && ingredient.is(recipe.ingredient())) {
                return recipe;
            }
        }

        return null;
    }

    public static boolean canBrew(
            ItemStack[] inputs,
            ItemStack ingredient
    ) {
        if (ingredient.isEmpty()) {
            return false;
        }

        for (ItemStack input : inputs) {
            if (find(input, ingredient) != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean isCustomInput(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        for (Recipe recipe : RECIPES) {
            if (stack.is(recipe.input())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isCustomIngredient(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        for (Recipe recipe : RECIPES) {
            if (stack.is(recipe.ingredient())) {
                return true;
            }
        }

        return false;
    }

    public record Recipe(
            Item input,
            Item ingredient,
            Item output,
            int outputCount
    ) {
    }
}