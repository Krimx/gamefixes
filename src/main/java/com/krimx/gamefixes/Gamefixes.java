/*
TODO:
Add specific item inputs to villager research trades
Finalize the villager research trade pool
Finish the villager research UI
Add any additional intended golden crops
Overhaul Trial Chamber loot
Add Mending-containing End City loot
Give Ghast Resin a gameplay purpose
Rename and reconcile the Reduced/Condensed Ghast Tear implementation
Add diakrete armor floating in water
Add Honeycomb Boots
Implement the Honeycomb Boots wall-jump mechanic
Add armadillo chestplate item
 */

package com.krimx.gamefixes;

import com.krimx.gamefixes.network.MaceNetworking;
import com.krimx.gamefixes.network.ResearchNetworking;
import com.krimx.gamefixes.research.ResearchAttachments;
import com.krimx.gamefixes.research.ResearchRegistry;
import com.krimx.gamefixes.loot.EnchantWithLevelsMendingFunction;
import net.minecraft.util.Unit;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class Gamefixes implements ModInitializer {

	public static final String MOD_ID = "gamefixes";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	public static Item REFINED_SULFUR;
	public static Item GOLDEN_POTATO;
	public static Item DIAKRETE;
	public static Item DIAKRETE_HELMET;
	public static Item DIAKRETE_CHESTPLATE;
	public static Item DIAKRETE_LEGGINGS;
	public static Item DIAKRETE_BOOTS;
	public static Item FRAMED_ELYTRA_TRIM;
	public static Item GHAST_RESIN;
	public static Item TIDE_SHELL;
	public static Item WINGWEAVE;
	public static Item WINGWOVEN_ELYTRA;
	public static Item GILDED_ELYTRA;
	public static Item GILDED_WINGWOVEN_ELYTRA;
	public static Block TWILIGHT_PRISMARINE;
	public static Item ELYTRA_CHESTPLATE;
	public static Item HONEYCOMB_BOOTS;

	private static final ResourceKey<EquipmentAsset> WINGWOVEN_ELYTRA_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "wingwoven_elytra")
			);

	private static final ResourceKey<EquipmentAsset> GILDED_ELYTRA_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "gilded_elytra")
			);

	private static final ResourceKey<EquipmentAsset> GILDED_WINGWOVEN_ELYTRA_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "gilded_wingwoven_elytra")
			);
	private static final ResourceKey<EquipmentAsset> ELYTRA_CHESTPLATE_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "elytra_chestplate")
			);
	private static final ResourceKey<EquipmentAsset> HONEYCOMB_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "honeycomb")
			);

	private static final Identifier WINGWOVEN_SPEED_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "wingwoven_elytra_speed");
	private static final Identifier GILDED_ELYTRA_ARMOR_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "gilded_elytra_armor");
	private static final Identifier ELYTRA_CHESTPLATE_SPEED_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "elytra_chestplate_speed");
	private static final Identifier ELYTRA_CHESTPLATE_ARMOR_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "elytra_chestplate_armor");

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

		REFINED_SULFUR = registerItem("refined_sulfur", new Item.Properties());
		FRAMED_ELYTRA_TRIM = registerItem("framed_elytra_trim", new Item.Properties());
		GHAST_RESIN = registerItem("ghast_resin", new Item.Properties());
		TIDE_SHELL = registerItem("tide_shell", new Item.Properties());
		WINGWEAVE = registerItem("wingweave", new Item.Properties());

		DIAKRETE = registerItem(
				"diakrete", new Item.Properties().delayedHolderComponent(
								DataComponents.PROVIDES_TRIM_MATERIAL,
								ResourceKey.create(
										Registries.TRIM_MATERIAL,
										Identifier.fromNamespaceAndPath(MOD_ID, "diakrete")
								)
						)
		);

		DIAKRETE_HELMET = registerArmorItem(
				"diakrete_helmet",
				ArmorType.HELMET
		);
		DIAKRETE_CHESTPLATE = registerArmorItem(
				"diakrete_chestplate",
				ArmorType.CHESTPLATE
		);
		DIAKRETE_LEGGINGS = registerArmorItem(
				"diakrete_leggings",
				ArmorType.LEGGINGS
		);
		DIAKRETE_BOOTS = registerArmorItem(
				"diakrete_boots",
				ArmorType.BOOTS
		);

		HONEYCOMB_BOOTS = registerHoneycombBoots();

		WINGWOVEN_ELYTRA = registerItem(
				"wingwoven_elytra",
				createGliderProperties(
						432,
						WINGWOVEN_ELYTRA_ASSET,
						true,
						0.0
				)
		);

		GILDED_ELYTRA = registerItem(
				"gilded_elytra",
				createGliderProperties(
						864,
						GILDED_ELYTRA_ASSET,
						false,
						2.0
				)
		);

		GILDED_WINGWOVEN_ELYTRA = registerItem(
				"gilded_wingwoven_elytra",
				createGliderProperties(
						864,
						GILDED_WINGWOVEN_ELYTRA_ASSET,
						true,
						2.0
				)
		);
		ELYTRA_CHESTPLATE = registerElytraChestplate();

		GOLDEN_POTATO = registerItem("golden_potato", new Item.Properties()
				.food(
						new FoodProperties.Builder()
								.nutrition(6)
								.saturationModifier(9.6F)
								.build()
				));

		TWILIGHT_PRISMARINE = registerBlock(
				"twilight_prismarine",
				BlockBehaviour.Properties.of()
						.strength(2.0F)
						.sound(SoundType.STONE)
		);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(GOLDEN_POTATO));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(REFINED_SULFUR));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(DIAKRETE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_HELMET));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(FRAMED_ELYTRA_TRIM));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(GHAST_RESIN));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(TIDE_SHELL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(WINGWEAVE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(WINGWOVEN_ELYTRA));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(GILDED_ELYTRA));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(GILDED_WINGWOVEN_ELYTRA));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ELYTRA_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(HONEYCOMB_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
				.register(output -> output.accept(TWILIGHT_PRISMARINE.asItem()));

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

	private static Item registerElytraChestplate() {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":elytra_chestplate"
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		Item.Properties properties =
				new Item.Properties()
						.humanoidArmor(
								DiakreteArmorMaterial.INSTANCE,
								ArmorType.CHESTPLATE
						)
						.durability(864)
						.repairable(DIAKRETE)
						.component(
								DataComponents.GLIDER,
								Unit.INSTANCE
						)
						.component(
								DataComponents.EQUIPPABLE,
								Equippable.builder(EquipmentSlot.CHEST)
										.setAsset(ELYTRA_CHESTPLATE_ASSET)
										.setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
										.build()
						)
						.attributes(
								ItemAttributeModifiers.builder()
										.add(
												Attributes.ARMOR,
												new AttributeModifier(
														ELYTRA_CHESTPLATE_ARMOR_MODIFIER_ID,
														8.0,
														AttributeModifier.Operation.ADD_VALUE
												),
												EquipmentSlotGroup.CHEST
										)
										.build()
						)
						.setId(key);

		return Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);
	}

	private static Item.Properties createGliderProperties(
			int durability,
			ResourceKey<EquipmentAsset> asset,
			boolean speedBoost,
			double armorValue
	) {
		Item.Properties properties = new Item.Properties()
				.durability(durability)
				.repairable(Items.PHANTOM_MEMBRANE)
				.component(DataComponents.GLIDER, Unit.INSTANCE)
				.component(
						DataComponents.EQUIPPABLE,
						Equippable.builder(EquipmentSlot.CHEST)
								.setAsset(asset)
								.setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
								.build()
				);

		ItemAttributeModifiers.Builder attributes =
				ItemAttributeModifiers.builder();

		if (speedBoost) {
			attributes.add(
					Attributes.MOVEMENT_SPEED,
					new AttributeModifier(
							WINGWOVEN_SPEED_MODIFIER_ID,
							0.10,
							AttributeModifier.Operation.ADD_MULTIPLIED_BASE
					),
					EquipmentSlotGroup.CHEST
			);
		}

		if (armorValue > 0) {
			attributes.add(
					Attributes.ARMOR,
					new AttributeModifier(
							GILDED_ELYTRA_ARMOR_MODIFIER_ID,
							armorValue,
							AttributeModifier.Operation.ADD_VALUE
					),
					EquipmentSlotGroup.CHEST
			);
		}

		if (speedBoost || armorValue > 0) {
			properties = properties.attributes(attributes.build());
		}

		return properties;
	}

	private static Item registerArmorItem(
			String name,
			ArmorType type
	) {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":" + name
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		Item.Properties properties =
				new Item.Properties()
						.humanoidArmor(
								DiakreteArmorMaterial.INSTANCE,
								type
						)
						.durability(
								type.getDurability(
										DiakreteArmorMaterial.BASE_DURABILITY
								)
						)
						.setId(key);

		return Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);
	}

	private static Item registerItem(String name, Item.Properties properties) {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":" + name
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		properties =
				properties.setId(key);

		return Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);
	}

	private static Block registerBlock(
			String name,
			BlockBehaviour.Properties properties
	) {
		Identifier id = Identifier.parse(MOD_ID + ":" + name);

		ResourceKey<Block> blockKey =
				ResourceKey.create(
						Registries.BLOCK,
						id
				);

		properties = properties.setId(blockKey);

		Block block = Registry.register(
				BuiltInRegistries.BLOCK,
				id,
				new Block(properties)
		);

		ResourceKey<Item> itemKey =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new BlockItem(
						block,
						new Item.Properties()
								.useBlockDescriptionPrefix()
								.setId(itemKey)
				)
		);

		return block;
	}

	private static Item registerHoneycombBoots() {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":honeycomb_boots"
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		Item.Properties properties =
				new Item.Properties()
						.humanoidArmor(
								DiakreteArmorMaterial.INSTANCE,
								ArmorType.BOOTS
						)
						.durability(
								ArmorType.BOOTS.getDurability(
										DiakreteArmorMaterial.BASE_DURABILITY
								)
						)
						.component(
								DataComponents.EQUIPPABLE,
								Equippable.builder(EquipmentSlot.FEET)
										.setAsset(HONEYCOMB_ASSET)
										.setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
										.build()
						)
						.setId(key);

		return Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);
	}
}