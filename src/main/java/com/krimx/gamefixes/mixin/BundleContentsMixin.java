package com.krimx.gamefixes.mixin;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BundleContents.class)
public abstract class BundleContentsMixin {

    /**
     * Vanilla calculates the weight of a normal item as:
     *
     *     1 / item.getMaxStackSize()
     *
     * Double the effective bundle capacity by making the stack size
     * used for this calculation twice as large.
     *
     * This only affects the normal-item branch of getWeight().
     * Nested bundles and beehives have their own weight calculations
     * and are therefore left unchanged.
     */
    @Redirect(
            method = "getWeight",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemInstance;getMaxStackSize()I"
            )
    )
    private static int gamefixes$doubleBundleCapacity(ItemInstance item) {
        return item.getMaxStackSize() * 2;
    }
}