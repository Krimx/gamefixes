package com.krimx.gamefixes;

import com.krimx.gamefixes.network.MaceNetworking;
import com.krimx.gamefixes.network.ResearchNetworking;
import com.krimx.gamefixes.research.ResearchAttachments;
import com.krimx.gamefixes.research.ResearchRegistry;
import com.krimx.gamefixes.loot.EnchantWithLevelsMendingFunction;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
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
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gamefixes implements ModInitializer {

	public static final String MOD_ID = "gamefixes";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	public static Item REFINED_SULFUR;

	private static final ThreadLocal<Boolean> ALLOW_MENDING =
			ThreadLocal.withInitial(() -> false);

	public static void setMendingAllowed(boolean allowed) {
		ALLOW_MENDING.set(allowed);
	}

	public static boolean isMendingAllowed() {
		return ALLOW_MENDING.get();
	}

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

		UseItemCallback.EVENT.register(
				(player, level, hand) -> {

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
		ConcreteConversion.initialize();

		Registry.register(
				BuiltInRegistries.LOOT_FUNCTION_TYPE,
				Identifier.fromNamespaceAndPath(
						"gamefixes",
						"enchant_with_levels_mending"
				),
				EnchantWithLevelsMendingFunction.MAP_CODEC
		);

		LOGGER.info(
				"GameFixes Mod initialized successfully!"
		);
	}
}