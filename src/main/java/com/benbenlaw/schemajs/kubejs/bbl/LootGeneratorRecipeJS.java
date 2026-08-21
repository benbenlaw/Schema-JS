package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.schemajs.kubejs.bbl.component.LootRollComponent;
import com.benbenlaw.schemajs.kubejs.util.IdentifierComponent;
import com.benbenlaw.structureloot.recipe.StructureLootRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.Identifier;

import java.util.List;

public interface LootGeneratorRecipeJS {

    RecipeKey<Identifier> LOOT_ID = IdentifierComponent.IDENTIFIER.otherKey("loot_id");
    RecipeKey<List<StructureLootRecipe.LootRoll>> LOOT_TABLES = LootRollComponent.LOOT_ROLL.asList().otherKey("loot_tables");
    RecipeKey<Integer> ROLLS = NumberComponent.INT.otherKey("rolls");
    RecipeKey<Integer> DURATION = NumberComponent.INT.otherKey("duration");
    RecipeKey<Integer> RF_PER_TICK = NumberComponent.INT.otherKey("rf_per_tick");

    RecipeKey<Integer> MAX_DURABILITY = NumberComponent.INT.otherKey("max_durability").functionNames("maxDurability").defaultOptional();
    RecipeKey<Boolean> CAN_BE_OBTAINED = BooleanComponent.BOOLEAN.otherKey("can_be_obtained").functionNames("canBeObtained").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(LOOT_ID, LOOT_TABLES, ROLLS, DURATION, RF_PER_TICK, MAX_DURABILITY, CAN_BE_OBTAINED);

}