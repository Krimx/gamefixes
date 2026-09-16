package com.krimx.gamefixes.worldgen;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.block.CattailBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Fluids;

public class CattailFeature implements Feature {

    public static final MapCodec<CattailFeature> CODEC =
            MapCodec.unit(CattailFeature::new);

    // ============================================================
    // Cluster tuning
    // ============================================================

    // Number of positions attempted for each cluster.
    private static final int CLUSTER_ATTEMPTS = 40;

    // Maximum horizontal distance from the feature origin.
    private static final int CLUSTER_RADIUS = 5;

    // Maximum horizontal distance from solid land.
    private static final int MAX_LAND_DISTANCE = 2;

    @Override
    public MapCodec<CattailFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        boolean placedAny = false;

        for (int i = 0; i < CLUSTER_ATTEMPTS; i++) {

            int offsetX =
                    random.nextInt(CLUSTER_RADIUS * 2 + 1)
                            - CLUSTER_RADIUS;

            int offsetZ =
                    random.nextInt(CLUSTER_RADIUS * 2 + 1)
                            - CLUSTER_RADIUS;

            int x = origin.getX() + offsetX;
            int z = origin.getZ() + offsetZ;

            /*
             * WORLD_SURFACE_WG gives us the highest non-air block
             * in this column during world generation.
             *
             * This lets us start near the surface instead of
             * scanning the entire world height.
             */
            int surfaceY = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    x,
                    z
            );

            /*
             * The height returned by the heightmap is the first
             * available Y above the highest non-air block.
             *
             * Start there and check a small amount below the
             * surface for our one-block-deep water position.
             */
            BlockPos.MutableBlockPos candidate =
                    new BlockPos.MutableBlockPos(
                            x,
                            surfaceY,
                            z
                    );

            /*
             * Check the surface position and the block immediately
             * below it.
             *
             * This is enough for the exact one-block-submerged
             * requirement without searching the entire column.
             */
            if (isValidCattailPosition(level, candidate)) {
                placeCattail(level, candidate);
                placedAny = true;
                continue;
            }

            candidate.move(0, -1, 0);

            if (isValidCattailPosition(level, candidate)) {
                placeCattail(level, candidate);
                placedAny = true;
            }
        }

        return placedAny;
    }

    private static boolean isValidCattailPosition(
            WorldGenLevel level,
            BlockPos pos
    ) {
        /*
         * The lower half of the cattail must occupy a full water
         * source block.
         */
        if (!level.getFluidState(pos).isSourceOfType(Fluids.WATER)) {
            return false;
        }

        /*
         * The block immediately below must be solid land.
         *
         * This guarantees that the cattail is exactly
         * one block submerged.
         *
         * Valid:
         *
         *     cattail
         *     water
         *     land
         *
         * Invalid:
         *
         *     cattail
         *     water
         *     water
         *     land
         */
        BlockPos below = pos.below();

        if (!isLand(level, below)) {
            return false;
        }

        /*
         * The upper half of the cattail must have room.
         */
        BlockPos above = pos.above();

        if (!level.getBlockState(above).isAir()) {
            return false;
        }

        /*
         * Finally, the water block must be within two blocks
         * horizontally of land.
         */
        return isNearLand(level, pos);
    }

    private static boolean isNearLand(
            WorldGenLevel level,
            BlockPos pos
    ) {
        for (int x = -MAX_LAND_DISTANCE;
             x <= MAX_LAND_DISTANCE;
             x++) {

            for (int z = -MAX_LAND_DISTANCE;
                 z <= MAX_LAND_DISTANCE;
                 z++) {

                /*
                 * The candidate's own block is already known to
                 * be water, so don't check it.
                 */
                if (x == 0 && z == 0) {
                    continue;
                }

                BlockPos check = pos.offset(x, 0, z);

                if (isLand(level, check)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isLand(
            WorldGenLevel level,
            BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        /*
         * Air cannot be land.
         */
        if (state.isAir()) {
            return false;
        }

        /*
         * A block containing water is not considered land.
         */
        if (!state.getFluidState().isEmpty()) {
            return false;
        }

        /*
         * Require the block to have a full collision shape.
         *
         * This prevents things such as grass, flowers, and other
         * non-solid vegetation from counting as shoreline land.
         */
        return state.isCollisionShapeFullBlock(level, pos);
    }

    private static void placeCattail(
            WorldGenLevel level,
            BlockPos pos
    ) {
        BlockState lower =
                Gamefixes.CATTAIL.defaultBlockState()
                        .setValue(
                                CattailBlock.HALF,
                                DoubleBlockHalf.LOWER
                        );

        BlockState upper =
                Gamefixes.CATTAIL.defaultBlockState()
                        .setValue(
                                CattailBlock.HALF,
                                DoubleBlockHalf.UPPER
                        );

        /*
         * Replace the water block with the lower cattail half.
         *
         * CattailBlock supplies its own water fluid state for the
         * lower half, preserving the water behavior.
         */
        level.setBlock(
                pos,
                lower,
                2
        );

        level.setBlock(
                pos.above(),
                upper,
                2
        );
    }
}