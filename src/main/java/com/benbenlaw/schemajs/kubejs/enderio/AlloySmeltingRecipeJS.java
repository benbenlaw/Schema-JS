package com.benbenlaw.schemajs.kubejs.enderio;

import com.benbenlaw.core.recipe.ChanceResult;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface AlloySmeltingRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<List<SizedIngredient>> INPUTS = SizedIngredientComponent.SIZED_INGREDIENT.asList().inputKey("inputs");
    RecipeKey<Integer> ENERGY = NumberComponent.INT.otherKey("energy");
    RecipeKey<Float> EXPERIENCE = NumberComponent.FLOAT.otherKey("experience");
    RecipeKey<Boolean> IS_SMELTING = BooleanComponent.BOOLEAN.otherKey("is_smelting").functionNames("isSmelting").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUTS, ENERGY, EXPERIENCE, IS_SMELTING);

}
