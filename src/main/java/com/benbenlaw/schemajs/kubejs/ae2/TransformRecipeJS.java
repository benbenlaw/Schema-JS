package com.benbenlaw.schemajs.kubejs.ae2;

import appeng.recipes.transform.TransformCircumstance;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.TransformCircumstanceComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface TransformRecipeJS {

    RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
    RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.asList().inputKey("ingredients");
    RecipeKey<TransformCircumstance> CIRCUMSTANCE = TransformCircumstanceComponent.INSTANCE.otherKey("circumstance").functionNames("circumstance").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENTS, CIRCUMSTANCE);

}