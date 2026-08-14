package com.krimx.gamefixes.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MaceItemMixin {

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void gamefixes$maceUseDuration(
            ItemStack stack,
            LivingEntity user,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (stack.getItem() instanceof MaceItem) {
            cir.setReturnValue(72000);
        }
    }
}