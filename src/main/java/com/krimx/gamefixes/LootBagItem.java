package com.krimx.gamefixes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LootBagItem extends Item {

    public LootBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay displayComponent,
            Consumer<Component> textConsumer,
            TooltipFlag type
    ) {
        List<Identifier> contents =
                stack.getOrDefault(
                        LootBagComponents.LOOT_BAG_CONTENTS,
                        List.of()
                );

        if (contents.isEmpty()) {
            return;
        }

        textConsumer.accept(
                Component.literal("Contains:")
        );

        for (Identifier identifier : contents) {

            Item item =
                    BuiltInRegistries.ITEM
                            .getOptional(identifier)
                            .orElse(null);

            if (item == null) {
                continue;
            }

            ItemStack itemStack =
                    new ItemStack(item);

            textConsumer.accept(
                    Component.literal("  • ")
                            .append(itemStack.getHoverName())
            );
        }
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

        for (
                Map.Entry<Identifier, Integer> entry :
                profile.getScores().entrySet()
        ) {
            player.sendSystemMessage(
                    Component.literal(
                            entry.getKey() + ": " + entry.getValue()
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}