package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OreFeature.class)
public abstract class BiomeSpecificOreMixin {

    @Unique
    private static final ThreadLocal<WorldGenLevel> GAMEFIXES_LEVEL =
            new ThreadLocal<>();

    @Unique
    private static final ThreadLocal<BlockPos> GAMEFIXES_ORE_POS =
            new ThreadLocal<>();

    /*
     * Store the world being used for this ore-generation pass.
     */
    @Inject(
            method = "doPlace",
            at = @At("HEAD")
    )
    private void gamefixes$storeWorldGenContext(
            WorldGenLevel level,
            RandomSource random,
            OreConfiguration config,
            double x0,
            double x1,
            double z0,
            double z1,
            double y0,
            double y1,
            int xStart,
            int yStart,
            int zStart,
            int sizeXZ,
            int sizeY,
            CallbackInfoReturnable<Boolean> cir
    ) {
        GAMEFIXES_LEVEL.set(level);
        GAMEFIXES_ORE_POS.remove();
    }

    /*
     * There should only be ONE redirect of this call on OreFeature.
     *
     * The previous Pink Diamond, Rose Gold, and Yellow Diamond
     * mixins each tried to redirect this same call independently.
     * That caused the Yellow Diamond mixin to fail its injection
     * check once the target was already being handled elsewhere.
     *
     * This single mixin stores the current ore position for all
     * three custom ore checks.
     */
    @Redirect(
            method = "doPlace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
            )
    )
    private BlockPos.MutableBlockPos gamefixes$storeOrePosition(
            BlockPos.MutableBlockPos pos,
            int x,
            int y,
            int z
    ) {
        GAMEFIXES_ORE_POS.set(new BlockPos(x, y, z));

        return pos.set(x, y, z);
    }

    /*
     * Replace vanilla diamond and gold target states according
     * to the biome rules for the custom ores.
     */
    @Redirect(
            method = "doPlace",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration$TargetBlockState;state:Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState gamefixes$replaceCustomOre(
            OreConfiguration.TargetBlockState targetState
    ) {
        BlockState originalState = targetState.state;

        WorldGenLevel level = GAMEFIXES_LEVEL.get();
        BlockPos orePos = GAMEFIXES_ORE_POS.get();

        if (level == null || orePos == null) {
            return originalState;
        }

        /*
         * =========================================================
         * DIAMOND -> PINK / YELLOW
         * =========================================================
         */
        if (originalState.is(Blocks.DIAMOND_ORE)
                || originalState.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {

            /*
             * Find the world-generation surface at this X/Z.
             *
             * This lets Pink Diamond and Yellow Diamond use the
             * surface biome even though the actual ore is underground.
             */
            int surfaceY = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    orePos.getX(),
                    orePos.getZ()
            );

            BlockPos surfacePos = new BlockPos(
                    orePos.getX(),
                    surfaceY,
                    orePos.getZ()
            );

            /*
             * Pink Diamond:
             * Any biome included in #minecraft:is_mountain.
             */
            boolean surfaceIsMountain =
                    level.getBiome(surfacePos).is(BiomeTags.IS_MOUNTAIN);

            /*
             * Yellow Diamond:
             *
             * - Surface biome is Swamp
             * - OR surface biome is Mangrove Swamp
             * - OR current underground biome is Sulfur Caves
             */
            boolean surfaceIsSwamp =
                    level.getBiome(surfacePos).is(Biomes.SWAMP)
                            || level.getBiome(surfacePos).is(Biomes.MANGROVE_SWAMP);

            boolean currentIsSulfurCaves =
                    level.getBiome(orePos).is(Biomes.SULFUR_CAVES);

            /*
             * Pink Diamond takes priority if a biome somehow satisfies
             * both rules. Otherwise Yellow Diamond is checked.
             */
            if (surfaceIsMountain) {
                System.out.println("Should generate pink diamond");

                if (originalState.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                    return Gamefixes.DEEPSLATE_PINK_DIAMOND_ORE.defaultBlockState();
                }

                return Gamefixes.PINK_DIAMOND_ORE.defaultBlockState();
            }

            if (surfaceIsSwamp || currentIsSulfurCaves) {
                System.out.println("Should generate yellow diamond");

                if (originalState.is(Blocks.DEEPSLATE_DIAMOND_ORE)) {
                    return Gamefixes.DEEPSLATE_YELLOW_DIAMOND_ORE.defaultBlockState();
                }

                return Gamefixes.YELLOW_DIAMOND_ORE.defaultBlockState();
            }

            return originalState;
        }

        /*
         * =========================================================
         * GOLD -> ROSE GOLD
         * =========================================================
         */
        if (originalState.is(Blocks.GOLD_ORE)
                || originalState.is(Blocks.DEEPSLATE_GOLD_ORE)) {

            /*
             * Rose Gold uses the surface biome.
             *
             * #minecraft:is_badlands covers the vanilla Badlands,
             * Eroded Badlands, and Wooded Badlands biomes.
             */
            int surfaceY = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    orePos.getX(),
                    orePos.getZ()
            );

            BlockPos surfacePos = new BlockPos(
                    orePos.getX(),
                    surfaceY,
                    orePos.getZ()
            );

            if (level.getBiome(surfacePos).is(BiomeTags.IS_BADLANDS)) {
                System.out.println("Should generate rose gold");

                if (originalState.is(Blocks.DEEPSLATE_GOLD_ORE)) {
                    return Gamefixes.DEEPSLATE_ROSE_GOLD_ORE.defaultBlockState();
                }

                return Gamefixes.ROSE_GOLD_ORE.defaultBlockState();
            }

            return originalState;
        }

        /*
         * =========================================================
         * COAL -> CHARCOAL
         * =========================================================
         */
        if (originalState.is(Blocks.COAL_ORE)
                || originalState.is(Blocks.DEEPSLATE_COAL_ORE)) {

            /*
             * Rose Gold uses the surface biome.
             *
             * #minecraft:is_badlands covers the vanilla Badlands,
             * Eroded Badlands, and Wooded Badlands biomes.
             */
            int surfaceY = level.getHeight(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    orePos.getX(),
                    orePos.getZ()
            );

            BlockPos surfacePos = new BlockPos(
                    orePos.getX(),
                    surfaceY,
                    orePos.getZ()
            );

            if (level.getBiome(surfacePos).is(Biomes.PALE_GARDEN)) {
                System.out.println("Should generate charcoal ore");

                if (originalState.is(Blocks.DEEPSLATE_COAL_ORE)) {
                    return Gamefixes.DEEPSLATE_CHARCOAL_ORE.defaultBlockState();
                }

                return Gamefixes.CHARCOAL_ORE.defaultBlockState();
            }

            return originalState;
        }

        /*
         * Every other OreFeature target remains untouched.
         */
        return originalState;
    }

    /*
     * Clear the thread-local state when this ore feature finishes.
     */
    @Inject(
            method = "doPlace",
            at = @At("RETURN")
    )
    private void gamefixes$clearWorldGenContext(
            WorldGenLevel level,
            RandomSource random,
            OreConfiguration config,
            double x0,
            double x1,
            double z0,
            double z1,
            double y0,
            double y1,
            int xStart,
            int yStart,
            int zStart,
            int sizeXZ,
            int sizeY,
            CallbackInfoReturnable<Boolean> cir
    ) {
        GAMEFIXES_LEVEL.remove();
        GAMEFIXES_ORE_POS.remove();
    }
}