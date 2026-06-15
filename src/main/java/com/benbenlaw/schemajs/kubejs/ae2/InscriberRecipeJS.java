package com.benbenlaw.schemajs.kubejs.ae2;

import appeng.recipes.handlers.InscriberProcessType;
import com.benbenlaw.schemajs.kubejs.ae2.compoment.InscriberIngredientsComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;

public interface InscriberRecipeJS {

    RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.outputKey("result");
    RecipeKey<InscriberIngredientsComponent.InscriberIngredients> INGREDIENTS = InscriberIngredientsComponent.INSTANCE.inputKey("ingredients");
    RecipeKey<InscriberProcessType> MODE = EnumComponent
            .create(RecipeComponentType.builtin("inscriber_mode"), InscriberProcessType.class)
            .otherKey("mode");

    RecipeSchema SCHEMA = new RecipeSchema(RESULT, INGREDIENTS, MODE);
}