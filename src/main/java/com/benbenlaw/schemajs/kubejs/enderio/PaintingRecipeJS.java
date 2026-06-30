package com.benbenlaw.schemajs.kubejs.enderio;

import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface PaintingRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT);

}
