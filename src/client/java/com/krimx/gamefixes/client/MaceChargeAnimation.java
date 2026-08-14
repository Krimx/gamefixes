package com.krimx.gamefixes.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class MaceChargeAnimation {

    // --- Charge ---
    public static final int OPTIMAL_CHARGE_TICKS = 20;
    public static final int OVERCHARGE_END_TICKS = 60;

    // --- Pullback ---
    public static final float PULLBACK_X = 0.0F;
    public static final float PULLBACK_Y = 0.0F;
    public static final float PULLBACK_Z = 0.25F;

    // --- Dip ---
    public static final float DIP_AMOUNT = -0.06F;
    public static final float DIP_PEAK = 0.7F;

    // --- Overcharge ---
    public static final float OVERCHARGE_LOWER_AMOUNT = -0.15F;
    public static final float OVERCHARGE_START_EASING = 2.0F;
    public static final float OVERCHARGE_END_EASING = 2.0F;

    // --- Charge rotation ---
    public static final float ROTATION_X = -20.0F;
    public static final float ROTATION_Y = 0.0F;
    public static final float ROTATION_Z = 0.0F;

    // --- Charge easing ---
    public static final float START_EASING_POWER = 2.0F;
    public static final float END_EASING_POWER = 2.0F;
    public static final float START_BLEND_TICKS = 2.0F;

    // --- Swing ---
    public static final int SWING_DURATION_TICKS = 10;
    public static final float SWING_ROTATION_RADIANS = (float) (Math.PI * 2.0);

    // -1 = counterclockwise, 1 = clockwise.
    public static final float SWING_DIRECTION = 1.0F;

    /*
     * The hand is to the right of the player's center.
     * Negative X moves the swing pivot back toward the center.
     */
    public static final float SWING_PIVOT_X = -0.45F;
    public static final float SWING_PIVOT_Y = 0.0F;
    public static final float SWING_PIVOT_Z = 0.0F;

    // --- Swing orientation ---

    /*
     * The mace turns onto its side over the first 20% of the swing.
     *
     * -90° makes the Heavy Core point outward and the handle point
     * inward relative to the player.
     */
    public static final float SWING_TILT_DEGREES = -90.0F;
    public static final float SWING_TILT_DURATION = 0.20F;

    /*
     * The mace returns to its normal orientation during the final
     * 20% of the swing.
     */
    public static final float SWING_UNTILT_START = 0.80F;
    public static final float SWING_UNTILT_END = 1.00F;

    public static final float SWING_TILT_START_EASING = 2.0F;
    public static final float SWING_TILT_END_EASING = 2.0F;

    // --- Charge state ---
    private static boolean charging = false;

    // --- Swing state ---
    private static boolean swinging = false;
    private static float previousSwingTicks = 0.0F;
    private static float swingTicks = 0.0F;

    public static void startCharging() {
        charging = true;
    }

    public static void stopCharging() {
        charging = false;
    }

    public static boolean isCharging() {
        return charging;
    }

    public static void tick(ItemStack stack) {
        if (!(stack.getItem() instanceof MaceItem)) {
            charging = false;
        }
    }

    // --- Charge progress ---

    public static float getChargeProgress(
            AbstractClientPlayer player,
            ItemStack stack,
            float frameInterp
    ) {
        if (!player.isUsingItem() ||
                !(stack.getItem() instanceof MaceItem)) {
            return 0.0F;
        }

        float timeHeld = getTimeHeld(player, stack, frameInterp);

        float progress = Math.clamp(
                timeHeld / OPTIMAL_CHARGE_TICKS,
                0.0F,
                1.0F
        );

        if (START_BLEND_TICKS > 0.0F) {
            float blend = Math.clamp(
                    timeHeld / START_BLEND_TICKS,
                    0.0F,
                    1.0F
            );

            progress *= blend;
        }

        return applyStartEndEasing(progress);
    }

    private static float getTimeHeld(
            AbstractClientPlayer player,
            ItemStack stack,
            float frameInterp
    ) {
        return stack.getUseDuration(player)
                - (player.getUseItemRemainingTicks() - frameInterp + 1.0F);
    }

    // --- Charge easing ---

    private static float applyStartEndEasing(float progress) {
        if (progress <= 0.0F) return 0.0F;
        if (progress >= 1.0F) return 1.0F;

        if (progress < 0.5F) {
            float local = progress * 2.0F;

            return (float) Math.pow(
                    local,
                    START_EASING_POWER
            ) * 0.5F;
        }

        float local = (1.0F - progress) * 2.0F;

        return 1.0F
                - (float) Math.pow(
                local,
                END_EASING_POWER
        ) * 0.5F;
    }

    // --- Overcharge lowering ---

    public static float getOverchargeLowering(
            AbstractClientPlayer player,
            ItemStack stack,
            float frameInterp
    ) {
        if (!player.isUsingItem() ||
                !(stack.getItem() instanceof MaceItem)) {
            return 0.0F;
        }

        float timeHeld = getTimeHeld(player, stack, frameInterp);

        if (timeHeld <= OPTIMAL_CHARGE_TICKS) {
            return 0.0F;
        }

        float progress = Math.clamp(
                (timeHeld - OPTIMAL_CHARGE_TICKS)
                        / (OVERCHARGE_END_TICKS - OPTIMAL_CHARGE_TICKS),
                0.0F,
                1.0F
        );

        progress = easeBetweenEndpoints(
                progress,
                OVERCHARGE_START_EASING,
                OVERCHARGE_END_EASING
        );

        return OVERCHARGE_LOWER_AMOUNT * progress;
    }

    // --- Dip ---

    public static float getDip(float progress) {
        progress = Math.clamp(progress, 0.0F, 1.0F);

        if (DIP_PEAK <= 0.0F || DIP_PEAK >= 1.0F) {
            return 0.0F;
        }

        if (progress <= DIP_PEAK) {
            float local = progress / DIP_PEAK;
            float parabola =
                    1.0F - (local - 1.0F) * (local - 1.0F);

            return DIP_AMOUNT * parabola;
        }

        float local =
                (progress - DIP_PEAK)
                        / (1.0F - DIP_PEAK);

        float parabola =
                1.0F - local * local;

        return DIP_AMOUNT * parabola;
    }

    // --- Swing state ---

    public static void startSwing() {
        swinging = true;
        previousSwingTicks = 0.0F;
        swingTicks = 0.0F;
    }

    public static void tickSwing() {
        if (!swinging) return;

        previousSwingTicks = swingTicks;
        swingTicks++;

        if (swingTicks >= SWING_DURATION_TICKS) {
            swingTicks = SWING_DURATION_TICKS;
        }
    }

    public static boolean isSwinging() {
        return swinging;
    }

    public static float getSwingProgress(float frameInterp) {
        if (!swinging) return 0.0F;

        float ticks =
                previousSwingTicks
                        + (swingTicks - previousSwingTicks) * frameInterp;

        float progress = Math.clamp(
                ticks / SWING_DURATION_TICKS,
                0.0F,
                1.0F
        );

        return easeBetweenEndpoints(
                progress,
                2.0F,
                2.0F
        );
    }

    // --- Swing tilt ---

    public static float getSwingTilt(float swingProgress) {
        if (swingProgress <= 0.0F) return 0.0F;
        if (swingProgress >= 1.0F) return 0.0F;

        // Turn onto side during the first 20%.
        if (swingProgress < SWING_TILT_DURATION) {
            float progress =
                    swingProgress / SWING_TILT_DURATION;

            return SWING_TILT_DEGREES
                    * easeBetweenEndpoints(
                    progress,
                    SWING_TILT_START_EASING,
                    SWING_TILT_END_EASING
            );
        }

        // Stay horizontal through the middle.
        if (swingProgress < SWING_UNTILT_START) {
            return SWING_TILT_DEGREES;
        }

        // Return to normal during the final 20%.
        float progress =
                (swingProgress - SWING_UNTILT_START)
                        / (SWING_UNTILT_END - SWING_UNTILT_START);

        return SWING_TILT_DEGREES
                * (1.0F - easeBetweenEndpoints(
                progress,
                SWING_TILT_START_EASING,
                SWING_TILT_END_EASING
        ));
    }

    // --- Easing utility ---

    private static float easeBetweenEndpoints(
            float progress,
            float startPower,
            float endPower
    ) {
        progress = Math.clamp(progress, 0.0F, 1.0F);

        if (progress <= 0.0F) return 0.0F;
        if (progress >= 1.0F) return 1.0F;

        float startTerm =
                (float) Math.pow(progress, startPower);

        float endTerm =
                (float) Math.pow(
                        1.0F - progress,
                        endPower
                );

        return startTerm / (startTerm + endTerm);
    }
}