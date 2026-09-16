package com.krimx.gamefixes.client.renderer;

import com.krimx.gamefixes.block.entity.TwilightPrismarineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class TwilightPrismarineBlockEntityRenderer
        extends AbstractEndPortalRenderer<
        TwilightPrismarineBlockEntity,
        EndPortalRenderState
        > {

    public TwilightPrismarineBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
    }

    @Override
    public EndPortalRenderState createRenderState() {
        return new EndPortalRenderState();
    }

    @Override
    public void submit(
            EndPortalRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();

        // Move the portal cube microscopically inside the prismarine shell.
        // This prevents z-fighting while keeping the portal effectively
        // flush with the six block faces.
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        submitCube(
                state.facesToShow,
                RenderTypes.endPortal(),
                poseStack,
                submitNodeCollector
        );

        poseStack.popPose();
    }
}