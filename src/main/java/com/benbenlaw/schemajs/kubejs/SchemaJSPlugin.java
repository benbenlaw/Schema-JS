package com.benbenlaw.schemajs.kubejs;

import com.benbenlaw.cloche.Cloche;
import com.benbenlaw.schemajs.kubejs.ae2.*;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.EntropyOutputComponent;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.InscriberIngredientsComponent;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.TransformCircumstanceComponent;
import com.benbenlaw.schemajs.kubejs.bbl.*;
import com.benbenlaw.schemajs.kubejs.cucumber.*;
import com.benbenlaw.schemajs.kubejs.powah.EnergizingRecipeJS;
import com.benbenlaw.schemajs.kubejs.util.*;
import com.benbenlaw.strainers.Strainers;
import com.benbenlaw.utility.Utility;
import com.blakebr0.cucumber.event.RecipeManagerLoadingEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;

public class SchemaJSPlugin implements KubeJSPlugin {

    public static EventGroup GROUP = EventGroup.of("SchemaJSCompatEvents");
    public static EventHandler MA_SEED_CRAFTING;

    static {
        if (ModList.get().isLoaded("mysticalagriculture")) {
            MA_SEED_CRAFTING = GROUP.server("mysticalAgricultureSeeds", () -> MASeedCraftingEventJS.class);
        }
    }


    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {

        if (ModList.get().isLoaded("cloche")) {
            event.register(Cloche.identifier("cloche"), ClocheRecipeJS.SCHEMA);
        }

        //BBL Strainers
        if (ModList.get().isLoaded("strainers")) {
            event.register(Strainers.identifier("strainer"), StrainerRecipeJS.SCHEMA);
        }

        //BBL Utility
        if (ModList.get().isLoaded("utility")) {
            event.register(Utility.identifier("drying_table"), DryingTableRecipeJS.SCHEMA);
            event.register(Utility.identifier("fluid_generator"), FluidGeneratorRecipeJS.SCHEMA);
            event.register(Utility.identifier("resource_generator"), ResourceGeneratorRecipeJS.SCHEMA);
            event.register(Utility.identifier("summoning"), SummoningRecipeJS.SCHEMA);
        }

        //AE2
        if (ModList.get().isLoaded("ae2")) {
            event.register(Identifier.fromNamespaceAndPath("ae2", "inscriber"), InscriberRecipeJS.SCHEMA);
            event.register(Identifier.fromNamespaceAndPath("ae2", "charger"), ChargerRecipeJS.SCHEMA);
            event.register(Identifier.fromNamespaceAndPath("ae2", "entropy"), EntropyRecipeJS.SCHEMA);
            event.register(Identifier.fromNamespaceAndPath("ae2", "matter_cannon"), MatterCannonAmmoJS.SCHEMA);
            event.register(Identifier.fromNamespaceAndPath("ae2", "transform"), TransformRecipeJS.SCHEMA);
        }

        //Mystical
        if (ModList.get().isLoaded("mysticalagriculture")) {
            event.register(Identifier.fromNamespaceAndPath("mysticalagriculture", "soul_extraction"), SoulExtractorJS.SCHEMA);
            event.register(Identifier.fromNamespaceAndPath("mysticalagriculture", "ore_infusion"), OreInfusionJS.SCHEMA);
        }

        //Powah
        if (ModList.get().isLoaded("powah")) {
            event.register(Identifier.fromNamespaceAndPath("powah", "energizing"), EnergizingRecipeJS.SCHEMA);
        }

    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {

        if (ModList.get().isLoaded("bblcore")) {
            registry.unit(ChanceResultComponent.CHANCE_RESULT);
        }

        if (ModList.get().isLoaded("utility")) {
            registry.unit(BlockTargetComponent.BLOCK_TARGET);
        }

        if (ModList.get().isLoaded("cucumber")) {
            registry.unit(OutputResolverComponent.OUTPUT_RESOLVER);
            if (ModList.get().isLoaded("mysticalagriculture")) {
                registry.unit(SoulExtractionResultComponent.SOUL_RESULT);
            }
        }

        if (ModList.get().isLoaded("ae2")) {
            registry.unit(EntropyOutputComponent.INSTANCE);
            registry.unit(EntropyOutputComponent.INSTANCE);
            registry.unit(TransformCircumstanceComponent.INSTANCE);
            registry.unit(InscriberIngredientsComponent.INSTANCE);
        }

        registry.unit(CompoundTagComponent.COMPOUND_TAG);
        registry.unit(EntityTypeComponent.ENTITY_TYPE);
        registry.unit(EnchantmentComponent.ENCHANTMENT);
        registry.unit(WeightedEntityListComponent.ENTITY_LIST);
        registry.unit(BiomeFilterComponent.BIOME_FILTER);
    }


    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {

        if (ModList.get().isLoaded("strainers")) {
            registry.of(Registries.ITEM, r -> r.add(Strainers.identifier("strainers_drop"), DropItemBuilder.class, DropItemBuilder::new ));
        }

        if (ModList.get().isLoaded("infinitystorage")) {
            registry.of(Registries.ITEM, r -> r.add(Strainers.identifier("infinity_drive"), InfinityDriveBuilder.class, InfinityDriveBuilder::new ));
        }
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        if (ModList.get().isLoaded("mysticalagriculture")) {
            MASeedRecipeOverrides.clear();
            MA_SEED_CRAFTING.post(ScriptType.SERVER, new MASeedCraftingEventJS());
        }
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }
}