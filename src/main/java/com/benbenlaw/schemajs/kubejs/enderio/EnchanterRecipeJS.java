package com.benbenlaw.schemajs.kubejs.enderio;

import com.benbenlaw.schemajs.kubejs.util.EnchantmentComponent;
import com.benbenlaw.schemajs.kubejs.util.EnchantmentHolderComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface EnchanterRecipeJS {

    RecipeKey<Holder<Enchantment>> ENCHANTMENT = EnchantmentHolderComponent.ENCHANTMENT_HOLDER.otherKey("enchantment");
    RecipeKey<Integer> COST_MULTIPLIER = NumberComponent.POSITIVE_INT.otherKey("cost_multiplier");
    RecipeKey<SizedIngredient> INPUT = SizedIngredientComponent.SIZED_INGREDIENT.inputKey("input");

    RecipeSchema SCHEMA = new RecipeSchema(ENCHANTMENT, COST_MULTIPLIER, INPUT);

}
