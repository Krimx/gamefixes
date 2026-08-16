package com.krimx.gamefixes;

import com.krimx.gamefixes.network.MaceNetworking;
import com.krimx.gamefixes.network.ResearchNetworking;
import com.krimx.gamefixes.research.ResearchAttachments;
import com.krimx.gamefixes.research.ResearchRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gamefixes implements ModInitializer {

	public static final String MOD_ID = "gamefixes";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	public static Item REFINED_SULFUR;

	@Override
	public void onInitialize() {

		Identifier id =
				Identifier.parse(
						MOD_ID + ":refined_sulfur"
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		Item.Properties properties =
				new Item.Properties()
						.setId(key);

		REFINED_SULFUR =
				Registry.register(
						BuiltInRegistries.ITEM,
						id,
						new Item(properties)
				);

		FuelValueEvents.BUILD.register(
				(builder, context) -> {

					builder.add(
							Items.MAGMA_BLOCK,
							1600
					);

					builder.add(
							REFINED_SULFUR,
							1200
					);
				}
		);

		ItemEvents.USE.register(
				(level, player, hand) -> {

					ItemStack stack =
							player.getItemInHand(hand);

					if (!(stack.getItem()
							instanceof MaceItem)) {
						return InteractionResult.PASS;
					}

					player.startUsingItem(hand);

					return InteractionResult.CONSUME;
				}
		);

		ResearchAttachments.initialize();
		ResearchRegistry.initialize();
		ResearchNetworking.registerCommon();
		MaceNetworking.registerCommon();

		LOGGER.info(
				"GameFixes Mod initialized successfully!"
		);
	}
}