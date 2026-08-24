package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.CustomBrewingRecipes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        targets = "net.minecraft.world.inventory.BrewingStandMenu$IngredientsSlot"
)
public abstract class BrewingStandIngredientsSlotMixin {

    @Inject(
            method = "mayPlace",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gamefixes$allowCustomIngredient(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (CustomBrewingRecipes.isCustomIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }
}