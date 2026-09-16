package com.krimx.gamefixes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HydratedFarmlandBlock extends FarmlandBlock {

    public HydratedFarmlandBlock(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (state.getValue(MOISTURE) < MAX_MOISTURE) {
            level.setBlock(
                    pos,
                    state.setValue(MOISTURE, MAX_MOISTURE),
                    2
            );
        }
    }

    @Override
    public void fallOn(
            Level level,
            BlockState state,
            BlockPos pos,
            Entity entity,
            double fallDistance
    ) {
        // Hydrated Farmland cannot be trampled.
    }
}