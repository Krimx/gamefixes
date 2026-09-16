package com.krimx.gamefixes.block;

import com.krimx.gamefixes.block.entity.TwilightPrismarineBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TwilightPrismarineBlock extends BaseEntityBlock {

    public static final MapCodec<TwilightPrismarineBlock> CODEC =
            MapCodec.unit(
                    () -> new TwilightPrismarineBlock(
                            BlockBehaviour.Properties.of()
                    )
            );

    public TwilightPrismarineBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TwilightPrismarineBlockEntity(
                pos,
                state
        );
    }
}