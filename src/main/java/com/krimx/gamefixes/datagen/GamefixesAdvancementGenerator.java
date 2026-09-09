package com.krimx.gamefixes.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class GamefixesAdvancementGenerator extends FabricAdvancementProvider {

    protected GamefixesAdvancementGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(
            HolderLookup.Provider wrapperLookup,
            Consumer<AdvancementHolder> consumer
    ) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.CRAFTING_TABLE,
                        Component.literal("Minecraft Rehaul"),
                        Component.literal(""),
                        Identifier.withDefaultNamespace(
                                "gui/advancements/backgrounds/adventure"
                        ),
                        AdvancementType.TASK,
                        false,
                        false,
                        true
                )
                .addCriterion(
                        "root",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.CRAFTING_TABLE
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "root"
                        )
                );

        AdvancementHolder clayFurnace = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.BLAST_FURNACE,
                        Component.literal("Clay Furnace"),
                        Component.literal(
                                "Use 8 clay balls to make a blast furnace"
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "craft_blast_furnace",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.BLAST_FURNACE
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "clay_furnace"
                        )
                );
        AdvancementHolder copperTools = Advancement.Builder.advancement()
                .parent(clayFurnace)
                .display(
                        Items.COPPER_PICKAXE,
                        Component.literal("Tired of using lame, sad metal?"),
                        Component.literal(
                                "Make a copper tool"
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "craft_copper_pickaxe",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.COPPER_PICKAXE
                        )
                )
                .addCriterion(
                        "craft_copper_axe",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.COPPER_AXE
                        )
                )
                .addCriterion(
                        "craft_copper_shovel",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.COPPER_SHOVEL
                        )
                )
                .addCriterion(
                        "craft_copper_hoe",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Items.COPPER_HOE
                        )
                )
                .requirements(
                        AdvancementRequirements.Strategy.OR
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "copper_tools"
                        )
                );
    }
}