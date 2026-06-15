package com.benbenlaw.schemajs.kubejs.ae2;

import appeng.recipes.entropy.EntropyMode;
import appeng.recipes.entropy.EntropyRecipe;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.EntropyInputComponent;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.EntropyOutputComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface EntropyRecipeJS {

    RecipeKey<EntropyRecipe.Output> OUTPUT = EntropyOutputComponent.INSTANCE.outputKey("output");
    RecipeKey<EntropyRecipe.Input> INPUT = EntropyInputComponent.INSTANCE.inputKey("input");
    RecipeKey<EntropyMode> MODE = EnumComponent.create(RecipeComponentType.builtin("mode"), EntropyMode.class).otherKey("mode");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, MODE);

}