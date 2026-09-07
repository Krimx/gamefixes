package com.krimx.gamefixes.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChestMenu.class)
public abstract class ChestMenuMixin {

    /**
     * ChestMenu normally creates ordinary Slots for its container.
     *
     * When the container is a Shulker Box, replace those slots with
     * ShulkerBoxSlot so vanilla's shulker-box nesting restriction is
     * preserved.
     */
    @Redirect(
            method = "addChestGrid",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/inventory/Slot"
            )
    )
    private Slot gamefixes$useShulkerBoxSlot(
            Container container,
            int slot,
            int x,
            int y
    ) {
        if (container instanceof ShulkerBoxBlockEntity) {
            return new ShulkerBoxSlot(container, slot, x, y);
        }

        return new Slot(container, slot, x, y);
    }
}