package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.access.ContainerMenuDataAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin
        implements ContainerMenuDataAccess {

    @Shadow
    protected abstract DataSlot addDataSlot(DataSlot dataSlot);

    @Override
    public DataSlot gamefixes$addDataSlot(DataSlot dataSlot) {
        return addDataSlot(dataSlot);
    }
}