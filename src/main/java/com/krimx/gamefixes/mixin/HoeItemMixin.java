package com.krimx.gamefixes.mixin;

import com.krimx.gamefixes.Gamefixes;
import com.krimx.gamefixes.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Redirect(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"
            )
    )
    private void gamefixes$specialFarmland(
            Consumer<UseOnContext> action,
            Object contextObject
    ) {
        UseOnContext context = (UseOnContext) contextObject;
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

        boolean canBecomeFarmland =
                context.getLevel()
                        .getBlockState(context.getClickedPos())
                        .is(Blocks.GRASS_BLOCK)
                        || context.getLevel()
                        .getBlockState(context.getClickedPos())
                        .is(Blocks.DIRT_PATH)
                        || context.getLevel()
                        .getBlockState(context.getClickedPos())
                        .is(Blocks.DIRT);

        if (canBecomeFarmland && hasHydration) {
            context.getLevel().setBlock(
                    context.getClickedPos(),
                    Gamefixes.HYDRATED_FARMLAND.defaultBlockState(),
                    11
            );

            context.getLevel().gameEvent(
                    GameEvent.BLOCK_CHANGE,
                    context.getClickedPos(),
                    GameEvent.Context.of(
                            context.getPlayer(),
                            Gamefixes.HYDRATED_FARMLAND.defaultBlockState()
                    )
            );

            return;
        }

        if (canBecomeFarmland && hasAbundance) {
            context.getLevel().setBlock(
                    context.getClickedPos(),
                    Gamefixes.ABUNDANT_FARMLAND.defaultBlockState(),
                    11
            );

            context.getLevel().gameEvent(
                    GameEvent.BLOCK_CHANGE,
                    context.getClickedPos(),
                    GameEvent.Context.of(
                            context.getPlayer(),
                            Gamefixes.ABUNDANT_FARMLAND.defaultBlockState()
                    )
            );

            return;
        }

        action.accept(context);
    }
}