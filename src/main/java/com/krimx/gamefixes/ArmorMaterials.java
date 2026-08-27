package com.krimx.gamefixes;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public final class ArmorMaterials {

    public static final int PINK_DIAMOND_BASE_DURABILITY = 33;
    public static final int YELLOW_DIAMOND_BASE_DURABILITY = 33;
    public static final int ROSE_GOLD_BASE_DURABILITY = 7;

    public static final ResourceKey<EquipmentAsset> PINK_DIAMOND_ARMOR_ASSET =
            equipmentAsset("pink_diamond");

    public static final ResourceKey<EquipmentAsset> YELLOW_DIAMOND_ARMOR_ASSET =
            equipmentAsset("yellow_diamond");

    public static final ResourceKey<EquipmentAsset> ROSE_GOLD_ARMOR_ASSET =
            equipmentAsset("rose_gold");

    public static final TagKey<Item> REPAIRS_PINK_DIAMOND_ARMOR =
            repairTag("repairs_pink_diamond_armor");

    public static final TagKey<Item> REPAIRS_YELLOW_DIAMOND_ARMOR =
            repairTag("repairs_yellow_diamond_armor");

    public static final TagKey<Item> REPAIRS_ROSE_GOLD_ARMOR =
            repairTag("repairs_rose_gold_armor");

    public static final ArmorMaterial PINK_DIAMOND =
            new ArmorMaterial(
                    PINK_DIAMOND_BASE_DURABILITY,
                    Map.of(
                            ArmorType.HELMET, 3,
                            ArmorType.CHESTPLATE, 8,
                            ArmorType.LEGGINGS, 6,
                            ArmorType.BOOTS, 3
                    ),
                    10,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    2.0F,
                    0.0F,
                    REPAIRS_PINK_DIAMOND_ARMOR,
                    PINK_DIAMOND_ARMOR_ASSET
            );

    public static final ArmorMaterial YELLOW_DIAMOND =
            new ArmorMaterial(
                    YELLOW_DIAMOND_BASE_DURABILITY,
                    Map.of(
                            ArmorType.HELMET, 3,
                            ArmorType.CHESTPLATE, 8,
                            ArmorType.LEGGINGS, 6,
                            ArmorType.BOOTS, 3
                    ),
                    10,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    2.0F,
                    0.0F,
                    REPAIRS_YELLOW_DIAMOND_ARMOR,
                    YELLOW_DIAMOND_ARMOR_ASSET
            );

    public static final ArmorMaterial ROSE_GOLD =
            new ArmorMaterial(
                    ROSE_GOLD_BASE_DURABILITY,
                    Map.of(
                            ArmorType.HELMET, 2,
                            ArmorType.CHESTPLATE, 5,
                            ArmorType.LEGGINGS, 3,
                            ArmorType.BOOTS, 1
                    ),
                    25,
                    SoundEvents.ARMOR_EQUIP_GOLD,
                    0.0F,
                    0.0F,
                    REPAIRS_ROSE_GOLD_ARMOR,
                    ROSE_GOLD_ARMOR_ASSET
            );

    public static ResourceKey<EquipmentAsset> equipmentAsset(String name) {
        return ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, name)
        );
    }

    public static TagKey<Item> repairTag(String name) {
        return TagKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, name)
        );
    }



    public static final ResourceKey<EquipmentAsset> WINGWOVEN_ELYTRA_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, "wingwoven_elytra")
            );

    public static final ResourceKey<EquipmentAsset> GILDED_ELYTRA_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, "gilded_elytra")
            );

    public static final ResourceKey<EquipmentAsset> GILDED_WINGWOVEN_ELYTRA_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, "gilded_wingwoven_elytra")
            );
    public static final ResourceKey<EquipmentAsset> ELYTRA_CHESTPLATE_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, "elytra_chestplate")
            );
    public static final ResourceKey<EquipmentAsset> HONEYCOMB_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(Gamefixes.MOD_ID, "honeycomb")
            );

    public ArmorMaterials() {
    }
}
