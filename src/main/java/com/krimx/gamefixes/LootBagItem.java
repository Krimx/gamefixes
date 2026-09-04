package com.krimx.gamefixes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

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
            return InteractionResult.SUCCESS;
        }

        ItemStack bag = player.getItemInHand(hand);

        LootBagProfile profile =
                new LootBagProfile(bag);

        for (Map.Entry<net.minecraft.resources.Identifier, Integer> entry :
                profile.getScores().entrySet()) {
            player.sendSystemMessage(
                    Component.literal(
                            entry.getKey() + ": " + entry.getValue()
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}
