package com.krimx.gamefixes.datagen;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.krimx.gamefixes.advancement.ModCriteria;
import com.krimx.gamefixes.advancement.ResearchCriterion;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import com.krimx.gamefixes.Gamefixes;

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

        AdvancementHolder wingweave = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Gamefixes.WINGWEAVE,
                        Component.literal("This sheep jumped over the moon"),
                        Component.literal(
                                "What could a shepherd possibly do with phantom membrane?"
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "research_wingweave",
                        ModCriteria.RESEARCH.createCriterion(
                                new ResearchCriterion.Conditions(
                                        Optional.empty(),
                                        "minecraft:shepherd/wingweave"
                                )
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "wingweave"
                        )
                );

        AdvancementHolder ghastResin = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.GHAST_TEAR,
                        Component.literal("Rapid Polymerization"),
                        Component.literal(
                                "What happens when you try to brew resin?"
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "brew_ghast_resin",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.GHAST_RESIN
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "ghast_resin"
                        )
                );

        AdvancementHolder diakrete = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.BREEZE_ROD,
                        Component.literal("Project Habakkuk"),
                        Component.literal(
                                "Show the cleric how to make a very light diamond."
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "research_diakrete",
                        ModCriteria.RESEARCH.createCriterion(
                                new ResearchCriterion.Conditions(
                                        Optional.empty(),
                                        "minecraft:cleric/diakrete"
                                )
                        )
                )
                .rewards(
                        new AdvancementRewards.Builder()
                                .addRecipe(
                                        RecipeBuilder.getDefaultRecipeId(
                                                new ItemStackTemplate(
                                                        Gamefixes.DIAKRETE_HELMET
                                                )
                                        )
                                )
                                .addRecipe(
                                        RecipeBuilder.getDefaultRecipeId(
                                                new ItemStackTemplate(
                                                        Gamefixes.DIAKRETE_CHESTPLATE
                                                )
                                        )
                                )
                                .addRecipe(
                                        RecipeBuilder.getDefaultRecipeId(
                                                new ItemStackTemplate(
                                                        Gamefixes.DIAKRETE_LEGGINGS
                                                )
                                        )
                                )
                                .addRecipe(
                                        RecipeBuilder.getDefaultRecipeId(
                                                new ItemStackTemplate(
                                                        Gamefixes.DIAKRETE_BOOTS
                                                )
                                        )
                                )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "diakrete"
                        )
                );

        AdvancementHolder cheese = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Gamefixes.CHEESE,
                        Component.literal("Grate Expectations"),
                        Component.literal(
                                "Heat up a cauldron and put some milk in it."
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "make_cheese",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.CHEESE
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "cheese"
                        )
                );

        AdvancementHolder cheeseWheel = Advancement.Builder.advancement()
                .parent(cheese)
                .display(
                        Gamefixes.CHEESE_WHEEL,
                        Component.literal("Aged to prerfection!"),
                        Component.literal(
                                "Combine cheese slices into a cheese wheel"
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "make_cheese_wheel",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.CHEESE_WHEEL
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "cheese_wheel"
                        )
                );

        AdvancementHolder twilightPrismarine = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.CONDUIT,
                        Component.literal("Twilight Zone"),
                        Component.literal(
                                "Discover the transformative properties of the conduit."
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "get_twilight_prismarine",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.TWILIGHT_PRISMARINE
                        )
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "twilight_prismarine"
                        )
                );

        AdvancementHolder diakreteArmor = Advancement.Builder.advancement()
                .parent(diakrete)
                .display(
                        Gamefixes.DIAKRETE_CHESTPLATE,
                        Component.literal("Light Work"),
                        Component.literal(
                                "Make a piece of diakrete armor."
                        ),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion(
                        "craft_diakrete_helmet",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.DIAKRETE_HELMET
                        )
                )
                .addCriterion(
                        "craft_diakrete_chestplate",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.DIAKRETE_CHESTPLATE
                        )
                )
                .addCriterion(
                        "craft_diakrete_leggings",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.DIAKRETE_LEGGINGS
                        )
                )
                .addCriterion(
                        "craft_diakrete_boots",
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                Gamefixes.DIAKRETE_BOOTS
                        )
                )
                .requirements(
                        AdvancementRequirements.Strategy.OR
                )
                .save(
                        consumer,
                        Identifier.fromNamespaceAndPath(
                                "gamefixes",
                                "diakrete_armor"
                        )
                );
    }
}