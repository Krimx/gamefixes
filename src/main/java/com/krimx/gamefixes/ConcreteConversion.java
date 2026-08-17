package com.krimx.gamefixes;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

public final class ConcreteConversion {

    private ConcreteConversion() {
    }

    public static void initialize() {
        ServerTickEvents.END_LEVEL_TICK.register(
                ConcreteConversion::convertConcretePowder
        );
    }

    private static void convertConcretePowder(ServerLevel level) {

        for (Entity entity : level.getAllEntities()) {

            if (!(entity instanceof ItemEntity itemEntity)) {
                continue;
            }

            if (!isInWater(itemEntity)
                    && !isInWaterCauldron(itemEntity)) {
                continue;
            }

            ItemStack stack = itemEntity.getItem();

            if (stack.isEmpty()) {
                continue;
            }

            ItemStack concrete = getConcrete(stack);

            if (concrete.isEmpty()) {
                continue;
            }

            concrete.setCount(stack.getCount());

            itemEntity.setItem(concrete);
        }
    }

    private static boolean isInWater(ItemEntity entity) {

        BlockPos pos = BlockPos.containing(
                entity.getX(),
                entity.getY(),
                entity.getZ()
        );

        return entity.level()
                .getBlockState(pos)
                .is(Blocks.WATER);
    }

    private static boolean isInWaterCauldron(ItemEntity entity) {

        BlockPos pos = BlockPos.containing(
                entity.getX(),
                entity.getY(),
                entity.getZ()
        );

        return entity.level()
                .getBlockState(pos)
                .is(Blocks.WATER_CAULDRON);
    }

    private static ItemStack getConcrete(ItemStack powder) {

        Identifier id =
                BuiltInRegistries.ITEM.getKey(
                        powder.getItem()
                );

        if (!id.getNamespace().equals("minecraft")) {
            return ItemStack.EMPTY;
        }

        String path = id.getPath();

        String suffix = "_concrete_powder";

        if (!path.endsWith(suffix)) {
            return ItemStack.EMPTY;
        }

        String concretePath =
                path.substring(
                        0,
                        path.length() - suffix.length()
                ) + "_concrete";

        Identifier concreteId =
                Identifier.fromNamespaceAndPath(
                        "minecraft",
                        concretePath
                );

        var concreteItem =
                BuiltInRegistries.ITEM.getValue(
                        concreteId
                );

        if (concreteItem == null) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(concreteItem);
    }
}