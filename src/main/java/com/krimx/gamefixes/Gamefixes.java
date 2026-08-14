package com.krimx.gamefixes;

import com.krimx.gamefixes.network.MaceNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.minecraft.world.InteractionResult;


public class Gamefixes implements ModInitializer {
	public static final String MOD_ID = "gamefixes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Item REFINED_SULFUR;

	@Override
	public void onInitialize() {
		// 1. Define the Identifier
		Identifier id = Identifier.parse(MOD_ID + ":refined_sulfur");

		// 2. Create a ResourceKey for the item
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);

		// 3. Bind the ResourceKey to the item's properties BEFORE making the item
		Item.Properties properties = new Item.Properties().setId(key);

		// 4. Safely register the item
		REFINED_SULFUR = Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);

		// 5. Register fuels in 26.2 using FuelValueEvents
		FuelValueEvents.BUILD.register((builder, context) -> {
			// Magma Block: 1600 ticks (smelts 8 items in standard furnace)
			builder.add(Items.MAGMA_BLOCK, 1600);

			// Refined Sulfur: 1200 ticks (smelts 6 items in standard furnace)
			builder.add(REFINED_SULFUR, 1200);
		});

		ItemEvents.USE.register((level, player, hand) -> {
			ItemStack stack = player.getItemInHand(hand);

			if (!(stack.getItem() instanceof MaceItem)) {
				return InteractionResult.PASS;
			}

			player.startUsingItem(hand);
			return InteractionResult.CONSUME;
		});

		MaceNetworking.registerCommon();

		LOGGER.info("GameFixes Mod initialized successfully!");
	}
}