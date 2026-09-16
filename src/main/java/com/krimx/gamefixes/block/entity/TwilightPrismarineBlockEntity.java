package com.krimx.gamefixes.block.entity;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TwilightPrismarineBlockEntity extends TheEndPortalBlockEntity {

    public TwilightPrismarineBlockEntity(BlockPos pos, BlockState state) {
        super(
                Gamefixes.TWILIGHT_PRISMARINE_BLOCK_ENTITY,
                pos,
                state
        );
    }

    @Override
    public boolean shouldRenderFace(Direction direction) {
        return true;
    }
}