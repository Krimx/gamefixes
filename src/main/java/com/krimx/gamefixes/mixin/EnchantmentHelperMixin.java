package com.krimx.gamefixes.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.krimx.gamefixes.Gamefixes;

import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {



    @ModifyVariable(
            method = "getAvailableEnchantmentResults",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Stream<Holder<Enchantment>> gamefixes$removeMending(
            Stream<Holder<Enchantment>> source
    ) {
        if (Gamefixes.isMendingAllowed()) {
            return source;
        }

        return source.filter(enchantment ->
                !enchantment.getRegisteredName().equals("minecraft:mending")
        );
    }
}