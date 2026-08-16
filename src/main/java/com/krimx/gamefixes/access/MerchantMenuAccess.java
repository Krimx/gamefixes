package com.krimx.gamefixes.access;

import net.minecraft.world.item.trading.Merchant;

public interface MerchantMenuAccess {

    int gamefixes$getResearchSlots();

    int gamefixes$getAvailableResearchSlots();

    int gamefixes$getSelectedResearchSlot();

    void gamefixes$setSelectedResearchSlot(int slot);

    boolean gamefixes$isResearchMode();

    void gamefixes$setResearchMode(boolean researchMode);

    boolean gamefixes$researchFailed();

    void gamefixes$setResearchFailed(boolean failed);

    boolean gamefixes$isResearchSlotCompleted(int slot);

    void gamefixes$setResearchCompletedMask(int mask);

    Merchant gamefixes$getTrader();
}