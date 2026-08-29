package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.world.item.ItemInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInstance.class)
public interface StackSizeItemInstanceMixin {

    @Inject(
            method = "getMaxStackSize",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gamefixes$increaseMaxStackSize(
            CallbackInfoReturnable<Integer> cir
    ) {
        if (cir.getReturnValue() == 99) {
            cir.setReturnValue(Gamefixes.MAX_STACK_SIZE);
        }
    }
}