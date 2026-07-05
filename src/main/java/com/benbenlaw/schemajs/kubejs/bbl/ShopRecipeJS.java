package com.benbenlaw.schemajs.kubejs.bbl;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.benbenlaw.schemajs.kubejs.bbl.ResourceGeneratorRecipeJS.LEFT_FLUID;

public interface ShopRecipeJS {

    RecipeKey<ItemStack> ITEM = ItemStackComponent.ITEM_STACK.inputKey("item");
    RecipeKey<Integer> BUY_PRICE = NumberComponent.INT.otherKey("buy_price");
    RecipeKey<Integer> SELL_PRICE = NumberComponent.INT.otherKey("sell_price");

    RecipeKey<String> TIER = StringComponent.OPTIONAL_STRING.otherKey("tier").functionNames("tier").defaultOptional();
    RecipeKey<Integer> ORDER = NumberComponent.INT.otherKey("order").functionNames("order").defaultOptional();
    RecipeKey<Integer> TRADER = NumberComponent.INT.otherKey("trader").functionNames("trader").defaultOptional();

    RecipeSchema SCHEMA = new RecipeSchema(ITEM, BUY_PRICE, SELL_PRICE, TIER, ORDER, TRADER);

}
