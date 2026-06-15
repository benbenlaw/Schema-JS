package com.benbenlaw.schemajs.kubejs.ae2;

import appeng.recipes.entropy.EntropyMode;
import appeng.recipes.entropy.EntropyRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.crafting.Ingredient;

public interface MatterCannonAmmoJS {

    RecipeKey<Ingredient> AMMO = IngredientComponent.INGREDIENT.inputKey("ammo");
    RecipeKey<Float> WEIGHT = NumberComponent.FLOAT.otherKey("weight");

    RecipeSchema SCHEMA = new RecipeSchema(AMMO, WEIGHT);

}