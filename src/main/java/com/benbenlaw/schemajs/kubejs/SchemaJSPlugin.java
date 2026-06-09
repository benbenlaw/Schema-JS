package com.benbenlaw.schemajs.kubejs;

import com.benbenlaw.schemajs.kubejs.bbl.*;
import com.benbenlaw.schemajs.kubejs.util.CompoundTagComponent;
import com.benbenlaw.schemajs.kubejs.util.EntityTypeComponent;
import com.benbenlaw.strainers.Strainers;
import com.benbenlaw.utility.Utility;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import net.neoforged.fml.ModList;

public class SchemaJSPlugin implements KubeJSPlugin {

    public static EventGroup GROUP = EventGroup.of("SchemaJSCompatEvents");


    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {

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
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {

        if (ModList.get().isLoaded("bblcore")) {
            registry.unit(ChanceResultComponent.CHANCE_RESULT);
        }

        if (ModList.get().isLoaded("utility")) {
            registry.unit(BlockTargetComponent.BLOCK_TARGET);
        }

        registry.unit(CompoundTagComponent.COMPOUND_TAG);
        registry.unit(EntityTypeComponent.ENTITY_TYPE);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }
}