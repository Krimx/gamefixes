package com.krimx.gamefixes.client;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.GlowSquidLeggingsLighting;
import com.krimx.gamefixes.HoneycombBootsWallJump;
import com.krimx.gamefixes.block.CattailBlock;
import com.krimx.gamefixes.client.particle.MosquitoParticle;
import com.krimx.gamefixes.client.renderer.TwilightPrismarineBlockEntityRenderer;
import com.krimx.gamefixes.client.renderer.TwilightPrismarineItemRenderer;
import com.krimx.gamefixes.network.HoneycombWallJumpPayload;
import com.krimx.gamefixes.network.MaceChargePayload;
import com.krimx.gamefixes.network.MaceSwingPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class GamefixesClient implements ClientModInitializer {

	private static final Identifier ARMADILLO_CHESTPLATE_TEXTURE =
			Identifier.fromNamespaceAndPath(
					Gamefixes.MOD_ID,
					"textures/entity/equipment/armadillo_chestplate.png"
			);

	private boolean wasUsingMace = false;

	private boolean honeycombJumpHeld = false;
	private boolean honeycombWallJumpUsed = false;
	private boolean honeycombWasAirborne = false;

	@Override
	public void onInitializeClient() {
		SpecialModelRenderers.ID_MAPPER.put(
				Identifier.fromNamespaceAndPath(
						Gamefixes.MOD_ID,
						"twilight_prismarine"
				),
				TwilightPrismarineItemRenderer.Unbaked.MAP_CODEC
		);

		BlockEntityRenderers.register(
				Gamefixes.TWILIGHT_PRISMARINE_BLOCK_ENTITY,
				TwilightPrismarineBlockEntityRenderer::new
		);

		ModelLayerRegistry.registerModelLayer(
				ArmadilloChestplateModel.LAYER,
				ArmadilloChestplateModel::createLayer
		);

		ArmorRenderer.register(
				context -> {
					ArmadilloChestplateModel model =
							new ArmadilloChestplateModel(
									context.bakeLayer(
											ArmadilloChestplateModel.LAYER
									)
							);

					return (
							poseStack,
							submitNodeCollector,
							stack,
							humanoidRenderState,
							slot,
							light,
							contextModel
					) -> {
						if (slot != EquipmentSlot.CHEST) {
							return;
						}

						ArmorRenderer.submitTransformCopyingModel(
								contextModel,
								humanoidRenderState,
								model,
								humanoidRenderState,
								true,
								(OrderedSubmitNodeCollector) submitNodeCollector,
								poseStack,
								RenderTypes.armorCutoutNoCull(
										ARMADILLO_CHESTPLATE_TEXTURE
								),
								light,
								OverlayTexture.NO_OVERLAY,
								0
						);
					};
				},
				Gamefixes.ARMADILLO_CHESTPLATE
		);

		ParticleProviderRegistry.getInstance().register(
				Gamefixes.MOSQUITO_PARTICLE,
				MosquitoParticle.Provider::new
		);

		ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> {
			CattailBlock.clearMosquitoSourcesInChunk(
					chunk.getPos().getBlockX(0),
					chunk.getPos().getBlockZ(0)
			);
		});

		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register(
				(client, level) -> {
					CattailBlock.clearMosquitoSources();
				}
		);

		ClientPlayNetworking.registerGlobalReceiver(
				MaceChargePayload.TYPE,
				(payload, context) ->
						context.client().execute(
								() ->
										MaceThirdPersonAnimation.startCharge(
												payload.playerId()
										)
						)
		);

		ClientPlayNetworking.registerGlobalReceiver(
				MaceSwingPayload.TYPE,
				(payload, context) ->
						context.client().execute(
								() ->
										MaceThirdPersonAnimation.startSwing(
												payload.playerId()
										)
						)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				wasUsingMace = false;
				honeycombJumpHeld = false;
				honeycombWallJumpUsed = false;
				honeycombWasAirborne = false;
				return;
			}

			GlowSquidLeggingsLighting.tick(client.player);

			// --- Honeycomb Boots wall jump ---
			boolean jumpPressed =
					client.options.keyJump.isDown();

			boolean onGround =
					client.player.onGround();

			if (onGround) {
				honeycombWallJumpUsed = false;
				honeycombWasAirborne = false;
			}

			boolean newJumpPress =
					jumpPressed && !honeycombJumpHeld;

			/*
			 * The player must have already spent at least one
			 * tick airborne before a wall jump can occur.
			 *
			 * This prevents the initial jump from being counted
			 * as the wall jump when the player is already touching
			 * a wall.
			 */
			if (newJumpPress
					&& honeycombWasAirborne
					&& !honeycombWallJumpUsed
					&& HoneycombBootsWallJump.canWallJump(
					client.player
			)) {

				/*
				 * Apply the jump locally immediately so the ability feels
				 * responsive. The server performs the same validation and
				 * applies the authoritative velocity.
				 */
				HoneycombBootsWallJump.performWallJump(client.player);

				ClientPlayNetworking.send(
						new HoneycombWallJumpPayload()
				);

				honeycombWallJumpUsed = true;
			}

			if (!onGround) {
				honeycombWasAirborne = true;
			}

			honeycombJumpHeld = jumpPressed;

			ItemStack stack =
					client.player.getMainHandItem();

			boolean usingMace =
					stack.getItem() instanceof MaceItem
							&& client.player.isUsingItem();

			if (usingMace) {
				if (!MaceChargeAnimation.isCharging()) {
					MaceChargeAnimation.startCharging();
				}

				MaceChargeAnimation.tick(stack);
			}

			if (wasUsingMace && !usingMace) {
				MaceChargeAnimation.stopCharging();
				MaceChargeAnimation.startSwing();
			}

			MaceChargeAnimation.tickSwing();
			MaceThirdPersonAnimation.tick();

			wasUsingMace = usingMace;

			if (!usingMace) {
				MaceChargeAnimation.stopCharging();
			}
		});
	}
}