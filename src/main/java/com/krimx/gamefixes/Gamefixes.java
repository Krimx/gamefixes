package com.krimx.gamefixes;

import java.util.Map;
import java.util.function.Function;

import com.krimx.gamefixes.advancement.ModCriteria;
import com.krimx.gamefixes.farming.FarmingExperience;
import com.krimx.gamefixes.loot_bags.AddLootBagTags;
import com.krimx.gamefixes.loot_bags.LootBagOutcomes;
import com.krimx.gamefixes.loot_bags.LootBagOutcomeExecutors;
import com.krimx.gamefixes.network.HoneycombNetworking;
import com.krimx.gamefixes.network.MaceNetworking;
import com.krimx.gamefixes.network.ResearchNetworking;
import com.krimx.gamefixes.research.ResearchAttachments;
import com.krimx.gamefixes.research.ResearchRegistry;
import com.krimx.gamefixes.loot.EnchantWithLevelsMendingFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.biome.Biome;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.item.ItemStack;
import com.krimx.gamefixes.HomeChunkManager;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.pathfinder.PathComputationType;
import com.krimx.gamefixes.block.HydratedFarmlandBlock;
import com.krimx.gamefixes.block.CattailBlock;
import com.krimx.gamefixes.block.TwilightPrismarineBlock;
import com.krimx.gamefixes.block.entity.TwilightPrismarineBlockEntity;

public class Gamefixes implements ModInitializer {

	public static final String MOD_ID = "gamefixes";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	public static final int MAX_STACK_SIZE = 128;

	public static Item REFINED_SULFUR;
	public static Item POLLEN;

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

	public static Item PINK_DIAMOND_SWORD;
	public static Item PINK_DIAMOND_PICKAXE;
	public static Item PINK_DIAMOND_AXE;
	public static Item PINK_DIAMOND_SHOVEL;
	public static Item PINK_DIAMOND_HOE;
	public static Item PINK_DIAMOND_SPEAR;

	public static Item YELLOW_DIAMOND_SWORD;
	public static Item YELLOW_DIAMOND_PICKAXE;
	public static Item YELLOW_DIAMOND_AXE;
	public static Item YELLOW_DIAMOND_SHOVEL;
	public static Item YELLOW_DIAMOND_HOE;
	public static Item YELLOW_DIAMOND_SPEAR;

	public static Item ROSE_GOLD_SWORD;
	public static Item ROSE_GOLD_PICKAXE;
	public static Item ROSE_GOLD_AXE;
	public static Item ROSE_GOLD_SHOVEL;
	public static Item ROSE_GOLD_HOE;
	public static Item ROSE_GOLD_SPEAR;

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
	public static BlockEntityType<TwilightPrismarineBlockEntity> TWILIGHT_PRISMARINE_BLOCK_ENTITY;

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
	public static Block ABUNDANT_FARMLAND;
	public static Block HYDRATED_FARMLAND;
	public static Block CATTAIL;
	public static SimpleParticleType MOSQUITO_PARTICLE;

	public static Item WINGWOVEN_ELYTRA;
	public static Item GILDED_ELYTRA;
	public static Item GILDED_WINGWOVEN_ELYTRA;
	public static Item ELYTRA_CHESTPLATE;
	public static Item HONEYCOMB_BOOTS;
	public static Item GLOW_SQUID_LEGGINGS;
	public static Item ARMADILLO_CHESTPLATE;

	public static Item GOLDEN_POTATO;

	public static Item MILK_BOTTLE;
	public static Block MILK_CAULDRON;
	public static Block CHEESE_CAULDRON;
	public static BlockEntityType<MilkCauldronBlockEntity> MILK_CAULDRON_BLOCK_ENTITY;
	public static Item CHEESE;
	public static Item FLOUR;
	public static Block CHEESE_WHEEL;

	public static Item LOOT_BAG;

