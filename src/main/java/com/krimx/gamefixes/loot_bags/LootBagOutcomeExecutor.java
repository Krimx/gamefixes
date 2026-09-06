package com.krimx.gamefixes.loot_bags;

import com.krimx.gamefixes.LootBagProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface LootBagOutcomeExecutor {

    void execute(
            ServerLevel level,
            ServerPlayer player,
            LootBagProfile profile
    );
}