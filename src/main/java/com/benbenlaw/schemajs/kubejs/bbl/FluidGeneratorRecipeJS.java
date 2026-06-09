package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FluidGeneratorRecipeJS {

    RecipeKey<FluidStack> OUTPUT = FluidStackComponent.FLUID_STACK.inputKey("output");
    RecipeKey<String> INPUT = StringComponent.ID.inputKey("input");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT);

}
