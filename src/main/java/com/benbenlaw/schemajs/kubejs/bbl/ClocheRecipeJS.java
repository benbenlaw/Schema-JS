package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.ListRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface ClocheRecipeJS {

    RecipeKey<List<ChanceResult>> RESULTS = ChanceResultComponent.CHANCE_RESULT.asList().outputKey("results");
    RecipeKey<Ingredient> SEED = IngredientComponent.INGREDIENT.inputKey("seed");
    RecipeKey<Ingredient> SOIL = IngredientComponent.INGREDIENT.inputKey("soil");
    RecipeKey<Integer> DURATION = NumberComponent.INT.otherKey("duration");
    RecipeKey<Ingredient> CATALYST = IngredientComponent.OPTIONAL_INGREDIENT.otherKey("catalyst").functionNames("catalyst").defaultOptional();
    RecipeKey<ItemStack> SHEARS_RESULT = ItemStackComponent.OPTIONAL_ITEM_STACK.outputKey("shears_result").functionNames("shearsResult").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, SEED, SOIL, DURATION, CATALYST, SHEARS_RESULT);

}