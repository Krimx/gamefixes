package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.GlowSquidLeggingsLighting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {

    @Inject(
            method = "getEmission",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$glowSquidLeggingsEmission(
            long packedPos,
            BlockState state,
            CallbackInfoReturnable<Integer> cir
    ) {
        BlockPos pos = BlockPos.of(packedPos);

        BlockGetter level =
                ((LightEngineAccessor) this)
                        .gamefixes$getChunkSource()
                        .getLevel();

        if (level instanceof ServerLevel serverLevel) {
            if (GlowSquidLeggingsLighting.isLightSource(
                    serverLevel,
                    pos
            )) {
                cir.setReturnValue(
                        GlowSquidLeggingsLighting.getLightLevel()
                );
            }

            return;
        }

        if (GlowSquidLeggingsLighting.isClientLightSource(pos)) {
            cir.setReturnValue(
                    GlowSquidLeggingsLighting.getLightLevel()
            );
        }
    }
}