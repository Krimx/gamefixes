package com.krimx.gamefixes;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import static com.krimx.gamefixes.Gamefixes.DIAKRETE_HELMET;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_CHESTPLATE;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_LEGGINGS;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_BOOTS;

public final class DiakreteArmorFloating {
    private DiakreteArmorFloating() {
    }
    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(
            DiakreteArmorFloating::floatInWater
        );
    }

    private static void floatInWater(ServerLevel level) {
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Player playerEntity)) {
                continue;
            }

            if (!isInWater(playerEntity)) {
                continue;
            }

            if (!playerEntity.getItemBySlot(EquipmentSlot.HEAD).is(DIAKRETE_HELMET)) continue;
            if (!playerEntity.getItemBySlot(EquipmentSlot.CHEST).is(DIAKRETE_CHESTPLATE)) continue;
            if (!playerEntity.getItemBySlot(EquipmentSlot.LEGS).is(DIAKRETE_LEGGINGS)) continue;
            if (!playerEntity.getItemBySlot(EquipmentSlot.FEET).is(DIAKRETE_BOOTS)) continue;

            Vec3 velocity = playerEntity.getDeltaMovement();

            playerEntity.setDeltaMovement(
                    velocity.x,
                    Math.min(velocity.y + 0.05, 0.3),
                    velocity.z
            );
        }
    }

    private static boolean isInWater(Player entity) {

        BlockPos pos = BlockPos.containing(
                entity.getX(),
                entity.getY(),
                entity.getZ()
        );

        return entity.level()
                .getBlockState(pos)
                .is(Blocks.WATER);
    }
}
