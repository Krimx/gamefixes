package com.krimx.gamefixes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static com.krimx.gamefixes.Gamefixes.DIAKRETE_HELMET;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_CHESTPLATE;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_LEGGINGS;
import static com.krimx.gamefixes.Gamefixes.DIAKRETE_BOOTS;

public final class DiakreteArmorFloating {

    private DiakreteArmorFloating() {
    }

    public static boolean shouldFloat(Player player) {
        if (!player.isInWater()) {
            return false;
        }

        if (player.isCrouching()) {
            return false;
        }

        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(DIAKRETE_HELMET)) {
            return false;
        }

        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(DIAKRETE_CHESTPLATE)) {
            return false;
        }

        if (!player.getItemBySlot(EquipmentSlot.LEGS).is(DIAKRETE_LEGGINGS)) {
            return false;
        }

        if (!player.getItemBySlot(EquipmentSlot.FEET).is(DIAKRETE_BOOTS)) {
            return false;
        }

        return true;
    }

    public static Vec3 applyFloatingVelocity(
            Player player,
            Vec3 velocity,
            Vec3 movementInput
    ) {
        if (!shouldFloat(player)) {
            return velocity;
        }

        // Allow the player to swim downward.
        if (movementInput.y < 0) {
            return velocity;
        }

        return new Vec3(
                velocity.x,
                Math.max(velocity.y, Gamefixes.diakreteArmorFloatSpeed),
                velocity.z
        );
    }
}