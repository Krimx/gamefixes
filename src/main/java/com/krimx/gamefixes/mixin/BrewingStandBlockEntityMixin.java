package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.CustomBrewingRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {

    @Inject(
            method = "isBrewable",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void gamefixes$allowCustomBrewing(
            PotionBrewing potionBrewing,
            NonNullList<ItemStack> items,
            CallbackInfoReturnable<Boolean> cir
    ) {
        /*
         * Vanilla already knows how to brew this setup.
         * Do not interfere with normal brewing.
         */
        if (cir.getReturnValue()) {
            return;
        }

        /*
         * Slot layout:
         *
         * 0 = first brewing input
         * 1 = second brewing input
         * 2 = third brewing input
         * 3 = ingredient
         * 4 = fuel
         */
        ItemStack[] inputs = {
                items.get(0),
                items.get(1),
                items.get(2)
        };

        ItemStack ingredient =
                items.get(3);

        if (CustomBrewingRecipes.canBrew(
                inputs,
                ingredient
        )) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "doBrew",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gamefixes$doCustomBrewing(
            Level level,
            BlockPos pos,
            NonNullList<ItemStack> items,
            CallbackInfo ci
    ) {
        ItemStack ingredient =
                items.get(3);

        if (ingredient.isEmpty()) {
            return;
        }

        /*
         * Determine whether this brewing cycle contains
         * at least one custom recipe.
         */
        boolean hasCustomRecipe = false;

        for (int slot = 0; slot < 3; slot++) {
            if (CustomBrewingRecipes.find(
                    items.get(slot),
                    ingredient
            ) != null) {
                hasCustomRecipe = true;
                break;
            }
        }

        if (!hasCustomRecipe) {
            /*
             * This is a completely vanilla brewing operation.
             */
            return;
        }

        /*
         * Process each lower slot independently.
         *
         * This deliberately does NOT require all three slots
         * to contain the same item.
         */
        for (int slot = 0; slot < 3; slot++) {

            ItemStack input =
                    items.get(slot);

            CustomBrewingRecipes.Recipe recipe =
                    CustomBrewingRecipes.find(
                            input,
                            ingredient
                    );

            if (recipe == null) {
                continue;
            }

            /*
             * Consume one input item.
             */
            input.shrink(1);

            /*
             * Put the result in the same slot.
             *
             * One input item produces one result stack,
             * exactly like one potion slot being processed.
             */
            items.set(
                    slot,
                    new ItemStack(
                            recipe.output(),
                            recipe.outputCount()
                    )
            );
        }

        /*
         * The ingredient is consumed once per brewing cycle,
         * regardless of whether one, two, or three input slots
         * were processed.
         */
        ingredient.shrink(1);

        ci.cancel();
    }

    @Inject(
            method = "canPlaceItem",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gamefixes$allowCustomAutomation(
            int slot,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        /*
         * Allow hoppers/droppers/etc. to insert custom
         * brewing inputs into the three lower slots.
         */
        if (slot >= 0
                && slot <= 2
                && CustomBrewingRecipes.isCustomInput(stack)) {
            cir.setReturnValue(true);
            return;
        }

        /*
         * Allow custom ingredients into the ingredient slot.
         */
        if (slot == 3
                && CustomBrewingRecipes.isCustomIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }
}