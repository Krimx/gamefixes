package com.krimx.gamefixes;

import com.krimx.gamefixes.mixin.CauldronInteractionDispatcherInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class MilkCauldronInteractions {

    private MilkCauldronInteractions() {
    }

    public static CauldronInteraction.Dispatcher createDispatcher() {
        CauldronInteraction.Dispatcher dispatcher =
                new CauldronInteraction.Dispatcher();

        CauldronInteractionDispatcherInvoker invoker =
                (CauldronInteractionDispatcherInvoker) dispatcher;

        invoker.gamefixes$put(
                Items.MILK_BUCKET,
                (state, level, pos, player, hand, itemInHand) ->
                        fillWithMilkBucket(
                                state,
                                level,
                                pos,
                                player,
                                hand,
                                itemInHand
                        )
        );

        invoker.gamefixes$put(
                Items.GLASS_BOTTLE,
                MilkCauldronInteractions::fillMilkBottle
        );

        return dispatcher;
    }

    public static void registerEmptyCauldronInteractions() {
        CauldronInteractionDispatcherInvoker invoker =
                (CauldronInteractionDispatcherInvoker) CauldronInteractions.EMPTY;

        invoker.gamefixes$put(
                Items.MILK_BUCKET,
                (state, level, pos, player, hand, itemInHand) ->
                        fillEmptyCauldronWithMilkBucket(
                                level,
                                pos,
                                player,
                                hand,
                                itemInHand
                        )
        );

        invoker.gamefixes$put(
                Items.GLASS_BOTTLE,
                MilkCauldronInteractions::fillEmptyCauldronWithMilkBottle
        );
    }

    private static InteractionResult fillEmptyCauldronWithMilkBucket(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack itemInHand
    ) {
        return CauldronInteractions.emptyBucket(
                level,
                pos,
                player,
                hand,
                itemInHand,
                Gamefixes.MILK_CAULDRON.defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY
        );
    }

    private static InteractionResult fillWithMilkBucket(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack itemInHand
    ) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) >= 3) {
            return InteractionResult.PASS;
        }

        return CauldronInteractions.emptyBucket(
                level,
                pos,
                player,
                hand,
                itemInHand,
                state.setValue(LayeredCauldronBlock.LEVEL, 3),
                SoundEvents.BUCKET_EMPTY
        );
    }

    private static InteractionResult fillEmptyCauldronWithMilkBottle(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack itemInHand
    ) {
        level.setBlock(
                pos,
                Gamefixes.MILK_CAULDRON.defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL, 1),
                3
        );

        ItemStack result = ItemUtils.createFilledResult(
                itemInHand,
                player,
                new ItemStack(Gamefixes.MILK_BOTTLE)
        );

        player.setItemInHand(hand, result);

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult fillMilkBottle(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack itemInHand
    ) {
        int currentLevel =
                state.getValue(LayeredCauldronBlock.LEVEL);

        int newLevel = currentLevel - 1;

        if (newLevel <= 0) {
            level.setBlock(
                    pos,
                    Blocks.CAULDRON.defaultBlockState(),
                    3
            );
        } else {
            level.setBlock(
                    pos,
                    state.setValue(
                            LayeredCauldronBlock.LEVEL,
                            newLevel
                    ),
                    3
            );
        }

        ItemStack result = ItemUtils.createFilledResult(
                itemInHand,
                player,
                new ItemStack(Gamefixes.MILK_BOTTLE)
        );

        player.setItemInHand(hand, result);

        return InteractionResult.SUCCESS;
    }
}