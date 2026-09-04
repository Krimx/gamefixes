package com.krimx.gamefixes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class LootBagCraftingRecipe extends CustomRecipe {

    public static final LootBagCraftingRecipe INSTANCE =
            new LootBagCraftingRecipe();

    public static final MapCodec<LootBagCraftingRecipe> CODEC =
            MapCodec.unit(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, LootBagCraftingRecipe>
            STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    public static final RecipeSerializer<LootBagCraftingRecipe> SERIALIZER =
            new RecipeSerializer<>(
                    CODEC,
                    STREAM_CODEC
            );

    public static void initialize() {
        Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(
                        Gamefixes.MOD_ID,
                        "loot_bag_crafting"
                ),
                SERIALIZER
        );
    }

    @Override
    public boolean matches(
            CraftingInput input,
            Level level
    ) {
        return getIngredients(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        List<ItemStack> ingredients =
                getIngredients(input);

        if (ingredients == null) {
            return ItemStack.EMPTY;
        }

        ItemStack bag = ItemStack.EMPTY;

        for (ItemStack stack : input.items()) {
            if (stack.is(Gamefixes.LOOT_BAG)) {
                bag = stack;
                break;
            }
        }

        ItemStack result = bag.copy();

        List<Identifier> contents = new ArrayList<>(
                bag.getOrDefault(
                        LootBagComponents.LOOT_BAG_CONTENTS,
                        List.of()
                )
        );

        for (ItemStack ingredient : ingredients) {
            contents.add(
                    BuiltInRegistries.ITEM.getKey(
                            ingredient.getItem()
                    )
            );
        }

        result.set(
                LootBagComponents.LOOT_BAG_CONTENTS,
                List.copyOf(contents)
        );

        return result;
    }

    private static List<ItemStack> getIngredients(
            CraftingInput input
    ) {
        ItemStack bag = ItemStack.EMPTY;
        List<ItemStack> ingredients = new ArrayList<>();

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Gamefixes.LOOT_BAG)) {
                if (!bag.isEmpty()) {
                    return null;
                }

                bag = stack;
            } else {
                ingredients.add(stack);
            }
        }

        if (bag.isEmpty() || ingredients.isEmpty()) {
            return null;
        }

        List<Identifier> existingContents =
                bag.getOrDefault(
                        LootBagComponents.LOOT_BAG_CONTENTS,
                        List.of()
                );

        if (existingContents.size() + ingredients.size() > 8) {
            return null;
        }

        for (ItemStack ingredient : ingredients) {
            Identifier ingredientId =
                    BuiltInRegistries.ITEM.getKey(
                            ingredient.getItem()
                    );

            if (existingContents.contains(ingredientId)) {
                return null;
            }
        }

        return ingredients;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}