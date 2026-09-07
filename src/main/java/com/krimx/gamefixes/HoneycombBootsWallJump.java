package com.krimx.gamefixes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import static com.krimx.gamefixes.Gamefixes.HONEYCOMB_BOOTS;

public final class HoneycombBootsWallJump {

    private static final double WALL_JUMP_VERTICAL_VELOCITY = 0.42D;

    private HoneycombBootsWallJump() {
    }

    public static boolean isWearingHoneycombBoots(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET)
                .is(HONEYCOMB_BOOTS);
    }

    public static boolean canWallJump(Player player) {
        if (!isWearingHoneycombBoots(player)) {
            return false;
        }

        if (player.onGround()) {
            return false;
        }

        return player.horizontalCollision;
    }

    public static void performWallJump(Player player) {
        player.setDeltaMovement(
                player.getDeltaMovement().x,
                WALL_JUMP_VERTICAL_VELOCITY,
                player.getDeltaMovement().z
        );

        player.hurtMarked = true;
    }
}