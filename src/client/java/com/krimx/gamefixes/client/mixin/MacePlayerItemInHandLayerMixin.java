package com.krimx.gamefixes.client.mixin;

import com.krimx.gamefixes.client.MaceThirdPersonAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public class MacePlayerItemInHandLayerMixin {

    // --- Orbit ---
    private static final float ORBIT_RADIUS = 0.75F;
    private static final float ORBIT_HEIGHT = 0.3F;
    private static final float ORBIT_CENTER_X = 0.4F;
    private static final float ORBIT_CENTER_Y = 0.0F;
    private static final float ORBIT_CENTER_Z = 0.0F;
    private static final float ORBIT_DIRECTION = -1.0F;

    // --- Mace orientation ---
    private static final float MACE_SIDE_ROTATION = -90.0F;
    private static final float MACE_OUTWARD_ROTATION = 70.0F;

    @Inject(
            method = "submitArmWithItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$animateDetachedMace(
            AvatarRenderState state,
            ItemStackRenderState itemState,
            ItemStack stack,
            HumanoidArm arm,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof MaceItem)) return;
        if (arm != HumanoidArm.RIGHT) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        var entity = minecraft.level.getEntity(state.id);
        if (entity == null) return;

        float frameInterp = minecraft.getDeltaTracker()
                .getGameTimeDeltaPartialTick(true);

        float progress = MaceThirdPersonAnimation.getSwingProgress(
                entity.getUUID(),
                frameInterp
        );

        if (progress < 0.0F ||
                !MaceThirdPersonAnimation.isOrbiting(progress)) {
            return;
        }

        ci.cancel();

        float orbitProgress =
                MaceThirdPersonAnimation.getOrbitProgress(progress);

        float angle =
                MaceThirdPersonAnimation.ORBIT_ROTATION
                        * orbitProgress
                        * ORBIT_DIRECTION;

        poseStack.pushPose();

        // Move the rotation center from the hand to the torso.
        poseStack.translate(
                ORBIT_CENTER_X,
                ORBIT_CENTER_Y,
                ORBIT_CENTER_Z
        );

        // Orbit around the player's vertical axis.
        poseStack.mulPose(Axis.YP.rotation(angle));

        // Move outward from the player.
        poseStack.translate(
                0.0F,
                ORBIT_HEIGHT,
                ORBIT_RADIUS
        );

        // Lay the mace horizontally.
        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        MACE_SIDE_ROTATION
                )
        );

        // Rotate around the remaining model axis to point
        // the heavy head outward.
        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        MACE_OUTWARD_ROTATION
                )
        );

        itemState.submit(
                poseStack,
                collector,
                light,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }
}