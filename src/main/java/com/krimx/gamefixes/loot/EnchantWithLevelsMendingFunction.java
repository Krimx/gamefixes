package com.krimx.gamefixes.loot;

import com.krimx.gamefixes.Gamefixes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;

import java.util.List;
import java.util.Optional;

public class EnchantWithLevelsMendingFunction extends LootItemConditionalFunction {

    public static final MapCodec<EnchantWithLevelsMendingFunction> MAP_CODEC =
            RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(
                    NumberProviders.CODEC.fieldOf("levels").forGetter((f) -> f.levels),
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT)
                            .optionalFieldOf("options")
                            .forGetter((f) -> f.options),
                    Codec.BOOL.optionalFieldOf("include_additional_cost_component", false)
                            .forGetter((f) -> f.includeAdditionalCostComponent)
            )).apply(i, EnchantWithLevelsMendingFunction::new));

    private final NumberProvider levels;
    private final Optional<HolderSet<Enchantment>> options;
    private final boolean includeAdditionalCostComponent;

    private EnchantWithLevelsMendingFunction(
            List<LootItemCondition> predicates,
            NumberProvider levels,
            Optional<HolderSet<Enchantment>> options,
            boolean includeAdditionalCostComponent
    ) {
        super(predicates);
        this.levels = levels;
        this.options = options;
        this.includeAdditionalCostComponent = includeAdditionalCostComponent;
    }

    @Override
    public MapCodec<EnchantWithLevelsMendingFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        RandomSource random = context.getRandom();
        RegistryAccess registryAccess = context.getLevel().registryAccess();
        int enchantmentCost = this.levels.getInt(context);

        Gamefixes.setMendingAllowed(true);

        try {
            ItemStack result = EnchantmentHelper.enchantItem(
                    random,
                    itemStack,
                    enchantmentCost,
                    registryAccess,
                    this.options
            );

            if (this.includeAdditionalCostComponent
                    && context.hasParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)
                    && !result.isEmpty()
                    && enchantmentCost > 0) {
                result.set(
                        DataComponents.ADDITIONAL_TRADE_COST,
                        enchantmentCost
                );
            }

            return result;
        } finally {
            Gamefixes.setMendingAllowed(false);
        }
    }
}