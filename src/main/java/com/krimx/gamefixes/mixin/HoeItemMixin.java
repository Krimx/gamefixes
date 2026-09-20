package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class HoeItemMixin {

    @Inject(
            method = "useOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gamefixes$specialFarmland(
            UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack stack = context.getItemInHand();

        var enchantmentsRegistry =
                context.getLevel()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> abundance =
                enchantmentsRegistry.getOrThrow(
                        ModEnchantments.ABUNDANCE
                );

        Holder<Enchantment> hydration =
                enchantmentsRegistry.getOrThrow(
                        ModEnchantments.HYDRATION
                );

        ItemEnchantments enchantments =
                stack.getOrDefault(
                        DataComponents.ENCHANTMENTS,
                        ItemEnchantments.EMPTY
                );

        boolean hasAbundance =
                enchantments.getLevel(abundance) > 0;

        boolean hasHydration =
                enchantments.getLevel(hydration) > 0;

        if (!hasAbundance && !hasHydration) {
            return;
        }

        boolean isHoe =
                stack.is(net.minecraft.tags.ItemTags.HOES);

        if (!isHoe) {
            return;
        }

        var level = context.getLevel();
        var pos = context.getClickedPos();
        var currentState = level.getBlockState(pos);

        boolean canBecomeFarmland =
                currentState.is(Blocks.GRASS_BLOCK)
                        || currentState.is(Blocks.DIRT_PATH)
                        || currentState.is(Blocks.DIRT)
                        || currentState.is(Blocks.FARMLAND)
                        || currentState.is(Gamefixes.ABUNDANT_FARMLAND)
                        || currentState.is(Gamefixes.HYDRATED_FARMLAND);

        if (!canBecomeFarmland) {
            return;
        }

        var farmland = hasHydration
                ? Gamefixes.HYDRATED_FARMLAND.defaultBlockState()
                : Gamefixes.ABUNDANT_FARMLAND.defaultBlockState();

        /*
         * Preserve the current moisture level when converting
         * between farmland types.
         *
         * Dirt, grass, and dirt paths don't have a moisture property,
         * so they simply use the default state of the new farmland.
         */
        if (currentState.getBlock() instanceof FarmlandBlock
                && farmland.hasProperty(FarmlandBlock.MOISTURE)) {

            farmland = farmland.setValue(
                    FarmlandBlock.MOISTURE,
                    currentState.getValue(FarmlandBlock.MOISTURE)
            );
        }

        /*
         * If the hoe is being used on farmland of the same type,
         * there is nothing to change.
         */
        if (currentState.is(Gamefixes.ABUNDANT_FARMLAND)
                && hasAbundance) {
            return;
        }

        if (currentState.is(Gamefixes.HYDRATED_FARMLAND)
                && hasHydration) {
            return;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, farmland, 11);

            level.gameEvent(
                    GameEvent.BLOCK_CHANGE,
                    pos,
                    GameEvent.Context.of(
                            context.getPlayer(),
                            farmland
                    )
            );
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}