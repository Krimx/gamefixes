package com.krimx.gamefixes.client;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class ArmadilloChestplateModel extends EntityModel<HumanoidRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Gamefixes.MOD_ID,
                    "armadillo_chestplate"
            ),
            "main"
    );

    public ArmadilloChestplateModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -4.0F,
                                -0.01F,
                                -2.0F,
                                8.0F,
                                12.0F,
                                5.0F
                        ),
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        /*
         * The Fabric ArmorRenderer copies the vanilla humanoid
         * body transform onto this model, so no custom animation
         * is needed here.
         */
    }
}