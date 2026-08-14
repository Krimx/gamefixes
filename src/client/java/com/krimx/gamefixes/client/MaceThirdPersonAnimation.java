package com.krimx.gamefixes.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MaceThirdPersonAnimation {

    // --- Timing ---
    public static final float OPTIMAL_CHARGE_TICKS = 20.0F;
    public static final float OVERCHARGE_END_TICKS = 60.0F;

    public static final float TOTAL_SWING_TICKS = 12.0F;
    public static final float WINDUP_END = 0.20F;
    public static final float ORBIT_END = 0.80F;

    // --- Charge pose ---
    public static final float CHARGE_ARM_X = -1.15F;
    public static final float CHARGE_ARM_Y = 0.55F;
    public static final float CHARGE_ARM_Z = 0.35F;

    // --- Overcharge pose ---
    public static final float OVERCHARGE_ARM_X = 0.35F;
    public static final float OVERCHARGE_ARM_Y = -0.15F;
    public static final float OVERCHARGE_ARM_Z = -0.10F;

    // --- Charge easing ---
    public static final float CHARGE_START_EASING = 2.0F;
    public static final float CHARGE_END_EASING = 2.0F;

    public static final float OVERCHARGE_START_EASING = 2.0F;
    public static final float OVERCHARGE_END_EASING = 2.0F;

    // --- Swing ---
    public static final float ORBIT_RADIUS = 0.75F;
    public static final float ORBIT_HEIGHT = 8.0F;
    public static final float ORBIT_ROTATION = (float) (Math.PI * 2.0F);

    public static final float WINDUP_START_EASING = 2.0F;
    public static final float WINDUP_END_EASING = 2.0F;
    public static final float ORBIT_START_EASING = 2.0F;
    public static final float ORBIT_END_EASING = 2.0F;
    public static final float RETURN_START_EASING = 2.0F;
    public static final float RETURN_END_EASING = 2.0F;

    private static final Map<UUID, ChargeState> CHARGES = new HashMap<>();
    private static final Map<UUID, SwingState> SWINGS = new HashMap<>();

    // --- Charge state ---

    public static void startCharge(UUID playerId) {
        CHARGES.put(playerId, new ChargeState());
    }

    public static void stopCharge(UUID playerId) {
        CHARGES.remove(playerId);
    }

    public static float getChargeProgress(UUID playerId, float frameInterp) {
        ChargeState state = CHARGES.get(playerId);
        if (state == null) return -1.0F;

        float ticks = state.previousTicks
                + (state.ticks - state.previousTicks) * frameInterp;

        return ease(
                Math.clamp(
                        ticks / OPTIMAL_CHARGE_TICKS,
                        0.0F,
                        1.0F
                ),
                CHARGE_START_EASING,
                CHARGE_END_EASING
        );
    }

    public static float getOverchargeProgress(
            UUID playerId,
            float frameInterp
    ) {
        ChargeState state = CHARGES.get(playerId);
        if (state == null) return -1.0F;

        float ticks = state.previousTicks
                + (state.ticks - state.previousTicks) * frameInterp;

        if (ticks <= OPTIMAL_CHARGE_TICKS) return 0.0F;

        float progress = Math.clamp(
                (ticks - OPTIMAL_CHARGE_TICKS)
                        / (OVERCHARGE_END_TICKS - OPTIMAL_CHARGE_TICKS),
                0.0F,
                1.0F
        );

        return ease(
                progress,
                OVERCHARGE_START_EASING,
                OVERCHARGE_END_EASING
        );
    }

    public static void tick() {
        for (ChargeState state : CHARGES.values()) {
            state.previousTicks = state.ticks;
            state.ticks++;
        }

        for (SwingState state : SWINGS.values()) {
            state.previousTicks = state.ticks;
            state.ticks = Math.min(
                    state.ticks + 1.0F,
                    TOTAL_SWING_TICKS
            );
        }
    }

    // --- Swing state ---

    public static void startSwing(UUID playerId) {
        CHARGES.remove(playerId);
        SWINGS.put(playerId, new SwingState());
    }

    public static float getSwingProgress(
            UUID playerId,
            float frameInterp
    ) {
        SwingState state = SWINGS.get(playerId);
        if (state == null) return -1.0F;

        float ticks = state.previousTicks
                + (state.ticks - state.previousTicks) * frameInterp;

        float progress = Math.clamp(
                ticks / TOTAL_SWING_TICKS,
                0.0F,
                1.0F
        );

        if (ticks >= TOTAL_SWING_TICKS) {
            SWINGS.remove(playerId);
        }

        return progress;
    }

    public static float getWindupProgress(float progress) {
        if (progress <= 0.0F) return 0.0F;
        if (progress >= WINDUP_END) return 1.0F;

        return ease(
                progress / WINDUP_END,
                WINDUP_START_EASING,
                WINDUP_END_EASING
        );
    }

    public static float getOrbitProgress(float progress) {
        if (progress <= WINDUP_END) return 0.0F;
        if (progress >= ORBIT_END) return 1.0F;

        return ease(
                (progress - WINDUP_END) / (ORBIT_END - WINDUP_END),
                ORBIT_START_EASING,
                ORBIT_END_EASING
        );
    }

    public static float getReturnProgress(float progress) {
        if (progress <= ORBIT_END) return 0.0F;
        if (progress >= 1.0F) return 1.0F;

        return ease(
                (progress - ORBIT_END) / (1.0F - ORBIT_END),
                RETURN_START_EASING,
                RETURN_END_EASING
        );
    }

    public static boolean isWindingUp(float progress) {
        return progress < WINDUP_END;
    }

    public static boolean isOrbiting(float progress) {
        return progress >= WINDUP_END && progress < ORBIT_END;
    }

    public static boolean isReturning(float progress) {
        return progress >= ORBIT_END;
    }

    // --- Utility ---

    private static float ease(
            float progress,
            float startPower,
            float endPower
    ) {
        progress = Math.clamp(progress, 0.0F, 1.0F);

        if (progress <= 0.0F) return 0.0F;
        if (progress >= 1.0F) return 1.0F;

        float start = (float) Math.pow(progress, startPower);
        float end = (float) Math.pow(1.0F - progress, endPower);

        return start / (start + end);
    }

    private static class ChargeState {
        private float previousTicks;
        private float ticks;
    }

    private static class SwingState {
        private float previousTicks;
        private float ticks;
    }
}