package com.krimx.gamefixes.datagen;

import com.krimx.gamefixes.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class GamefixesEnchantmentGenerator
        extends FabricDynamicRegistryProvider {

    public GamefixesEnchantmentGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(
            HolderLookup.Provider registries,
            Entries entries
    ) {
        entries.addAll(
                registries.lookupOrThrow(Registries.ENCHANTMENT)
        );
    }

    @Override
    public String getName() {
        return "Enchantments";
    }

    private static void register(
            BootstrapContext<Enchantment> context,
            ResourceKey<Enchantment> key,
            Enchantment.Builder builder
    ) {
        context.register(
                key,
                builder.build(key.identifier())
        );
    }

    public static void bootstrap(
            BootstrapContext<Enchantment> context
    ) {
        register(
                context,
                ModEnchantments.ABUNDANCE,
                Enchantment.enchantment(
                        Enchantment.definition(
                                context.lookup(Registries.ITEM)
                                        .getOrThrow(ItemTags.HOES),
                                2,
                                1,
                                Enchantment.dynamicCost(15, 9),
                                Enchantment.dynamicCost(65, 9),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        )
                ).exclusiveWith(
                        HolderSet.direct(
                                context.lookup(Registries.ENCHANTMENT)
                                        .getOrThrow(ModEnchantments.ABUNDANCE)
                        )
                )
        );

        register(
                context,
                ModEnchantments.HYDRATION,
                Enchantment.enchantment(
                        Enchantment.definition(
                                context.lookup(Registries.ITEM)
                                        .getOrThrow(ItemTags.HOES),
                                2,
                                1,
                                Enchantment.dynamicCost(15, 9),
                                Enchantment.dynamicCost(65, 9),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        )
                ).exclusiveWith(
                        HolderSet.direct(
                                context.lookup(Registries.ENCHANTMENT)
                                        .getOrThrow(ModEnchantments.HYDRATION)
                        )
                )
        );
    }
}