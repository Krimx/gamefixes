package com.krimx.gamefixes.client.renderer;

import com.krimx.gamefixes.Gamefixes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.EndCubeSpecialRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TwilightPrismarineItemRenderer
        implements NoDataSpecialModelRenderer {

    private final EndCubeSpecialRenderer endCubeRenderer;
    private final BlockState prismarineState;

    public TwilightPrismarineItemRenderer() {
        this.endCubeRenderer = new EndCubeSpecialRenderer(
                RenderTypes.endPortal()
        );

        this.prismarineState =
                Gamefixes.TWILIGHT_PRISMARINE.defaultBlockState();
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        /*
         * Draw the End Portal first.
         */
        endCubeRenderer.submit(
                poseStack,
                submitNodeCollector,
                lightCoords,
                overlayCoords,
                hasFoil,
                outlineColor
        );

        /*
         * Resolve the Twilight Prismarine block model at render time.
         */
        BlockStateModel prismarineModel = Minecraft.getInstance()
                .getModelManager()
                .getBlockStateModelSet()
                .get(prismarineState);

        List<BlockStateModelPart> parts = new ArrayList<>();

        prismarineModel.collectParts(
                RandomSource.create(0L),
                parts
        );

        /*
         * Draw the transparent Twilight Prismarine texture after
         * the portal. The transparent pixels reveal the portal below,
         * while the opaque pixels form the prismarine shards.
         */
        submitNodeCollector.order(1).submitBlockModel(
                poseStack,
                Sheets.cutoutBlockItemSheet(),
                parts,
                new int[0],
                lightCoords,
                overlayCoords,
                outlineColor
        );
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0.0F, 0.0F, 0.0F));
        output.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public static final class Unbaked
            implements NoDataSpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC =
                MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TwilightPrismarineItemRenderer bake(
                SpecialModelRenderer.BakingContext context
        ) {
            return new TwilightPrismarineItemRenderer();
        }
    }
}