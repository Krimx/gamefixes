package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.access.ContainerMenuDataAccess;
import com.krimx.gamefixes.access.MerchantMenuAccess;
import com.krimx.gamefixes.research.VillagerResearch;
import com.krimx.gamefixes.research.VillagerResearchData;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin
        implements MerchantMenuAccess {

    @Shadow
    private Merchant trader;

    @Unique
    private int gamefixes$selectedResearchSlot = -1;

    @Unique
    private DataSlot gamefixes$researchSlotCount;

    @Unique
    private DataSlot gamefixes$researchMode;

    @Unique
    private DataSlot gamefixes$researchFailed;

    @Unique
    private DataSlot gamefixes$researchCompletedMask;

    @Inject(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",
            at = @At("TAIL")
    )
    private void gamefixes$initResearchSync(
            int containerId,
            net.minecraft.world.entity.player.Inventory inventory,
            Merchant merchant,
            CallbackInfo ci
    ) {
        gamefixes$researchSlotCount =
                DataSlot.standalone();

        gamefixes$researchMode =
                DataSlot.standalone();

        gamefixes$researchFailed =
                DataSlot.standalone();

        gamefixes$researchCompletedMask =
                DataSlot.standalone();

        ContainerMenuDataAccess dataAccess =
                (ContainerMenuDataAccess) (Object) this;

        dataAccess.gamefixes$addDataSlot(
                gamefixes$researchSlotCount
        );

        dataAccess.gamefixes$addDataSlot(
                gamefixes$researchMode
        );

        dataAccess.gamefixes$addDataSlot(
                gamefixes$researchFailed
        );

        dataAccess.gamefixes$addDataSlot(
                gamefixes$researchCompletedMask
        );

        if (merchant instanceof Villager villager) {

            VillagerResearchData data =
                    VillagerResearch.getData(villager);

            gamefixes$researchSlotCount.set(
                    data.slotCount()
            );

            gamefixes$researchCompletedMask.set(
                    gamefixes$buildCompletedMask(data)
            );

        } else {

            gamefixes$researchSlotCount.set(0);
            gamefixes$researchCompletedMask.set(0);
        }

        gamefixes$researchMode.set(0);
        gamefixes$researchFailed.set(0);
    }

    @Unique
    private int gamefixes$buildCompletedMask(
            VillagerResearchData data
    ) {
        int mask = 0;

        for (int i = 0;
             i < data.slotCount();
             i++) {

            if (!data.isEmpty(i)) {
                mask |= (1 << i);
            }
        }

        return mask;
    }

    @Override
    public int gamefixes$getResearchSlots() {
        return gamefixes$researchSlotCount == null
                ? 0
                : gamefixes$researchSlotCount.get();
    }

    @Override
    public int gamefixes$getAvailableResearchSlots() {
        int total =
                gamefixes$getResearchSlots();

        int completed = 0;

        for (int i = 0; i < total; i++) {
            if (gamefixes$isResearchSlotCompleted(i)) {
                completed++;
            }
        }

        return Math.max(
                0,
                total - completed
        );
    }

    @Override
    public int gamefixes$getSelectedResearchSlot() {
        return gamefixes$selectedResearchSlot;
    }

    @Override
    public void gamefixes$setSelectedResearchSlot(
            int slot
    ) {
        gamefixes$selectedResearchSlot = slot;
    }

    @Override
    public boolean gamefixes$isResearchMode() {
        return gamefixes$researchMode != null
                && gamefixes$researchMode.get() != 0;
    }

    @Override
    public void gamefixes$setResearchMode(
            boolean researchMode
    ) {
        if (gamefixes$researchMode != null) {
            gamefixes$researchMode.set(
                    researchMode ? 1 : 0
            );
        }
    }

    @Override
    public boolean gamefixes$researchFailed() {
        return gamefixes$researchFailed != null
                && gamefixes$researchFailed.get() != 0;
    }

    @Override
    public void gamefixes$setResearchFailed(
            boolean failed
    ) {
        if (gamefixes$researchFailed != null) {
            gamefixes$researchFailed.set(
                    failed ? 1 : 0
            );
        }
    }

    @Override
    public boolean gamefixes$isResearchSlotCompleted(
            int slot
    ) {
        if (slot < 0
                || slot >= gamefixes$getResearchSlots()) {
            return false;
        }

        return (
                gamefixes$researchCompletedMask.get()
                        & (1 << slot)
        ) != 0;
    }

    @Override
    public void gamefixes$setResearchCompletedMask(
            int mask
    ) {
        if (gamefixes$researchCompletedMask != null) {
            gamefixes$researchCompletedMask.set(mask);
        }
    }

    @Override
    public Merchant gamefixes$getTrader() {
        return trader;
    }
}