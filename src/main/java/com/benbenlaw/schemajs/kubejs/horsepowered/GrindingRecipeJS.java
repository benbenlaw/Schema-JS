package com.benbenlaw.schemajs.kubejs.horsepowered;

import com.breakinblocks.horsepowered.recipes.RecipeTier;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;

public interface GrindingRecipeJS {

    RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
    RecipeKey<Ingredient> INGREDIENT = IngredientComponent.INGREDIENT.inputKey("ingredient");
    RecipeKey<Integer> TIME = NumberComponent.INT.otherKey("time");
    RecipeKey<Integer> PRIORITY = NumberComponent.INT.otherKey("priority")
            .functionNames("priority")
            .optional(0)
            .alwaysWrite();
    RecipeKey<Float> HUNGER_COST = NumberComponent.FLOAT.otherKey("hungerCost")
            .functionNames("hungerCost")
            .optional(0.0F)
            .alwaysWrite();
    RecipeKey<RecipeTier> TIER = EnumComponent.create(RecipeComponentType.builtin("recipe_tier"), RecipeTier.class)
            .otherKey("tier")
            .functionNames("mode")
            .optional(RecipeTier.ANY);

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENT, TIME, PRIORITY, HUNGER_COST, TIER);
}