package com.krimx.gamefixes;

import com.krimx.gamefixes.loot_bags.LootBagOutcomeExecutor;
import com.krimx.gamefixes.loot_bags.LootBagOutcomeExecutors;
import com.krimx.gamefixes.loot_bags.LootBagOutcomeSelector;
import com.krimx.gamefixes.loot_bags.Outcome;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LootBagItem extends Item {

    public LootBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        ServerLevel serverLevel =
                (ServerLevel) level;

        ServerPlayer serverPlayer =
                (ServerPlayer) player;

        ItemStack bag =
                player.getItemInHand(hand);

        LootBagProfile profile =
                new LootBagProfile(bag);

        Outcome outcome =
                LootBagOutcomeSelector.select(
                        profile,
                        serverLevel.getRandom()
                );

        if (outcome == null) {

            player.sendSystemMessage(
                    Component.literal(
                            "The Loot Bag contained nothing useful."
                    )
            );

        } else {

            LootBagOutcomeExecutor executor =
                    LootBagOutcomeExecutors.get(
                            outcome.getId()
                    );

            if (executor == null) {

                player.sendSystemMessage(
                        Component.literal(
                                "[GameFixes] Loot Bag outcome '"
                                        + outcome.getId()
                                        + "' has no executor."
                        )
                );

            } else {

                executor.execute(
                        serverLevel,
                        serverPlayer,
                        profile
                );
            }
        }

        if (!player.isCreative()) {
            bag.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}