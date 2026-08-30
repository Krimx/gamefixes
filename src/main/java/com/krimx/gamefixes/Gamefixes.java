package com.krimx.gamefixes;

import com.krimx.gamefixes.network.MaceNetworking;
import com.krimx.gamefixes.network.ResearchNetworking;
import com.krimx.gamefixes.research.ResearchAttachments;
import com.krimx.gamefixes.research.ResearchRegistry;
import com.krimx.gamefixes.loot.EnchantWithLevelsMendingFunction;
import net.minecraft.util.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
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
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.biome.Biome;

import net.minecraft.world.item.ItemStack;

public class Gamefixes implements ModInitializer {

	public static final String MOD_ID = "gamefixes";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	public static final int MAX_STACK_SIZE = 128;

	public static Item REFINED_SULFUR;

	public static Item DIAKRETE;
	public static Item DIAKRETE_HELMET;
	public static Item DIAKRETE_CHESTPLATE;
	public static Item DIAKRETE_LEGGINGS;
	public static Item DIAKRETE_BOOTS;
	public static final double diakreteArmorFloatSpeed = 0.1D;

	public static Item PINK_DIAMOND_HELMET;
	public static Item PINK_DIAMOND_CHESTPLATE;
	public static Item PINK_DIAMOND_LEGGINGS;
	public static Item PINK_DIAMOND_BOOTS;

	public static Item YELLOW_DIAMOND_HELMET;
	public static Item YELLOW_DIAMOND_CHESTPLATE;
	public static Item YELLOW_DIAMOND_LEGGINGS;
	public static Item YELLOW_DIAMOND_BOOTS;

	public static Item ROSE_GOLD_HELMET;
	public static Item ROSE_GOLD_CHESTPLATE;
	public static Item ROSE_GOLD_LEGGINGS;
	public static Item ROSE_GOLD_BOOTS;

	public static Item ROSE_GOLD_INGOT;
	public static Item RAW_ROSE_GOLD;
	public static Item PINK_DIAMOND;
	public static Item YELLOW_DIAMOND;

	public static Item FRAMED_ELYTRA_TRIM;
	public static Item BLIGHT_ARMOR_TRIM_SMITHING_TEMPLATE;

	public static Item GHAST_RESIN;
	public static Item TIDE_SHELL;
	public static Item WINGWEAVE;
	public static Block TWILIGHT_PRISMARINE;

	public static Block PINK_DIAMOND_ORE;
	public static Block DEEPSLATE_PINK_DIAMOND_ORE;
	public static Block PINK_DIAMOND_BLOCK;
	public static Block YELLOW_DIAMOND_ORE;
	public static Block DEEPSLATE_YELLOW_DIAMOND_ORE;
	public static Block YELLOW_DIAMOND_BLOCK;
	public static Block ROSE_GOLD_ORE;
	public static Block DEEPSLATE_ROSE_GOLD_ORE;
	public static Block RAW_ROSE_GOLD_BLOCK;
	public static Block ROSE_GOLD_BLOCK;
	public static Block CHARCOAL_ORE;
	public static Block DEEPSLATE_CHARCOAL_ORE;

	public static Item WINGWOVEN_ELYTRA;
	public static Item GILDED_ELYTRA;
	public static Item GILDED_WINGWOVEN_ELYTRA;
	public static Item ELYTRA_CHESTPLATE;
	public static Item HONEYCOMB_BOOTS;

	public static Item GOLDEN_POTATO;

	public static Item MILK_BOTTLE;
	public static Block MILK_CAULDRON;

