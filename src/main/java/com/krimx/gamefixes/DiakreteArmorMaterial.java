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

public final class DiakreteArmorMaterial {

    public static final int BASE_DURABILITY = 33;

    public static final ResourceKey<EquipmentAsset> DIAKRETE_ARMOR_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            "diakrete"
                    )
            );

    public static final TagKey<Item> REPAIRS_DIAKRETE_ARMOR =
            TagKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            "repairs_diakrete_armor"
                    )
            );

    public static final ArmorMaterial INSTANCE =
            new ArmorMaterial(
                    BASE_DURABILITY,
                    Map.of(
                            ArmorType.HELMET, 3,
                            ArmorType.CHESTPLATE, 7,
                            ArmorType.LEGGINGS, 6,
                            ArmorType.BOOTS, 2
                    ),
                    10,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    2.0F,
                    0.0F,
                    REPAIRS_DIAKRETE_ARMOR,
                    DIAKRETE_ARMOR_ASSET
            );

    private DiakreteArmorMaterial() {
    }
}
