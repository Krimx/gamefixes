package com.krimx.gamefixes.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxMenuMixin {

    /**
     * Replace the vanilla shulker box menu with a vanilla six-row chest menu.
     *
     * This gives the shulker box the normal 54-slot chest layout and
     * automatically uses the vanilla generic_54 chest screen.
     */
    @Overwrite
    protected AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory
    ) {
        return ChestMenu.sixRows(
                containerId,
                inventory,
                (Container) (Object) this
        );
    }
}