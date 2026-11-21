package com.benbenlaw.schemajs.kubejs.rifts;

import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.rifts.recipe.CrusherRecipe.BonusOutput;
import com.benbenlaw.rifts.recipe.util.BlockStateResult;
import com.benbenlaw.rifts.recipe.util.BlockTarget;
import com.benbenlaw.schemajs.kubejs.util.DimensionComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

/**
 * Schemas for the BBL Rifts recipe types.
 */
public interface RiftsSchemas {

    RecipeKey<Integer> PROCESSING_TIME = NumberComponent.INT.otherKey("processing_time").functionNames("processingTime");
    RecipeKey<Integer> RIFT_ENERGY = NumberComponent.INT.otherKey("rift_energy_per_tick").functionNames("riftEnergyPerTick");

    /** crusher(output, ingredient, processingTime, riftEnergyPerTick).bonus(["#c:dusts/copper", 0.5]) */
    RecipeSchema CRUSHER = new RecipeSchema(
            SizedIngredientComponent.SIZED_INGREDIENT.outputKey("output"),
            SizedIngredientComponent.SIZED_INGREDIENT.inputKey("ingredient"),
            PROCESSING_TIME,
            RIFT_ENERGY,
            RiftsBonusOutputComponent.BONUS_OUTPUT.otherKey("bonus").functionNames("bonus").defaultOptional());

    /** infuser(output, ingredients, riftEnergyPerTick) */
    RecipeSchema INFUSER = new RecipeSchema(
            ItemStackComponent.ITEM_STACK.outputKey("output"),
            SizedIngredientComponent.SIZED_INGREDIENT.asList().inputKey("ingredients"),
            RIFT_ENERGY);

    /** quarry(focus, results, processingTime, riftEnergyPerTick), results are [item, weight] pairs */
    RecipeSchema QUARRY = new RecipeSchema(
            SizedIngredientComponent.SIZED_INGREDIENT.inputKey("focus"),
            RiftsQuarryResultComponent.QUARRY_RESULT.asList().outputKey("results"),
            PROCESSING_TIME,
            RIFT_ENERGY);

    /** converter(input, output, processingTime, riftEnergyPerTick).fromDimension(..).toDimension(..).ignoreBlockState(true) */
    RecipeSchema CONVERTER = new RecipeSchema(
            RiftsBlockTargetComponent.BLOCK_TARGET.inputKey("input"),
            RiftsBlockStateResultComponent.BLOCK_STATE_RESULT.outputKey("output"),
            PROCESSING_TIME,
            RIFT_ENERGY,
            BooleanComponent.BOOLEAN.otherKey("ignore_block_state").functionNames("ignoreBlockState").defaultOptional(),
            DimensionComponent.DIMENSION.otherKey("from_dimension").functionNames("fromDimension").defaultOptional(),
            DimensionComponent.DIMENSION.otherKey("to_dimension").functionNames("toDimension").defaultOptional());
}
