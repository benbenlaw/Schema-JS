package com.benbenlaw.schemajs.kubejs.enderio;

import com.benbenlaw.schemajs.kubejs.util.IdentifierComponent;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public interface SoulBindingRecipeJS {

    RecipeKey<ItemStack> OUTPUT = ItemStackComponent.ITEM_STACK.outputKey("output");
    RecipeKey<Ingredient> INPUT = IngredientComponent.INGREDIENT.inputKey("input");
    RecipeKey<Integer> ENERGY = NumberComponent.INT.otherKey("energy");
    RecipeKey<Integer> EXPERIENCE = NumberComponent.INT.otherKey("experience");

    RecipeKey<Identifier> ENTITY_TYPE = IdentifierComponent.IDENTIFIER.otherKey("entity_type").functionNames("entityType").defaultOptional();
    RecipeKey<MobCategory> MOB_CATEGORY = EnumComponent.create(RecipeComponentType.builtin("mob_category"),
            MobCategory.class).otherKey("mob_category").functionNames("mobCategory").defaultOptional();
    RecipeKey<String> SOUL_DATA = StringComponent.STRING.otherKey("soul_data").functionNames("soulData").defaultOptional();
    RecipeKey<Boolean> COPY_INPUT_COMPONENTS = BooleanComponent.BOOLEAN.otherKey("copy_input_components").functionNames("copyInputComponents").defaultOptional();


    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, ENERGY, EXPERIENCE, ENTITY_TYPE, MOB_CATEGORY, SOUL_DATA, COPY_INPUT_COMPONENTS);

}