	private static final Identifier WINGWOVEN_SPEED_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "wingwoven_elytra_speed");
	private static final Identifier GILDED_ELYTRA_ARMOR_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "gilded_elytra_armor");
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
		BLIGHT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("blight_armor_trim_smithing_template", new Item.Properties());
		GHAST_RESIN = registerItem("ghast_resin", new Item.Properties());
		TIDE_SHELL = registerItem("tide_shell", new Item.Properties());
		WINGWEAVE = registerItem("wingweave", new Item.Properties());
		RAW_ROSE_GOLD = registerItem("raw_rose_gold", new Item.Properties());

		DIAKRETE = registerItem(
				"diakrete", new Item.Properties().delayedHolderComponent(
						DataComponents.PROVIDES_TRIM_MATERIAL,
						ResourceKey.create(
								Registries.TRIM_MATERIAL,
								Identifier.fromNamespaceAndPath(MOD_ID, "diakrete")
						)
				)
		);
		PINK_DIAMOND = registerItem(
				"pink_diamond", new Item.Properties().delayedHolderComponent(
						DataComponents.PROVIDES_TRIM_MATERIAL,
						ResourceKey.create(
								Registries.TRIM_MATERIAL,
								Identifier.fromNamespaceAndPath(MOD_ID, "pink_diamond")
						)
				)
		);
		YELLOW_DIAMOND = registerItem(
				"yellow_diamond", new Item.Properties().delayedHolderComponent(
						DataComponents.PROVIDES_TRIM_MATERIAL,
						ResourceKey.create(
								Registries.TRIM_MATERIAL,
								Identifier.fromNamespaceAndPath(MOD_ID, "yellow_diamond")
						)
				)
		);
		ROSE_GOLD_INGOT = registerItem(
				"rose_gold_ingot", new Item.Properties().delayedHolderComponent(
						DataComponents.PROVIDES_TRIM_MATERIAL,
						ResourceKey.create(
								Registries.TRIM_MATERIAL,
								Identifier.fromNamespaceAndPath(MOD_ID, "rose_gold_ingot")
						)
				)
		);

		DefaultItemComponentEvents.MODIFY.register(context ->
				context.modify(Items.ECHO_SHARD, (builder, lookup, item) ->
						builder.set(
								DataComponents.PROVIDES_TRIM_MATERIAL,
								lookup.lookupOrThrow(Registries.TRIM_MATERIAL).getOrThrow(
										ResourceKey.create(
												Registries.TRIM_MATERIAL,
												Identifier.fromNamespaceAndPath(MOD_ID, "sculk")
										)
								)
						)
				)
		);

		DefaultItemComponentEvents.MODIFY.register(context ->
				context.modify(
						item -> item.getDefaultMaxStackSize() == 64,
						(builder, lookup, item) ->
								builder.set(
										DataComponents.MAX_STACK_SIZE,
										128
								)
				)
		);

		DIAKRETE_HELMET = registerArmorItem("diakrete_helmet", ArmorType.HELMET, DiakreteArmorMaterial.INSTANCE, DiakreteArmorMaterial.BASE_DURABILITY);
		DIAKRETE_CHESTPLATE = registerArmorItem("diakrete_chestplate", ArmorType.CHESTPLATE, DiakreteArmorMaterial.INSTANCE, DiakreteArmorMaterial.BASE_DURABILITY);
		DIAKRETE_LEGGINGS = registerArmorItem("diakrete_leggings", ArmorType.LEGGINGS, DiakreteArmorMaterial.INSTANCE, DiakreteArmorMaterial.BASE_DURABILITY);
		DIAKRETE_BOOTS = registerArmorItem("diakrete_boots", ArmorType.BOOTS, DiakreteArmorMaterial.INSTANCE, DiakreteArmorMaterial.BASE_DURABILITY);

		PINK_DIAMOND_HELMET = registerArmorItem("pink_diamond_helmet", ArmorType.HELMET, ArmorMaterials.PINK_DIAMOND, ArmorMaterials.PINK_DIAMOND_BASE_DURABILITY);
		PINK_DIAMOND_CHESTPLATE = registerArmorItem("pink_diamond_chestplate", ArmorType.CHESTPLATE, ArmorMaterials.PINK_DIAMOND, ArmorMaterials.PINK_DIAMOND_BASE_DURABILITY);
		PINK_DIAMOND_LEGGINGS = registerArmorItem("pink_diamond_leggings", ArmorType.LEGGINGS, ArmorMaterials.PINK_DIAMOND, ArmorMaterials.PINK_DIAMOND_BASE_DURABILITY);
		PINK_DIAMOND_BOOTS = registerArmorItem("pink_diamond_boots", ArmorType.BOOTS, ArmorMaterials.PINK_DIAMOND, ArmorMaterials.PINK_DIAMOND_BASE_DURABILITY);

		YELLOW_DIAMOND_HELMET = registerArmorItem("yellow_diamond_helmet", ArmorType.HELMET, ArmorMaterials.YELLOW_DIAMOND, ArmorMaterials.YELLOW_DIAMOND_BASE_DURABILITY);
		YELLOW_DIAMOND_CHESTPLATE = registerArmorItem("yellow_diamond_chestplate", ArmorType.CHESTPLATE, ArmorMaterials.YELLOW_DIAMOND, ArmorMaterials.YELLOW_DIAMOND_BASE_DURABILITY);
		YELLOW_DIAMOND_LEGGINGS = registerArmorItem("yellow_diamond_leggings", ArmorType.LEGGINGS, ArmorMaterials.YELLOW_DIAMOND, ArmorMaterials.YELLOW_DIAMOND_BASE_DURABILITY);
		YELLOW_DIAMOND_BOOTS = registerArmorItem("yellow_diamond_boots", ArmorType.BOOTS, ArmorMaterials.YELLOW_DIAMOND, ArmorMaterials.YELLOW_DIAMOND_BASE_DURABILITY);

		ROSE_GOLD_HELMET = registerArmorItem("rose_gold_helmet", ArmorType.HELMET, ArmorMaterials.ROSE_GOLD, ArmorMaterials.ROSE_GOLD_BASE_DURABILITY);
		ROSE_GOLD_CHESTPLATE = registerArmorItem("rose_gold_chestplate", ArmorType.CHESTPLATE, ArmorMaterials.ROSE_GOLD, ArmorMaterials.ROSE_GOLD_BASE_DURABILITY);
		ROSE_GOLD_LEGGINGS = registerArmorItem("rose_gold_leggings", ArmorType.LEGGINGS, ArmorMaterials.ROSE_GOLD, ArmorMaterials.ROSE_GOLD_BASE_DURABILITY);
		ROSE_GOLD_BOOTS = registerArmorItem("rose_gold_boots", ArmorType.BOOTS, ArmorMaterials.ROSE_GOLD, ArmorMaterials.ROSE_GOLD_BASE_DURABILITY);

		HONEYCOMB_BOOTS = registerHoneycombBoots();

		WINGWOVEN_ELYTRA = registerItem(
				"wingwoven_elytra",
				createGliderProperties(
						432,
						ArmorMaterials.WINGWOVEN_ELYTRA_ASSET,
						true,
						0.0
				)
		);

		GILDED_ELYTRA = registerItem(
				"gilded_elytra",
				createGliderProperties(
						864,
						ArmorMaterials.GILDED_ELYTRA_ASSET,
						false,
						2.0
				)
		);

		GILDED_WINGWOVEN_ELYTRA = registerItem(
				"gilded_wingwoven_elytra",
				createGliderProperties(
						864,
						ArmorMaterials.GILDED_WINGWOVEN_ELYTRA_ASSET,
						true,
						2.0
				)
		);

		ELYTRA_CHESTPLATE = registerElytraChestplate();

		PINK_DIAMOND_ORE = registerBlock(
				"pink_diamond_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE)
		);

		DEEPSLATE_PINK_DIAMOND_ORE = registerBlock(
				"deepslate_pink_diamond_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
		);

		PINK_DIAMOND_BLOCK = registerBlock(
				"pink_diamond_block",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)
		);

		YELLOW_DIAMOND_ORE = registerBlock(
				"yellow_diamond_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE)
		);

		DEEPSLATE_YELLOW_DIAMOND_ORE = registerBlock(
				"deepslate_yellow_diamond_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
		);

		YELLOW_DIAMOND_BLOCK = registerBlock(
				"yellow_diamond_block",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)
		);

		ROSE_GOLD_ORE = registerBlock(
				"rose_gold_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_ORE)
		);

		DEEPSLATE_ROSE_GOLD_ORE = registerBlock(
				"deepslate_rose_gold_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_GOLD_ORE)
		);

		RAW_ROSE_GOLD_BLOCK = registerBlock(
				"raw_rose_gold_block",
				BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_GOLD_BLOCK)
		);

		ROSE_GOLD_BLOCK = registerBlock(
				"rose_gold_block",
				BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)
		);

		TWILIGHT_PRISMARINE = registerBlock(
				"twilight_prismarine",
				BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE)
		);

		CHARCOAL_ORE = registerBlock(
				"charcoal_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE)
		);

		DEEPSLATE_CHARCOAL_ORE = registerBlock(
				"deepslate_charcoal_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_COAL_ORE)
		);

		Identifier milkCauldronId =
				Identifier.fromNamespaceAndPath(
						MOD_ID,
						"milk_cauldron"
				);

		ResourceKey<Block> milkCauldronKey =
				ResourceKey.create(
						Registries.BLOCK,
						milkCauldronId
				);

		MILK_CAULDRON = Registry.register(
				BuiltInRegistries.BLOCK,
				milkCauldronId,
				new LayeredCauldronBlock(
						Biome.Precipitation.NONE,
						MilkCauldronInteractions.createDispatcher(),
						BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
								.setId(milkCauldronKey)
				)
		);

		MilkCauldronInteractions.registerEmptyCauldronInteractions();

		MILK_BOTTLE = registerItem(
				"milk_bottle",
				new Item.Properties()
						.component(DataComponents.MAX_STACK_SIZE, 16)
						.component(
								DataComponents.USE_REMAINDER,
								new UseRemainder(
										new ItemStackTemplate(Items.GLASS_BOTTLE)
								)
						)
						.component(
								DataComponents.CONSUMABLE,
								Consumables.MILK_BUCKET
						)
		);

		GOLDEN_POTATO = registerItem(
				"golden_potato",
				new Item.Properties()
						.food(
								new FoodProperties.Builder()
										.nutrition(6)
										.saturationModifier(1.2F)
										.build()
						)
		);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(REFINED_SULFUR));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(MILK_BOTTLE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(DIAKRETE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(PINK_DIAMOND));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(YELLOW_DIAMOND));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(ROSE_GOLD_INGOT));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(RAW_ROSE_GOLD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(PINK_DIAMOND_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(YELLOW_DIAMOND_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(ROSE_GOLD_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(RAW_ROSE_GOLD_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_HELMET));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(DIAKRETE_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_HELMET));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_HELMET));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_HELMET));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_CHESTPLATE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_BOOTS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(FRAMED_ELYTRA_TRIM));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(GHAST_RESIN));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(TIDE_SHELL));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(WINGWEAVE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(BLIGHT_ARMOR_TRIM_SMITHING_TEMPLATE));
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
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(PINK_DIAMOND_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(YELLOW_DIAMOND_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(ROSE_GOLD_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(RAW_ROSE_GOLD_BLOCK.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(PINK_DIAMOND_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(DEEPSLATE_PINK_DIAMOND_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(YELLOW_DIAMOND_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(DEEPSLATE_YELLOW_DIAMOND_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(ROSE_GOLD_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(DEEPSLATE_ROSE_GOLD_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(CHARCOAL_ORE.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(DEEPSLATE_CHARCOAL_ORE.asItem()));

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
										.setAsset(ArmorMaterials.ELYTRA_CHESTPLATE_ASSET)
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
			ArmorType type,
			net.minecraft.world.item.equipment.ArmorMaterial material,
			int baseDurability
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
								material,
								type
						)
						.durability(
								type.getDurability(baseDurability)
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
						MOD_ID + ":"
								+ name
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
										.setAsset(ArmorMaterials.HONEYCOMB_ASSET)
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