package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @ModifyVariable(
            method = "getGrowthSpeed",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static float gamefixes$abundanceGrowth(
            float blockSpeed,
            Block type,
            BlockGetter level,
            BlockPos pos
    ) {
        BlockState farmland = level.getBlockState(pos.below());

        if (farmland.is(Gamefixes.ABUNDANT_FARMLAND)) {
            if (farmland.getValueOrElse(FarmlandBlock.MOISTURE, 0) > 0) {
                return 9.0F;
            }

            return 2.0F;
        }

        return blockSpeed;
    }
}
