package com.krimx.gamefixes.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.Stream;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyLootFunctionMixin {

    @ModifyVariable(
            method = "run",
            at = @At("STORE"),
            ordinal = 0
    )
    private Stream<Holder<Enchantment>> gamefixes$removeMending(
            Stream<Holder<Enchantment>> source
    ) {
        return source.filter(enchantment ->
                !enchantment.getRegisteredName().equals("minecraft:mending")
        );
    }
}