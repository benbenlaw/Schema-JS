package com.benbenlaw.schemajs.kubejs.cucumber.mystical;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.crafting.Ingredient;

public interface SoulExtractorJS {

    RecipeKey<SoulExtractionResultComponent.Result> RESULT = SoulExtractionResultComponent.SOUL_RESULT.outputKey("result");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INPUT);

}