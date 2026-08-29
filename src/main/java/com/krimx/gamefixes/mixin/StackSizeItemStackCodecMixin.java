package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExtraCodecs.class)
public abstract class StackSizeItemStackCodecMixin {

    @Inject(
            method = "intRange",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gamefixes$increaseItemStackCountLimit(
            int minInclusive,
            int maxInclusive,
            CallbackInfoReturnable<Codec<Integer>> cir
    ) {
        if (minInclusive == 1 && maxInclusive == 99) {
            cir.setReturnValue(ExtraCodecs.intRange(1, Gamefixes.MAX_STACK_SIZE));
        }
    }
}