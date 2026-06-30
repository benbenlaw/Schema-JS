package com.benbenlaw.schemajs.kubejs.enderio;

import com.enderio.enderio.content.machines.obelisks.weather.WeatherChangeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface WeatherChangeRecipeJS {

    RecipeKey<FluidStack> FLUID = FluidStackComponent.FLUID_STACK.inputKey("fluid");
    RecipeKey< WeatherChangeRecipe.WeatherMode> MODE = EnumComponent.create(RecipeComponentType.builtin("mode"), WeatherChangeRecipe.WeatherMode.class).otherKey("mode");

    RecipeSchema SCHEMA = new RecipeSchema(FLUID, MODE);

}
