package com.benbenlaw.schemajs.kubejs.enderio;

import com.enderio.enderio.content.fire_crafting.FireCraftingRecipe.Result;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
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

public record FireCraftingResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Result> codec) implements RecipeComponent<Result> {

    public static final FireCraftingResultComponent FIRE_CRAFTING_RESULT = new FireCraftingResultComponent(RecipeComponentType.builtin("fire_crafting_result"), Result.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof Result || from instanceof NativeArray || from instanceof NativeObject
                || from instanceof ItemStack || from instanceof String;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Result value, ReplacementMatchInfo match) {
        if (match.match() instanceof ItemMatch m) {
            ItemStack stack = value.result().create();
            return !stack.isEmpty() && m.matches(cx, stack, match.exact());
        }
        return false;
    }

    @Override
    public boolean isEmpty(Result value) {
        return value.result().create().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, Result value) {
        ItemStack stack = value.result().create();
        if (!stack.isEmpty()) {
            builder.append(stack.typeHolder().getKey());
            builder.append(Float.toString(value.chance()));
        }
    }

    @Override
    public String toString(OpsContainer ops, Result value) {
        ItemStack stack = value.result().create();
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return id + " x[" + value.minCount() + "-" + value.maxCount() + "] @ " + value.chance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, Result value) {
        if (value.result().create().isEmpty()) {
            throw new InvalidRecipeComponentValueException("Fire crafting result cannot be empty", this, value);
        }
        if (value.chance() < 0F || value.chance() > 1F) {
            throw new InvalidRecipeComponentValueException("Chance must be between 0 and 1", this, value);
        }
        if (value.minCount() > value.maxCount()) {
            throw new InvalidRecipeComponentValueException("min_count cannot exceed max_count", this, value);
        }
    }

    @Override
    public Result wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof Result r) {
            return r;
        }

        if (from instanceof NativeArray arr) {
            if (arr.getLength() < 1) {
                throw new IllegalArgumentException("Fire crafting result array must have at least 1 element (item)");
            }

            Object itemObj = arr.get(0);
            int minCount = arr.getLength() > 1 ? (int) Double.parseDouble(String.valueOf(arr.get(1))) : 1;
            int maxCount = arr.getLength() > 2 ? (int) Double.parseDouble(String.valueOf(arr.get(2))) : minCount;
            float chance = arr.getLength() > 3 ? Float.parseFloat(String.valueOf(arr.get(3))) : 1.0F;

            return build(cx, itemObj, minCount, maxCount, chance);
        }

        if (from instanceof NativeObject obj) {
            Object itemObj = obj.get("item");
            if (itemObj == null) {
                throw new IllegalArgumentException("Fire crafting result missing 'item' field");
            }

            int minCount = obj.get("min_count") instanceof Number n ? n.intValue() : 1;
            int maxCount = obj.get("max_count") instanceof Number n ? n.intValue() : minCount;
            float chance = obj.get("chance") instanceof Number n ? n.floatValue() : 1.0F;

            return build(cx, itemObj, minCount, maxCount, chance);
        }

        return build(cx, from, 1, 1, 1.0F);
    }

    private static Result build(RecipeScriptContext cx, Object itemObj, int minCount, int maxCount, float chance) {
        ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, itemObj);
        ItemStackTemplate template = ItemStackTemplate.fromNonEmptyStack(stack);
        return new Result(template, minCount, maxCount, chance);
    }
}