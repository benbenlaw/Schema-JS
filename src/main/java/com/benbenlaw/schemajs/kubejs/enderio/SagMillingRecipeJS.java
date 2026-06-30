package com.benbenlaw.schemajs.kubejs.enderio;

import com.benbenlaw.schemajs.kubejs.util.EnchantmentComponent;
import com.benbenlaw.utility.util.TemperatureValues;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface SagMillingRecipeJS {

    RecipeKey<List<SagMillingRecipe.OutputItem>> OUTPUTS = OutputItemComponent.OUTPUT_ITEM.asList().outputKey("outputs");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");
    RecipeKey<Integer> ENERGY = NumberComponent.INT.otherKey("energy");

    RecipeKey<SagMillingRecipe.BonusType> BONUS = EnumComponent.create(RecipeComponentType.builtin("bonus"),
            SagMillingRecipe.BonusType.class).otherKey("bonus").functionNames("bonus").defaultOptional();


    RecipeSchema SCHEMA = new RecipeSchema(OUTPUTS, INPUT, ENERGY, BONUS);

}
