package com.benbenlaw.schemajs.kubejs.enderio;

import com.benbenlaw.schemajs.kubejs.util.BlockTagComponent;
import com.benbenlaw.schemajs.kubejs.util.DimensionComponent;
import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface FireCraftingRecipeJS {

    RecipeKey<List<FireCraftingRecipe.Result>> RESULTS = FireCraftingResultComponent.FIRE_CRAFTING_RESULT.asList().otherKey("results");
    RecipeKey<List<ResourceKey<Level>>> DIMENSIONS = DimensionComponent.DIMENSION.asList().otherKey("dimensions").functionNames("dimensions");
    RecipeKey<List<Block>> BASES = BlockComponent.BLOCK.asList().otherKey("base_blocks").defaultOptional().functionNames("baseBlocks");
    RecipeKey<List<TagKey<Block>>> BASE_TAGS = BlockTagComponent.BLOCK_TAG.asList().otherKey("base_tags").defaultOptional().functionNames("baseTags");
    RecipeKey<Block> BLOCK_AFTER_BURNING = BlockComponent.BLOCK.otherKey("block_after_burning").defaultOptional().functionNames("blockAfterBurning");

    RecipeSchema SCHEMA = new RecipeSchema(RESULTS, DIMENSIONS, BASES, BASE_TAGS, BLOCK_AFTER_BURNING);

}