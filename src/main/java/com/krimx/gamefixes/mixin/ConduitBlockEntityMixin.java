package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin {

    /*
     * The complete transformation takes three Minecraft days.
     *
     * 1 Minecraft day = 24,000 ticks.
     * 3 Minecraft days = 72,000 ticks.
     *
     * There are 42 frame blocks, so one block is transformed
     * approximately every 1,714 ticks.
     */
    @Unique
    private static final long GAMEFIXES_TRANSFORMATION_DURATION =
            3L * 24_000L;

    @Unique
    private static final String GAMEFIXES_TRANSFORMATION_START =
            "GamefixesTransformationStart";

    /*
     * The game time at which this conduit first reached a complete
     * 42-block frame.
     *
     * -1 means the transformation has not started yet.
     */
    @Unique
    private long gamefixes$transformationStartTime = -1L;

    @Inject(
            method = "serverTick",
            at = @At("TAIL")
    )
    private static void gamefixes$convertFrameBlocks(
            Level level,
            BlockPos pos,
            BlockState state,
            ConduitBlockEntity blockEntity,
            CallbackInfo ci
    ) {
        if (level.isClientSide()) {
            return;
        }

        ConduitBlockEntityMixin conduit =
                (ConduitBlockEntityMixin) (Object) blockEntity;

        List<BlockPos> framePositions =
                getFramePositions(pos);

        /*
         * The transformation requires the complete 42-block
         * conduit frame.
         *
         * Twilight Prismarine also counts here so that already
         * transformed blocks remain part of the frame.
         */
        if (!hasCompleteFrame(level, framePositions)) {
            return;
        }

        long gameTime =
                level.getGameTime();

        /*
         * Start the transformation timer the first time the
         * conduit reaches full power.
         */
        if (conduit.gamefixes$transformationStartTime < 0L) {
            conduit.gamefixes$transformationStartTime =
                    gameTime;

            blockEntity.setChanged();

            return;
        }

        /*
         * Work out how far through the three-day transformation
         * we should be.
         */
        long elapsed =
                gameTime
                        - conduit.gamefixes$transformationStartTime;

        if (elapsed < 0L) {
            /*
             * Protect against unusual world-time changes.
             */
            conduit.gamefixes$transformationStartTime =
                    gameTime;

            blockEntity.setChanged();

            return;
        }

        int blocksThatShouldBeTransformed =
                (int) Math.min(
                        42L,
                        (
                                elapsed
                                        * 42L
                        )
                                / GAMEFIXES_TRANSFORMATION_DURATION
                );

        /*
         * Transform only as many blocks as the elapsed time
         * allows.
         */
        int transformedBlocks = 0;

        for (BlockPos framePos : framePositions) {
            BlockState frameState =
                    level.getBlockState(framePos);

            if (frameState.is(Gamefixes.TWILIGHT_PRISMARINE)) {
                transformedBlocks++;
            }
        }

        if (transformedBlocks >= blocksThatShouldBeTransformed) {
            return;
        }

        /*
         * Convert one block at a time.
         *
         * The ordering is deterministic so the process looks
         * consistent rather than randomly changing blocks.
         */
        for (BlockPos framePos : framePositions) {
            BlockState frameState =
                    level.getBlockState(framePos);

            if (!isVanillaConduitFrameBlock(frameState)) {
                continue;
            }

            level.setBlock(
                    framePos,
                    Gamefixes.TWILIGHT_PRISMARINE.defaultBlockState(),
                    3
            );

            break;
        }
    }

    @Inject(
            method = "saveAdditional",
            at = @At("TAIL")
    )
    private void gamefixes$saveTransformationProgress(
            ValueOutput output,
            CallbackInfo ci
    ) {
        if (gamefixes$transformationStartTime >= 0L) {
            output.putLong(
                    GAMEFIXES_TRANSFORMATION_START,
                    gamefixes$transformationStartTime
            );
        }
    }

    @Inject(
            method = "loadAdditional",
            at = @At("TAIL")
    )
    private void gamefixes$loadTransformationProgress(
            ValueInput input,
            CallbackInfo ci
    ) {
        gamefixes$transformationStartTime =
                input.getLongOr(
                        GAMEFIXES_TRANSFORMATION_START,
                        -1L
                );
    }

    @Unique
    private static boolean hasCompleteFrame(
            Level level,
            List<BlockPos> framePositions
    ) {
        for (BlockPos framePos : framePositions) {
            BlockState frameState =
                    level.getBlockState(framePos);

            if (!isConduitFrameBlock(frameState)) {
                return false;
            }
        }

        return true;
    }

    @Unique
    private static boolean isConduitFrameBlock(
            BlockState state
    ) {
        return isVanillaConduitFrameBlock(state)
                || state.is(Gamefixes.TWILIGHT_PRISMARINE);
    }

    @Unique
    private static boolean isVanillaConduitFrameBlock(
            BlockState state
    ) {
        return state.is(Blocks.PRISMARINE)
                || state.is(Blocks.DARK_PRISMARINE)
                || state.is(Blocks.PRISMARINE_BRICKS)
                || state.is(Blocks.SEA_LANTERN);
    }

    @Unique
    private static List<BlockPos> getFramePositions(
            BlockPos conduitPos
    ) {
        List<BlockPos> positions =
                new ArrayList<>(42);

        /*
         * Horizontal ring.
         */
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2
                        || Math.abs(z) == 2) {

                    positions.add(
                            conduitPos.offset(
                                    x,
                                    0,
                                    z
                            )
                    );
                }
            }
        }

        /*
         * North/south vertical ring.
         */
        for (int y = -2; y <= 2; y++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(y) == 2
                        || Math.abs(z) == 2) {

                    positions.add(
                            conduitPos.offset(
                                    0,
                                    y,
                                    z
                            )
                    );
                }
            }
        }

        /*
         * East/west vertical ring.
         */
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (Math.abs(x) == 2
                        || Math.abs(y) == 2) {

                    positions.add(
                            conduitPos.offset(
                                    x,
                                    y,
                                    0
                            )
                    );
                }
            }
        }

        return positions;
    }
}