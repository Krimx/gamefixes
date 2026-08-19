package com.krimx.gamefixes.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    private final int repairCostFactor = 5;

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private int repairItemCountCost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void gamefixes$useEchoShardToReduceRepairCost(CallbackInfo ci) {
        AnvilMenu anvil = (AnvilMenu) (Object) this;

        ItemStack target = anvil.getSlot(0).getItem();
        ItemStack material = anvil.getSlot(1).getItem();

        // Leave normal vanilla anvil behavior unchanged.
        if (!material.is(Items.ECHO_SHARD)) {
            return;
        }

        // Echo Shards only work on an item that has prior-work cost to remove.
        int currentRepairCost = target.getOrDefault(
                DataComponents.REPAIR_COST,
                0
        );

        if (target.isEmpty() || currentRepairCost <= 0) {
            anvil.getSlot(2).set(ItemStack.EMPTY);
            this.cost.set(0);
            this.repairItemCountCost = 0;
            ci.cancel();
            return;
        }

        ItemStack result = target.copy();

        // One Echo Shard reduces the stored prior-work cost by one.
        result.set(
                DataComponents.REPAIR_COST,
                Math.max(0, currentRepairCost - repairCostFactor)
        );

        anvil.getSlot(2).set(result);

        // One experience level and one Echo Shard per use.
        this.cost.set(1);
        this.repairItemCountCost = 1;

        // Prevent vanilla from rejecting the Echo Shard as an invalid repair item.
        ci.cancel();
    }
}