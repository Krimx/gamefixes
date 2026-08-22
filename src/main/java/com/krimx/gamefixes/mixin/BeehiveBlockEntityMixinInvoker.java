package com.krimx.gamefixes.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public interface BeehiveBlockEntityMixinInvoker {

    @Invoker("releaseOccupant")
    static boolean gamefixes$callReleaseOccupant(
            Level level,
            BlockPos pos,
            BlockState state,
            BeehiveBlockEntity.Occupant occupant,
            List<Entity> storedInHives,
            BeehiveBlockEntity.BeeReleaseStatus releaseStatus,
            BlockPos savedFlowerPos
    ) {
        throw new AssertionError();
    }
}