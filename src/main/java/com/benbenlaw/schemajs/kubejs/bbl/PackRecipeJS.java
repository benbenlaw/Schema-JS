package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.bbltcg.recipe.WeightedItemStack;
import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.schemajs.kubejs.bbl.component.ChanceResultComponent;
import com.benbenlaw.schemajs.kubejs.bbl.component.WeightedItemStackComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface PackRecipeJS {

    RecipeKey<List<WeightedItemStack>> CARDS = WeightedItemStackComponent.WEIGHTED_ITEM_STACK_COMPONENT.asList().outputKey("cards");
    RecipeKey<Ingredient> CARD_PACK = IngredientComponent.INGREDIENT.inputKey("card_pack");
    RecipeKey<Integer> AMOUNT_OF_CARDS = NumberComponent.INT.otherKey("amount_of_cards");

    RecipeSchema SCHEMA = new RecipeSchema(CARDS, CARD_PACK, AMOUNT_OF_CARDS);

}