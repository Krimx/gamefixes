package com.krimx.gamefixes.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin {

    private static final int SMOKER_RANGE = 5;

    /*
     * =========================================================
     * HIVE SEDATION
     * =========================================================
     *
     * Makes an active smoker count as a valid smoke source.
     */
    @Inject(
            method = "isSedated",
            at = @At("RETURN"),
            cancellable = true
    )
    private void gamefixes$checkForActiveSmoker(
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            return;
        }

        BeehiveBlockEntity beehive =
                (BeehiveBlockEntity) (Object) this;

        if (beehive.getLevel() == null) {
            return;
        }

        if (gamefixes$hasActiveSmoker(beehive)) {
            cir.setReturnValue(true);
        }
    }

    /*
     * =========================================================
     * PLAYER HARVESTING
     * =========================================================
     *
     * Prevents harvesting from releasing the bees when an
     * active smoker is nearby.
     */
    @Inject(
            method = "emptyAllLivingFromHive",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$preventBeeRelease(
            Player player,
            BlockState state,
            BeehiveBlockEntity.BeeReleaseStatus releaseStatus,
            CallbackInfo ci
    ) {
        BeehiveBlockEntity beehive =
                (BeehiveBlockEntity) (Object) this;

        if (beehive.getLevel() != null
                && gamefixes$hasActiveSmoker(beehive)) {

            ci.cancel();
        }
    }

    /*
     * =========================================================
     * NATURAL BEE RELEASE
     * =========================================================
     *
     * Bees that have finished their time inside the hive are
     * normally released by tickOccupants(), which calls the
     * private releaseOccupant() method.
     *
     * Returning false here prevents that individual bee from
     * being released while an active smoker is nearby.
     */
    @Redirect(
            method = "tickOccupants",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;releaseOccupant(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$Occupant;Ljava/util/List;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$BeeReleaseStatus;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean gamefixes$preventNaturalBeeRelease(
            Level level,
            BlockPos pos,
            BlockState state,
            BeehiveBlockEntity.Occupant occupant,
            List<net.minecraft.world.entity.Entity> storedInHives,
            BeehiveBlockEntity.BeeReleaseStatus releaseStatus,
            BlockPos savedFlowerPos
    ) {
        BlockPos hivePos = pos;

        for (int x = -SMOKER_RANGE; x <= SMOKER_RANGE; x++) {
            for (int z = -SMOKER_RANGE; z <= SMOKER_RANGE; z++) {

                if (x * x + z * z
                        > SMOKER_RANGE * SMOKER_RANGE) {
                    continue;
                }

                for (int y = -SMOKER_RANGE; y <= SMOKER_RANGE; y++) {
                    BlockPos smokerPos =
                            hivePos.offset(x, y, z);

                    BlockState smokerState =
                            level.getBlockState(smokerPos);

                    if (smokerState.is(Blocks.SMOKER)
                            && smokerState.hasProperty(
                            BlockStateProperties.LIT)
                            && smokerState.getValue(
                            BlockStateProperties.LIT)) {

                        return false;
                    }
                }
            }
        }

        /*
         * No active smoker: perform the normal vanilla release.
         */
        return BeehiveBlockEntityMixinInvoker
                .gamefixes$callReleaseOccupant(
                        level,
                        pos,
                        state,
                        occupant,
                        storedInHives,
                        releaseStatus,
                        savedFlowerPos
                );
    }

    /*
     * =========================================================
     * SMOKER SEARCH
     * =========================================================
     */

    private static boolean gamefixes$hasActiveSmoker(
            BeehiveBlockEntity beehive
    ) {
        Level level = beehive.getLevel();

        if (level == null) {
            return false;
        }

        BlockPos hivePos =
                beehive.getBlockPos();

        for (int x = -SMOKER_RANGE; x <= SMOKER_RANGE; x++) {
            for (int z = -SMOKER_RANGE; z <= SMOKER_RANGE; z++) {

                if (x * x + z * z
                        > SMOKER_RANGE * SMOKER_RANGE) {
                    continue;
                }

                for (int y = -SMOKER_RANGE; y <= SMOKER_RANGE; y++) {
                    BlockPos smokerPos =
                            hivePos.offset(x, y, z);

                    BlockState smokerState =
                            level.getBlockState(smokerPos);

                    if (smokerState.is(Blocks.SMOKER)
                            && smokerState.hasProperty(
                            BlockStateProperties.LIT)
                            && smokerState.getValue(
                            BlockStateProperties.LIT)) {

                        return true;
                    }
                }
            }
        }

        return false;
    }
}