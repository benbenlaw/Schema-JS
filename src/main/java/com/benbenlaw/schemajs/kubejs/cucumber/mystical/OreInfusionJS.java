package com.benbenlaw.schemajs.kubejs.cucumber.mystical;

import com.benbenlaw.schemajs.kubejs.cucumber.OutputResolverComponent;
import com.blakebr0.cucumber.crafting.OutputResolver;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface OreInfusionJS {

    RecipeKey<OutputResolver> RESULTS = OutputResolverComponent.OUTPUT_RESOLVER.outputKey("result");
    RecipeKey<List<SizedIngredient>> INGREDIENTS = SizedIngredientComponent.SIZED_INGREDIENT.asList().inputKey("ingredients");

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, INGREDIENTS);

}