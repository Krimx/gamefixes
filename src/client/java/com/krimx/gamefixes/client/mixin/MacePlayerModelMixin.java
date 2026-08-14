package com.krimx.gamefixes.client.mixin;

import com.krimx.gamefixes.client.MaceThirdPersonAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerModel.class)
public class MacePlayerModelMixin {

    // --- Charge pose ---
    private static final float CHARGE_X = -1.15F;
    private static final float CHARGE_Y = 0.55F;
    private static final float CHARGE_Z = 0.35F;

    // --- Overcharge pose ---
    private static final float OVERCHARGE_X = 0.35F;
    private static final float OVERCHARGE_Y = -0.15F;
    private static final float OVERCHARGE_Z = -0.10F;

    // --- Swing wind-up ---
    private static final float SWING_WINDUP_X = -1.0F;
    private static final float SWING_WINDUP_Y = -0.4F;
    private static final float SWING_WINDUP_Z = 0.25F;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void gamefixes$maceThirdPersonAnimation(
            AvatarRenderState state,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        var entity = minecraft.level.getEntity(state.id);
        if (entity == null) return;

        float frameInterp = minecraft.getDeltaTracker()
                .getGameTimeDeltaPartialTick(true);

        UUID playerId = entity.getUUID();

        float charge =
                MaceThirdPersonAnimation.getChargeProgress(
                        playerId,
                        frameInterp
                );

        if (charge >= 0.0F) {
            animateCharge(
                    (PlayerModel) (Object) this,
                    playerId,
                    frameInterp,
                    charge
            );
            return;
        }

        float swing =
                MaceThirdPersonAnimation.getSwingProgress(
                        playerId,
                        frameInterp
                );

        if (swing < 0.0F) return;

        animateSwing(
                (PlayerModel) (Object) this,
                swing
        );
    }

    // --- Charge ---

    private static void animateCharge(
            PlayerModel model,
            UUID playerId,
            float frameInterp,
            float charge
    ) {
        float overcharge =
                MaceThirdPersonAnimation.getOverchargeProgress(
                        playerId,
                        frameInterp
                );

        model.rightArm.xRot += CHARGE_X * charge;
        model.rightArm.yRot += CHARGE_Y * charge;
        model.rightArm.zRot += CHARGE_Z * charge;

        if (overcharge > 0.0F) {
            model.rightArm.xRot +=
                    OVERCHARGE_X * overcharge;
            model.rightArm.yRot +=
                    OVERCHARGE_Y * overcharge;
            model.rightArm.zRot +=
                    OVERCHARGE_Z * overcharge;
        }
    }

    // --- Swing ---

    private static void animateSwing(
            PlayerModel model,
            float progress
    ) {
        if (MaceThirdPersonAnimation.isWindingUp(progress)) {
            float p =
                    MaceThirdPersonAnimation.getWindupProgress(progress);

            /*
             * Blend directly from the final charge pose into the
             * normal swing wind-up instead of returning to neutral.
             */
            float chargeWeight = 1.0F - p;

            model.rightArm.xRot +=
                    CHARGE_X * chargeWeight
                            + SWING_WINDUP_X * p;

            model.rightArm.yRot +=
                    CHARGE_Y * chargeWeight
                            + SWING_WINDUP_Y * p;

            model.rightArm.zRot +=
                    CHARGE_Z * chargeWeight
                            + SWING_WINDUP_Z * p;

            return;
        }

        if (MaceThirdPersonAnimation.isOrbiting(progress)) {
            float p =
                    MaceThirdPersonAnimation.getOrbitProgress(progress);

            model.rightArm.xRot +=
                    (float) Math.sin(p * Math.PI * 2.0F) * 0.8F;

            model.rightArm.yRot +=
                    ((float) Math.cos(p * Math.PI * 2.0F) - 1.0F)
                            * -1.0F;

            model.rightArm.zRot +=
                    (float) Math.sin(p * Math.PI * 2.0F) * 0.35F;

            return;
        }

        float p =
                MaceThirdPersonAnimation.getReturnProgress(progress);

        model.rightArm.xRot +=
                0.8F * (1.0F - p);

        model.rightArm.yRot += p;

        model.rightArm.zRot +=
                0.35F * (1.0F - p);
    }
}