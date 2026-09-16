package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.CustomBrewingRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
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
            ServerLevel level,
            BrewingStandBlockEntity entity,
            CallbackInfoReturnable<Boolean> cir
    ) {
        /*
         * Vanilla already knows how to brew this setup.
         * Do not interfere with normal brewing.
         */
        if (cir.getReturnValue()) {
            return;
        }

        ItemStack[] inputs = {
                entity.getItem(0),
                entity.getItem(1),
                entity.getItem(2)
        };

        ItemStack ingredient = entity.getItem(3);

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
            ServerLevel level,
            BlockPos pos,
            BrewingStandBlockEntity entity,
            CallbackInfo ci
    ) {
        ItemStack ingredient = entity.getItem(3);

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
                    entity.getItem(slot),
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
            ItemStack input = entity.getItem(slot);

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
             */
            entity.setItem(
                    slot,
                    new ItemStack(
                            recipe.output(),
                            recipe.outputCount()
                    )
            );
        }

        /*
         * The ingredient is consumed once per brewing cycle.
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