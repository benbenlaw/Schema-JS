package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface DryingTableRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<SizedIngredient> INPUT = SizedIngredientComponent.SIZED_INGREDIENT.inputKey("input");

    RecipeKey<FluidStack> FLUID = FluidStackComponent.OPTIONAL_FLUID_STACK.inputKey("fluid").functionNames("fluid").defaultOptional();
    RecipeKey<Integer> CONSUME_AMOUNT = NumberComponent.INT.otherKey("consume_amount").functionNames("consumeAmount").defaultOptional();;

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, FLUID, CONSUME_AMOUNT);

}