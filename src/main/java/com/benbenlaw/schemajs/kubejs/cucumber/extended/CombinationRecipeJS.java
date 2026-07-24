package com.benbenlaw.schemajs.kubejs.cucumber.extended;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface CombinationRecipeJS {

    RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");
    RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.asList().inputKey("ingredients");
    RecipeKey<Integer> POWER_COST = NumberComponent.INT.otherKey("power_cost");
    RecipeKey<Integer> POWER_RATE = NumberComponent.INT.otherKey("power_rate").functionNames("powerRate").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INPUT, INGREDIENTS, POWER_COST, POWER_RATE);

}