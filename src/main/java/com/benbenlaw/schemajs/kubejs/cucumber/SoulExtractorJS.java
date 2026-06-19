package com.benbenlaw.schemajs.kubejs.cucumber;

import com.blakebr0.cucumber.crafting.OutputResolver;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface SoulExtractorJS {

    RecipeKey<SoulExtractionResultComponent.Result> RESULT = SoulExtractionResultComponent.SOUL_RESULT.outputKey("result");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INPUT);

}