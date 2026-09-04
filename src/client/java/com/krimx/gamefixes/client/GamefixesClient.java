package com.krimx.gamefixes.client;

import com.krimx.gamefixes.network.MaceChargePayload;
import com.krimx.gamefixes.network.MaceSwingPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class GamefixesClient implements ClientModInitializer {

	private boolean wasUsingMace = false;

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
				return;
			}

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
