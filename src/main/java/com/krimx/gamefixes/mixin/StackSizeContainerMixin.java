package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public interface StackSizeContainerMixin {

    @Inject(
            method = "getMaxStackSize",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$increaseContainerStackSize(
            CallbackInfoReturnable<Integer> cir
    ) {
        cir.setReturnValue(Gamefixes.MAX_STACK_SIZE);
    }
}