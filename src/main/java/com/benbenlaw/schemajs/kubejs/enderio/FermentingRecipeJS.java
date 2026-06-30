package com.benbenlaw.schemajs.kubejs.enderio;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface FermentingRecipeJS {

    RecipeKey<FluidStack> OUTPUT = FluidStackComponent.FLUID_STACK.outputKey("output");
    RecipeKey<SizedFluidIngredient> INPUT = SizedFluidIngredientComponent.SIZED_FLUID_INGREDIENT.inputKey("input");
    RecipeKey<TagKey<Item>> FIRST_REAGENT = TagKeyComponent.ITEM.otherKey("first_reagent");
    RecipeKey<TagKey<Item>> SECOND_REAGENT = TagKeyComponent.ITEM.otherKey("second_reagent");
    RecipeKey<Integer> TICKS = NumberComponent.INT.outputKey("ticks");

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, FIRST_REAGENT, SECOND_REAGENT, TICKS);

}
