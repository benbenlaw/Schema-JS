package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.schemajs.kubejs.bbl.component.ChanceResultComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedFluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface StrainerRecipeJS {

    RecipeKey<ChanceResult> RESULT = ChanceResultComponent.CHANCE_RESULT.outputKey("result");
    RecipeKey<SizedIngredient> INPUT = SizedIngredientComponent.SIZED_INGREDIENT.inputKey("input");
    RecipeKey<Integer> MIN_MESH_TIER = NumberComponent.INT.otherKey("min_mesh_tier");
    RecipeKey<Float> ADDITIONAL_CHANCE_PER_TIER = NumberComponent.FLOAT.otherKey("additional_chance_per_tier");

    RecipeKey<SizedFluidIngredient> FLUID = SizedFluidIngredientComponent.SIZED_FLUID_INGREDIENT.inputKey("fluid").functionNames("fluid").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INPUT, MIN_MESH_TIER, ADDITIONAL_CHANCE_PER_TIER, FLUID);

}
