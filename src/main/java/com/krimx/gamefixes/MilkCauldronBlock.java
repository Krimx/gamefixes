package com.krimx.gamefixes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome.Precipitation;
import org.jetbrains.annotations.Nullable;

public class MilkCauldronBlock extends LayeredCauldronBlock implements EntityBlock {

    public MilkCauldronBlock(
            Precipitation precipitation,
            CauldronInteraction.Dispatcher interactions,
            Properties properties
    ) {
        super(precipitation, interactions, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MilkCauldronBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (type != Gamefixes.MILK_CAULDRON_BLOCK_ENTITY) {
            return null;
        }

        return (tickLevel, pos, tickState, blockEntity) ->
                MilkCauldronBlockEntity.tick(
                        tickLevel,
                        pos,
                        tickState,
                        (MilkCauldronBlockEntity) blockEntity
                );
    }
}