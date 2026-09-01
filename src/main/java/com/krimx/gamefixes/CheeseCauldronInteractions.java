package com.krimx.gamefixes;

import com.krimx.gamefixes.mixin.CauldronInteractionDispatcherInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class CheeseCauldronInteractions {

    private CheeseCauldronInteractions() {
    }

    public static CauldronInteraction.Dispatcher createDispatcher() {
        CauldronInteraction.Dispatcher dispatcher =
                new CauldronInteraction.Dispatcher();

        CauldronInteractionDispatcherInvoker invoker =
                (CauldronInteractionDispatcherInvoker) dispatcher;

        invoker.gamefixes$put(
                Items.AIR,
                CheeseCauldronInteractions::collectCheese
        );

        return dispatcher;
    }

    private static InteractionResult collectCheese(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack itemInHand
    ) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) < 3) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int cheeseCount = 6 + level.getRandom().nextInt(3);
        ItemStack cheese = new ItemStack(Gamefixes.CHEESE, cheeseCount);

        if (!player.getInventory().add(cheese)) {
            player.drop(cheese, false);
        }

        level.setBlock(
                pos,
                Blocks.CAULDRON.defaultBlockState(),
                3
        );

        return InteractionResult.SUCCESS;
    }
}
