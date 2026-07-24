package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.schemajs.kubejs.util.IdentifierComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface StructureLootRecipeJS {

    RecipeKey<Identifier> STRUCTURE = IdentifierComponent.IDENTIFIER.otherKey("STRUCTURE");
    RecipeKey<List<Identifier>> LOOT_TABLES = IdentifierComponent.IDENTIFIER.asList().otherKey("loot_tables");
    RecipeKey<Integer> ROLLS = NumberComponent.INT.otherKey("rolls");
    RecipeKey<Integer> DURATION = NumberComponent.INT.otherKey("duration");
    RecipeKey<Integer> RF_PER_TICK = NumberComponent.INT.otherKey("rf_per_tick");

    RecipeSchema SCHEMA = new RecipeSchema(STRUCTURE, LOOT_TABLES, ROLLS, DURATION, RF_PER_TICK);

}