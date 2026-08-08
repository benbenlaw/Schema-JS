package com.benbenlaw.schemajs.kubejs.bbl.component;

import com.benbenlaw.bbltcg.recipe.WeightedItemStack;
import com.benbenlaw.bbltcg.util.CardRarity;
import com.benbenlaw.core.recipe.ChanceResult;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

public record WeightedItemStackComponent(ResourceKey<RecipeComponentType<?>> type, Codec<WeightedItemStack> codec) implements RecipeComponent<WeightedItemStack> {

    public static final WeightedItemStackComponent WEIGHTED_ITEM_STACK_COMPONENT = new WeightedItemStackComponent(RecipeComponentType.builtin("weight_item_stack"), WeightedItemStack.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return ItemWrapper.TYPE_INFO;
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, WeightedItemStack value, ReplacementMatchInfo match) {
        Object rawMatch = match.match();
        if (rawMatch instanceof ItemMatch m) {
            ItemStack stack = value.getStack().create();
            return !stack.isEmpty() && m.matches(cx, stack, match.exact());
        }
        return false;
    }

    @Override
    public boolean isEmpty(WeightedItemStack value) {
        return value.getStack().create().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, WeightedItemStack value) {
        ItemStack stack = value.getStack().create();
        if (!stack.isEmpty()) {
            builder.append(stack.typeHolder().getKey());
            builder.append(Integer.toString(value.getWeight()));
            builder.append(value.getCardRarity().name());
        }
    }

    @Override
    public String toString(OpsContainer ops, WeightedItemStack value) {
        ItemStack stack = value.getStack().create();
        return BuiltInRegistries.ITEM.getKey(stack.getItem())
                + " (weight=" + value.getWeight()
                + ", rarity=" + value.getCardRarity().name() + ")";
    }

    @Override
    public void validate(RecipeValidationContext ctx, WeightedItemStack value) throws InvalidRecipeComponentValueException {
        if (value.getStack().create().isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "WeightedItemStack item cannot be empty", this, value);
        }
        if (value.getWeight() <= 0) {
            throw new InvalidRecipeComponentValueException(
                    "WeightedItemStack weight must be greater than 0", this, value);
        }
    }

    @Override
    public WeightedItemStack wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof WeightedItemStack w) {
            return w;
        }

        if (from instanceof NativeArray arr) {
            if (arr.getLength() < 1) {
                throw new IllegalArgumentException("WeightedItemStack array must have at least 1 element (item)");
            }

            Object first = arr.get(0);
            ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, first);
            int weight = arr.getLength() > 1 ? ((Number) arr.get(1)).intValue() : 1;
            CardRarity rarity = arr.getLength() > 2
                    ? CardRarity.valueOf(String.valueOf(arr.get(2)).toUpperCase())
                    : CardRarity.COMMON;

            return new WeightedItemStack(ItemStackTemplate.fromNonEmptyStack(stack), weight, rarity);
        }

        if (from instanceof NativeObject obj) {
            Object itemObj = obj.get("item");
            if (itemObj == null) {
                throw new IllegalArgumentException("WeightedItemStack missing 'item' field");
            }

            ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, itemObj);
            int weight = obj.get("weight") != null
                    ? ((Number) obj.get("weight")).intValue() : 1;
            CardRarity rarity = obj.get("rarity") != null
                    ? CardRarity.valueOf(String.valueOf(obj.get("rarity")).toUpperCase())
                    : CardRarity.COMMON;

            return new WeightedItemStack(ItemStackTemplate.fromNonEmptyStack(stack), weight, rarity);
        }

        if (from instanceof String) {
            ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, from);
            return new WeightedItemStack(ItemStackTemplate.fromNonEmptyStack(stack), 1, CardRarity.COMMON);
        }

        throw new IllegalArgumentException("Cannot convert to WeightedItemStack: " + from);
    }
}
