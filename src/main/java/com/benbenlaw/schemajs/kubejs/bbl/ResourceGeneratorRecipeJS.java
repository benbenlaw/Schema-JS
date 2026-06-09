package com.benbenlaw.schemajs.kubejs.bbl;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface ResourceGeneratorRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.inputKey("output");
    RecipeKey<ItemStack> INPUT = ItemStackComponent.ITEM_STACK.inputKey("input");
    RecipeKey<FluidStack> LEFT_FLUID = FluidStackComponent.FLUID_STACK.inputKey("left_fluid");
    RecipeKey<FluidStack> RIGHT_FLUID = FluidStackComponent.FLUID_STACK.inputKey("right_fluid");
    RecipeKey<Boolean> CONSUME_LEFT = BooleanComponent.BOOLEAN.inputKey("consume_left");
    RecipeKey<Boolean> CONSUME_RIGHT = BooleanComponent.BOOLEAN.inputKey("consume_right");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, LEFT_FLUID, RIGHT_FLUID, CONSUME_LEFT, CONSUME_RIGHT);

}