	private static final Identifier WINGWOVEN_SPEED_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "wingwoven_elytra_speed");
	private static final Identifier GILDED_ELYTRA_ARMOR_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "gilded_elytra_armor");
	private static final Identifier ELYTRA_CHESTPLATE_ARMOR_MODIFIER_ID =
			Identifier.fromNamespaceAndPath(MOD_ID, "elytra_chestplate_armor");

	private static final ResourceKey<EquipmentAsset> ARMADILLO_CHESTPLATE_ASSET =
			ResourceKey.create(
					EquipmentAssets.ROOT_ID,
					Identifier.fromNamespaceAndPath(MOD_ID, "armadillo_chestplate")
			);

	private static final TagKey<Item> REPAIRS_ARMADILLO_CHESTPLATE =
			TagKey.create(
					BuiltInRegistries.ITEM.key(),
					Identifier.fromNamespaceAndPath(MOD_ID, "repairs_armadillo_chestplate")
			);

	private static final TagKey<Item> REPAIRS_PINK_DIAMOND_TOOLS =
			TagKey.create(
					BuiltInRegistries.ITEM.key(),
					Identifier.fromNamespaceAndPath(MOD_ID, "repairs_pink_diamond_tools")
			);
	private static final TagKey<Item> REPAIRS_YELLOW_DIAMOND_TOOLS =
			TagKey.create(
					BuiltInRegistries.ITEM.key(),
					Identifier.fromNamespaceAndPath(MOD_ID, "repairs_yellow_diamond_tools")
			);
	private static final TagKey<Item> REPAIRS_ROSE_GOLD_TOOLS =
			TagKey.create(
					BuiltInRegistries.ITEM.key(),
					Identifier.fromNamespaceAndPath(MOD_ID, "repairs_rose_gold_tools")
			);

	private static final ToolMaterial PINK_DIAMOND_TOOL_MATERIAL =
			copyToolMaterial(ToolMaterial.DIAMOND, REPAIRS_PINK_DIAMOND_TOOLS);
	private static final ToolMaterial YELLOW_DIAMOND_TOOL_MATERIAL =
			copyToolMaterial(ToolMaterial.DIAMOND, REPAIRS_YELLOW_DIAMOND_TOOLS);
	private static final ToolMaterial ROSE_GOLD_TOOL_MATERIAL =
			copyToolMaterial(ToolMaterial.GOLD, REPAIRS_ROSE_GOLD_TOOLS);

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

		MOSQUITO_PARTICLE = FabricParticleTypes.simple();
		Registry.register(
				BuiltInRegistries.PARTICLE_TYPE,
				Identifier.fromNamespaceAndPath(MOD_ID, "mosquito"),
				MOSQUITO_PARTICLE
		);

		REFINED_SULFUR = registerItem("refined_sulfur", new Item.Properties());
		POLLEN = registerItem("pollen", new Item.Properties());
		FRAMED_ELYTRA_TRIM = registerItem("framed_elytra_trim", new Item.Properties());
		BLIGHT_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("blight_armor_trim_smithing_template", new Item.Properties());
		GHAST_RESIN = registerItem("ghast_resin", new Item.Properties());
		TIDE_SHELL = registerItem("tide_shell", new Item.Properties());
		WINGWEAVE = registerItem("wingweave", new Item.Properties());
		RAW_ROSE_GOLD = registerItem("raw_rose_gold", new Item.Properties());

		Identifier lootBagId =
				Identifier.fromNamespaceAndPath(
						MOD_ID,
						"loot_bag"
				);

		ResourceKey<Item> lootBagKey =
				ResourceKey.create(
						Registries.ITEM,
						lootBagId
				);

		LOOT_BAG = Registry.register(
				BuiltInRegistries.ITEM,
				lootBagId,
				new LootBagItem(
						new Item.Properties()
								.setId(lootBagKey)
				)
		);

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

		DefaultItemComponentEvents.MODIFY.register(context ->
				context.modify(
						item -> item.getDefaultMaxStackSize() == 1
								&& item.components().has(DataComponents.CONSUMABLE),
						(builder, lookup, item) ->
								builder.set(
										DataComponents.MAX_STACK_SIZE,
										16
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

		PINK_DIAMOND_SWORD = registerItem("pink_diamond_sword", new Item.Properties().sword(PINK_DIAMOND_TOOL_MATERIAL, 3.0F, -2.4F));
		PINK_DIAMOND_PICKAXE = registerItem("pink_diamond_pickaxe", new Item.Properties().pickaxe(PINK_DIAMOND_TOOL_MATERIAL, 1.0F, -2.8F));
		PINK_DIAMOND_AXE = registerItem("pink_diamond_axe", new Item.Properties().axe(PINK_DIAMOND_TOOL_MATERIAL, 5.0F, -3.0F));
		PINK_DIAMOND_SHOVEL = registerItem("pink_diamond_shovel", new Item.Properties().shovel(PINK_DIAMOND_TOOL_MATERIAL, 1.5F, -3.0F));
		PINK_DIAMOND_HOE = registerItem("pink_diamond_hoe", new Item.Properties().hoe(PINK_DIAMOND_TOOL_MATERIAL, -3.0F, 0.0F));
		PINK_DIAMOND_SPEAR = registerItem("pink_diamond_spear", new Item.Properties().spear(PINK_DIAMOND_TOOL_MATERIAL, 1.05F, 1.075F, 0.5F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F));

		YELLOW_DIAMOND_SWORD = registerItem("yellow_diamond_sword", new Item.Properties().sword(YELLOW_DIAMOND_TOOL_MATERIAL, 3.0F, -2.4F));
		YELLOW_DIAMOND_PICKAXE = registerItem("yellow_diamond_pickaxe", new Item.Properties().pickaxe(YELLOW_DIAMOND_TOOL_MATERIAL, 1.0F, -2.8F));
		YELLOW_DIAMOND_AXE = registerItem("yellow_diamond_axe", new Item.Properties().axe(YELLOW_DIAMOND_TOOL_MATERIAL, 5.0F, -3.0F));
		YELLOW_DIAMOND_SHOVEL = registerItem("yellow_diamond_shovel", new Item.Properties().shovel(YELLOW_DIAMOND_TOOL_MATERIAL, 1.5F, -3.0F));
		YELLOW_DIAMOND_HOE = registerItem("yellow_diamond_hoe", new Item.Properties().hoe(YELLOW_DIAMOND_TOOL_MATERIAL, -3.0F, 0.0F));
		YELLOW_DIAMOND_SPEAR = registerItem("yellow_diamond_spear", new Item.Properties().spear(YELLOW_DIAMOND_TOOL_MATERIAL, 1.05F, 1.075F, 0.5F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F));

		ROSE_GOLD_SWORD = registerItem("rose_gold_sword", new Item.Properties().sword(ROSE_GOLD_TOOL_MATERIAL, 3.0F, -2.4F));
		ROSE_GOLD_PICKAXE = registerItem("rose_gold_pickaxe", new Item.Properties().pickaxe(ROSE_GOLD_TOOL_MATERIAL, 1.0F, -2.8F));
		ROSE_GOLD_AXE = registerItem("rose_gold_axe", new Item.Properties().axe(ROSE_GOLD_TOOL_MATERIAL, 6.0F, -3.0F));
		ROSE_GOLD_SHOVEL = registerItem("rose_gold_shovel", new Item.Properties().shovel(ROSE_GOLD_TOOL_MATERIAL, 1.5F, -3.0F));
		ROSE_GOLD_HOE = registerItem("rose_gold_hoe", new Item.Properties().hoe(ROSE_GOLD_TOOL_MATERIAL, 0.0F, -3.0F));
		ROSE_GOLD_SPEAR = registerItem("rose_gold_spear", new Item.Properties().spear(ROSE_GOLD_TOOL_MATERIAL, 0.95F, 0.7F, 0.7F, 3.5F, 13.0F, 8.5F, 5.1F, 13.75F, 4.6F));

		HONEYCOMB_BOOTS = registerHoneycombBoots();
		GLOW_SQUID_LEGGINGS = registerGlowSquidLeggings();
		ARMADILLO_CHESTPLATE = registerArmadilloChestplate();

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
				BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE),
				TwilightPrismarineBlock::new
		);

		TWILIGHT_PRISMARINE_BLOCK_ENTITY = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				Identifier.fromNamespaceAndPath(MOD_ID, "twilight_prismarine"),
				FabricBlockEntityTypeBuilder.create(
						TwilightPrismarineBlockEntity::new,
						TWILIGHT_PRISMARINE
				).build()
		);

		CHARCOAL_ORE = registerBlock(
				"charcoal_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE)
		);

		DEEPSLATE_CHARCOAL_ORE = registerBlock(
				"deepslate_charcoal_ore",
				BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_COAL_ORE)
		);

		ABUNDANT_FARMLAND = registerFarmlandBlock(
				"abundant_farmland",
				BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)
		);

		HYDRATED_FARMLAND = registerHydratedFarmlandBlock(
				"hydrated_farmland",
				BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)
		);

		Identifier cattailId =
				Identifier.fromNamespaceAndPath(
						MOD_ID,
						"cattail"
				);

		ResourceKey<Block> cattailKey =
				ResourceKey.create(
						Registries.BLOCK,
						cattailId
				);

		CATTAIL = Registry.register(
				BuiltInRegistries.BLOCK,
				cattailId,
				new CattailBlock(
						BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)
								.setId(cattailKey)
				)
		);

		ResourceKey<Item> cattailItemKey =
				ResourceKey.create(
						Registries.ITEM,
						cattailId
				);

		Registry.register(
				BuiltInRegistries.ITEM,
				cattailId,
				new BlockItem(
						CATTAIL,
						new Item.Properties()
								.useBlockDescriptionPrefix()
								.setId(cattailItemKey)
				)
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
				new MilkCauldronBlock(
						Biome.Precipitation.NONE,
						MilkCauldronInteractions.createDispatcher(),
						BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
								.setId(milkCauldronKey)
				)
		);

		MILK_CAULDRON_BLOCK_ENTITY = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE,
				Identifier.fromNamespaceAndPath(MOD_ID, "milk_cauldron"),
				FabricBlockEntityTypeBuilder.create(
						MilkCauldronBlockEntity::new,
						MILK_CAULDRON
				).build()
		);

		Identifier cheeseWheelId =
				Identifier.fromNamespaceAndPath(
						MOD_ID,
						"cheese_wheel"
				);

		ResourceKey<Block> cheeseWheelKey =
				ResourceKey.create(
						Registries.BLOCK,
						cheeseWheelId
				);

		CHEESE_WHEEL = Registry.register(
				BuiltInRegistries.BLOCK,
				cheeseWheelId,
				new CheeseWheelBlock(
						BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)
								.setId(cheeseWheelKey)
				)
		);

		Registry.register(
				BuiltInRegistries.ITEM,
				cheeseWheelId,
				new BlockItem(
						CHEESE_WHEEL,
						new Item.Properties()
								.setId(
										ResourceKey.create(
												Registries.ITEM,
												cheeseWheelId
										)
								)
				)
		);

		MilkCauldronInteractions.registerEmptyCauldronInteractions();

		Identifier cheeseCauldronId =
				Identifier.fromNamespaceAndPath(
						MOD_ID,
						"cheese_cauldron"
				);

		ResourceKey<Block> cheeseCauldronKey =
				ResourceKey.create(
						Registries.BLOCK,
						cheeseCauldronId
				);

		CHEESE_CAULDRON = Registry.register(
				BuiltInRegistries.BLOCK,
				cheeseCauldronId,
				new LayeredCauldronBlock(
						Biome.Precipitation.NONE,
						CheeseCauldronInteractions.createDispatcher(),
						BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
								.setId(cheeseCauldronKey)
				)
		);

		CHEESE = registerItem(
				"cheese",
				new Item.Properties()
						.food(
								new FoodProperties.Builder()
										.nutrition(4)
										.saturationModifier(0.8F)
										.build()
						)
		);

		FLOUR = registerItem("flour", new Item.Properties());

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
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(POLLEN));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(MILK_BOTTLE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(CHEESE));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(GOLDEN_POTATO));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(FLOUR));
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
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_SWORD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_SWORD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_SWORD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(PINK_DIAMOND_SPEAR));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(YELLOW_DIAMOND_SPEAR));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ROSE_GOLD_SPEAR));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register(output -> {
					output.accept(PINK_DIAMOND_PICKAXE);
					output.accept(PINK_DIAMOND_AXE);
					output.accept(PINK_DIAMOND_SHOVEL);
					output.accept(PINK_DIAMOND_HOE);
					output.accept(YELLOW_DIAMOND_PICKAXE);
					output.accept(YELLOW_DIAMOND_AXE);
					output.accept(YELLOW_DIAMOND_SHOVEL);
					output.accept(YELLOW_DIAMOND_HOE);
					output.accept(ROSE_GOLD_PICKAXE);
					output.accept(ROSE_GOLD_AXE);
					output.accept(ROSE_GOLD_SHOVEL);
					output.accept(ROSE_GOLD_HOE);
				});
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
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(GLOW_SQUID_LEGGINGS));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
				.register(output -> output.accept(ARMADILLO_CHESTPLATE));
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
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
				.register(output -> output.accept(CHEESE_WHEEL.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS)
				.register(output -> output.accept(CATTAIL.asItem()));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> output.accept(LOOT_BAG));


		PlayerBlockBreakEvents.AFTER.register(
				(level, player, pos, state, blockEntity) -> {
					if (state.getBlock() instanceof CropBlock crop
							&& crop.isMaxAge(state)
							&& !player.isCreative()) {
						FarmingExperience.award(
								level,
								pos,
								player.getMainHandItem()
						);
					}
				}
		);


		ItemEvents.USE.register(
				(level, player, hand) -> {

					ItemStack stack =
							player.getItemInHand(hand);

					if (!(stack.getItem()
							instanceof MaceItem)) {
						return null;
					}

					player.startUsingItem(hand);

					return InteractionResult.CONSUME;
				}
		);

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				GlowSquidLeggingsLighting.tick(player);
			}
		});

		ResearchAttachments.initialize();
		ResearchRegistry.initialize();
		ResearchNetworking.registerCommon();
		MaceNetworking.registerCommon();
		ConcreteConversion.initialize();
		HomeChunkManager.initialize();
		LootBagComponents.initialize();
		LootBagCraftingRecipe.initialize();
		AddLootBagTags.initialize();
		LootBagOutcomes.initialize();
		LootBagOutcomeExecutors.initialize();
		HoneycombNetworking.initialize();
		ModCriteria.init();

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

	private static ToolMaterial copyToolMaterial(ToolMaterial vanillaMaterial, TagKey<Item> repairItems) {
		return new ToolMaterial(
				vanillaMaterial.incorrectBlocksForDrops(),
				vanillaMaterial.durability(),
				vanillaMaterial.speed(),
				vanillaMaterial.attackDamageBonus(),
				vanillaMaterial.enchantmentValue(),
				repairItems
		);
	}

	private static Block registerBlock(
			String name,
			BlockBehaviour.Properties properties
	) {
		return registerBlock(name, properties, Block::new);
	}

	private static Block registerBlock(
			String name,
			BlockBehaviour.Properties properties,
			Function<BlockBehaviour.Properties, ? extends Block> blockFactory
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
				blockFactory.apply(properties)
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

	private static Block registerFarmlandBlock(
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
				new FarmlandBlock(Blocks.FARMLAND, properties)
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
								ArmorMaterials.HONEYCOMB_BOOTS,
								ArmorType.BOOTS
						)
						.durability(222)
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

	private static Item registerArmadilloChestplate() {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":armadillo_chestplate"
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		ArmorMaterial material =
				new ArmorMaterial(
						15,
						Map.of(
								ArmorType.CHESTPLATE, 6
						),
						9,
						SoundEvents.ARMOR_EQUIP_LEATHER,
						0.0F,
						0.0F,
						REPAIRS_ARMADILLO_CHESTPLATE,
						ARMADILLO_CHESTPLATE_ASSET
				);

		Item.Properties properties =
				new Item.Properties()
						.humanoidArmor(
								material,
								ArmorType.CHESTPLATE
						)
						.durability(
								ArmorType.CHESTPLATE.getDurability(15)
						)
						.setId(key);

		return Registry.register(
				BuiltInRegistries.ITEM,
				id,
				new Item(properties)
		);
	}

	private static Item registerGlowSquidLeggings() {
		Identifier id =
				Identifier.parse(
						MOD_ID + ":glow_squid_leggings"
				);

		ResourceKey<Item> key =
				ResourceKey.create(
						Registries.ITEM,
						id
				);

		ArmorMaterial material =
				new ArmorMaterial(
						24,
						java.util.Map.of(
								ArmorType.LEGGINGS, 2
						),
						1,
						SoundEvents.ARMOR_EQUIP_LEATHER,
						0.0F,
						0.0F,
						ArmorMaterials.REPAIRS_GLOW_SQUID_LEGGINGS,
						ArmorMaterials.GLOW_SQUID_LEGGINGS_ASSET
				);

		Item.Properties properties =
				new Item.Properties()
						.humanoidArmor(
								material,
								ArmorType.LEGGINGS
						)
						.durability(216)
						.component(
								DataComponents.EQUIPPABLE,
								Equippable.builder(EquipmentSlot.LEGS)
										.setAsset(ArmorMaterials.GLOW_SQUID_LEGGINGS_ASSET)
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

	private static Block registerHydratedFarmlandBlock(
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
				new HydratedFarmlandBlock(Blocks.FARMLAND, properties)
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
}
