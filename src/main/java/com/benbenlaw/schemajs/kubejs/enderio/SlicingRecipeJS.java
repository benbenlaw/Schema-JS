package com.benbenlaw.schemajs.kubejs.enderio;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface SlicingRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<List<Ingredient>> INPUTS = IngredientComponent.INGREDIENT.asList().inputKey("inputs");
    RecipeKey<Integer> ENERGY = NumberComponent.INT.otherKey("energy");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUTS, ENERGY);

}
