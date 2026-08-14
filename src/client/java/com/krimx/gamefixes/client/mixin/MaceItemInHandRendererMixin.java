package com.krimx.gamefixes.client.mixin;

import com.krimx.gamefixes.client.MaceChargeAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MaceItemInHandRendererMixin {

    // --- Prevent vanilla equip pop ---

    @Inject(
            method = "itemUsed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$preventMaceEquipReset(
            InteractionHand hand,
            CallbackInfo ci
    ) {
        if (hand != InteractionHand.MAIN_HAND) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        ItemStack stack = minecraft.player.getMainHandItem();

        if (stack.getItem() instanceof MaceItem) {
            ci.cancel();
        }
    }

    // --- Mace animation ---

    @Inject(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
                    ordinal = 1
            )
    )
    private void gamefixes$animateMace(
            AbstractClientPlayer player,
            float frameInterp,
            float xRot,
            InteractionHand hand,
            float attack,
            ItemStack itemStack,
            float inverseArmHeight,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            CallbackInfo ci
    ) {
        if (hand != InteractionHand.MAIN_HAND) return;
        if (!(itemStack.getItem() instanceof MaceItem)) return;

        if (player.isUsingItem()) {
            animateCharge(player, itemStack, frameInterp, poseStack);
        } else if (MaceChargeAnimation.isSwinging()) {
            animateSwing(frameInterp, poseStack);
        }
    }

    // --- Charge ---

    private static void animateCharge(
            AbstractClientPlayer player,
            ItemStack itemStack,
            float frameInterp,
            PoseStack poseStack
    ) {
        float progress = MaceChargeAnimation.getChargeProgress(
                player,
                itemStack,
                frameInterp
        );

        poseStack.translate(
                MaceChargeAnimation.PULLBACK_X * progress,
                MaceChargeAnimation.PULLBACK_Y * progress,
                MaceChargeAnimation.PULLBACK_Z * progress
        );

        poseStack.translate(
                0.0F,
                MaceChargeAnimation.getDip(progress),
                0.0F
        );

        poseStack.translate(
                0.0F,
                MaceChargeAnimation.getOverchargeLowering(
                        player,
                        itemStack,
                        frameInterp
                ),
                0.0F
        );

        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        MaceChargeAnimation.ROTATION_X * progress
                )
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        MaceChargeAnimation.ROTATION_Y * progress
                )
        );

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        MaceChargeAnimation.ROTATION_Z * progress
                )
        );
    }

    // --- Swing ---

    private static void animateSwing(
            float frameInterp,
            PoseStack poseStack
    ) {
        float progress =
                MaceChargeAnimation.getSwingProgress(frameInterp);

        /*
         * Move the swing pivot from the hand to the player's center.
         * The mace's actual position is offset from that center,
         * so rotating around this point makes it orbit the player.
         */
        poseStack.translate(
                MaceChargeAnimation.SWING_PIVOT_X,
                MaceChargeAnimation.SWING_PIVOT_Y,
                MaceChargeAnimation.SWING_PIVOT_Z
        );

        // Counterclockwise horizontal sweep.
        float angle =
                MaceChargeAnimation.SWING_ROTATION_RADIANS
                        * progress
                        * MaceChargeAnimation.SWING_DIRECTION;

        poseStack.mulPose(
                Axis.YP.rotation(angle)
        );

        poseStack.translate(
                -MaceChargeAnimation.SWING_PIVOT_X,
                -MaceChargeAnimation.SWING_PIVOT_Y,
                -MaceChargeAnimation.SWING_PIVOT_Z
        );

        /*
         * Turn the mace onto its side, then return it to the
         * normal holding orientation by the end of the swing.
         */
        float tilt =
                MaceChargeAnimation.getSwingTilt(progress);

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(tilt)
        );
    }
}