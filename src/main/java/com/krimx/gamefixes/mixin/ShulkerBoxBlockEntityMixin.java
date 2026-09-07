package com.krimx.gamefixes.mixin;

import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {

    /**
     * Expand all vanilla 27-slot shulker box constants to 54 slots.
     */
    @ModifyConstant(
            method = "*",
            constant = @Constant(intValue = 27)
    )
    private int gamefixes$expandShulkerStorage(int value) {
        return 54;
    }
}