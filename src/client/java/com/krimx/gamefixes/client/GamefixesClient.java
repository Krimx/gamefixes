package com.krimx.gamefixes.client;

import com.krimx.gamefixes.GlowSquidLeggingsLighting;
import com.krimx.gamefixes.HoneycombBootsWallJump;
import com.krimx.gamefixes.network.HoneycombWallJumpPayload;
import com.krimx.gamefixes.network.MaceChargePayload;
import com.krimx.gamefixes.network.MaceSwingPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class GamefixesClient implements ClientModInitializer {

	private boolean wasUsingMace = false;

	private boolean honeycombJumpHeld = false;
	private boolean honeycombWallJumpUsed = false;
	private boolean honeycombWasAirborne = false;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
				MaceChargePayload.TYPE,
				(payload, context) ->
						context.client().execute(() ->
								MaceThirdPersonAnimation.startCharge(
										payload.playerId()
								)
						)
		);

		ClientPlayNetworking.registerGlobalReceiver(
				MaceSwingPayload.TYPE,
				(payload, context) ->
						context.client().execute(() ->
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