package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.schemajs.kubejs.bbl.component.BlockTargetComponent;
import com.benbenlaw.schemajs.kubejs.util.CompoundTagComponent;
import com.benbenlaw.schemajs.kubejs.util.EntityTypeComponent;
import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.TemperatureValues;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SummoningRecipeJS {

    RecipeKey<EntityType<?>> ENTITY = EntityTypeComponent.ENTITY_TYPE.inputKey("entity");
    RecipeKey<SizedIngredient> INPUT = SizedIngredientComponent.SIZED_INGREDIENT.inputKey("input");
    RecipeKey<BlockTarget> BELOW_BLOCK = BlockTargetComponent.BLOCK_TARGET.inputKey("below_block");
    RecipeKey<CompoundTag> ENTITY_DATA = CompoundTagComponent.COMPOUND_TAG.inputKey("entity_data").functionNames("entityData").defaultOptional();
    RecipeKey<TemperatureValues> TEMPERATURE_VARIANT = EnumComponent.create(RecipeComponentType.builtin("temperature_variant"),
            TemperatureValues.class).otherKey("temperature_variant").functionNames("temperatureVariant").defaultOptional();


    RecipeSchema SCHEMA = new RecipeSchema(ENTITY, INPUT, BELOW_BLOCK, ENTITY_DATA, TEMPERATURE_VARIANT);

}
