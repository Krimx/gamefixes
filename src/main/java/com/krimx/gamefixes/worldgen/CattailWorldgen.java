package com.krimx.gamefixes.worldgen;

import com.krimx.gamefixes.Gamefixes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class CattailWorldgen {

    public static final ResourceKey<PlacedFeature> CATTAIL_PLACED_KEY =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    Identifier.fromNamespaceAndPath(
                            Gamefixes.MOD_ID,
                            "cattail"
                    )
            );

    private CattailWorldgen() {
    }

    public static void initialize() {

        Registry.register(
                BuiltInRegistries.FEATURE_TYPE,
                Identifier.fromNamespaceAndPath(
                        Gamefixes.MOD_ID,
                        "cattail"
                ),
                CattailFeature.CODEC
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        Biomes.SWAMP,
                        Biomes.MANGROVE_SWAMP
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                CATTAIL_PLACED_KEY
        );
    }
}