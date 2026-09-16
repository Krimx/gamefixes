package com.krimx.gamefixes.worldgen;

import net.fabricmc.api.ModInitializer;

public class CattailWorldgenInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        CattailWorldgen.initialize();
    }
}