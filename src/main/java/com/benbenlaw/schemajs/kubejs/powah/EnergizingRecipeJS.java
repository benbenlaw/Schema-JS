package com.benbenlaw.schemajs.kubejs.powah;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface EnergizingRecipeJS {

    RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
    RecipeKey<Long> ENERGY = NumberComponent.LONG.otherKey("energy");
    RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.asList().inputKey("ingredients");

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, ENERGY, INGREDIENTS);

}