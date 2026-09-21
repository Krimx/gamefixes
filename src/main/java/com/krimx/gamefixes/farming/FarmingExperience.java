package com.krimx.gamefixes.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FarmingExperience {

    private static final ResourceKey<Enchantment> FORTUNE =
            ResourceKey.create(
                    Registries.ENCHANTMENT,
                    Identifier.parse("minecraft:fortune")
            );

    private FarmingExperience() {
    }

    public static void award(
            Level level,
            BlockPos pos,
            ItemStack tool
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Holder<Enchantment> fortune =
                serverLevel.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(FORTUNE);

        ItemEnchantments enchantments =
                tool.getOrDefault(
                        DataComponents.ENCHANTMENTS,
                        ItemEnchantments.EMPTY
                );

        int fortuneLevel =
                Math.min(
                        enchantments.getLevel(fortune),
                        3
                );

        int experience = 1 + fortuneLevel;

        ExperienceOrb.award(
                serverLevel,
                Vec3.atCenterOf(pos),
                experience
        );
    }
}
