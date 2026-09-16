package com.krimx.gamefixes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import static com.krimx.gamefixes.Gamefixes.HONEYCOMB_BOOTS;

public final class HoneycombBootsWallJump {

    private static final double WALL_JUMP_VERTICAL_VELOCITY = 0.42D;
    private static final double WALL_CONTACT_DISTANCE = 0.15D;

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

        /*
         * horizontalCollision can be stale for a tick when the client
         * sends the wall-jump packet. Check the actual block collision
         * geometry around the player as well.
         */
        if (player.horizontalCollision) {
            return true;
        }

        AABB wallCheckBox =
                player.getBoundingBox().inflate(
                        WALL_CONTACT_DISTANCE,
                        0.0D,
                        WALL_CONTACT_DISTANCE
                );

        return player.level()
                .getBlockCollisions(player, wallCheckBox)
                .iterator()
                .hasNext();
    }

    public static void performWallJump(Player player) {
        player.setDeltaMovement(
                player.getDeltaMovement().x,
                Math.max(
                        player.getDeltaMovement().y,
                        WALL_JUMP_VERTICAL_VELOCITY
                ),
                player.getDeltaMovement().z
        );
    }
}
