package com.benbenlaw.schemajs.kubejs.enderio;

import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.storage.fluid_tank.TankRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public interface TankRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");
    RecipeKey<SizedFluidIngredient> FLUID = SizedFluidIngredientComponent.SIZED_FLUID_INGREDIENT.otherKey("fluid");



    RecipeKey<TankRecipe.Mode> MODE = EnumComponent.create(RecipeComponentType.builtin("mode"),
            TankRecipe.Mode.class).otherKey("mode").functionNames("mode").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, FLUID, MODE);

}
