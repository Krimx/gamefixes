package com.krimx.gamefixes.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class GamefixesDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(
            FabricDataGenerator fabricDataGenerator
    ) {
        FabricDataGenerator.Pack pack =
                fabricDataGenerator.createPack();

        pack.addProvider(
                GamefixesEnchantmentGenerator::new
        );

        pack.addProvider(
                GamefixesEnchantmentTagProvider::new
        );
    }

    @Override
    public void buildRegistry(
            RegistrySetBuilder registryBuilder
    ) {
        registryBuilder.add(
                Registries.ENCHANTMENT,
                GamefixesEnchantmentGenerator::bootstrap
        );
    }
}