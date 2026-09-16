package com.krimx.gamefixes.block;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.HashSet;
import java.util.Set;

public class CattailBlock extends DoublePlantBlock {

    private static final int MOSQUITO_COUNT = 6;

    private static final Set<BlockPos> MOSQUITO_SOURCES = new HashSet<>();

    public CattailBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);

        if (half == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());

            return below.is(this)
                    && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }

        return level.getFluidState(pos).isSourceOfType(Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return Fluids.WATER.getSource(false);
        }

        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        BlockPos sourcePos = pos.immutable();

        // Only create one swarm for this cattail.
        if (!MOSQUITO_SOURCES.add(sourcePos)) {
            return;
        }

        for (int i = 0; i < MOSQUITO_COUNT; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 6.0;
            double y = pos.getY() + 0.5 + random.nextDouble() * 3.0;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 6.0;

            // The velocity arguments are used to pass the cattail's
            // coordinates to the particle as its permanent source.
            level.addParticle(
                    Gamefixes.MOSQUITO_PARTICLE,
                    x,
                    y,
                    z,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            );
        }
    }

    public static void removeMosquitoSource(BlockPos pos) {
        MOSQUITO_SOURCES.remove(pos);
    }

    public static void clearMosquitoSources() {
        MOSQUITO_SOURCES.clear();
    }

    public static void clearMosquitoSourcesInChunk(int chunkX, int chunkZ) {
        MOSQUITO_SOURCES.removeIf(pos ->
                (pos.getX() >> 4) == chunkX
                        && (pos.getZ() >> 4) == chunkZ
        );
    }
}