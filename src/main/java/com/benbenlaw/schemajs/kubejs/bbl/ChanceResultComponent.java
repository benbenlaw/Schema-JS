package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.core.recipe.ChanceResult;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

public record ChanceResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ChanceResult> codec) implements RecipeComponent<ChanceResult> {

    public static final ChanceResultComponent CHANCE_RESULT = new ChanceResultComponent(RecipeComponentType.builtin("chance_result"), ChanceResult.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return ItemWrapper.TYPE_INFO;
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ChanceResult value, ReplacementMatchInfo match) {
        if (match.match() instanceof ItemMatch m) {
            ItemStack stack = value.template().create();

            return !stack.isEmpty()
                    && m.matches(cx, stack, match.exact());
        }

        return false;
    }

    @Override
    public boolean isEmpty(ChanceResult value) {
        return value.template().create().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder,ChanceResult value) {
        ItemStack stack = value.template().create();

        if (!stack.isEmpty()) {
            builder.append(stack.typeHolder().getKey());
            builder.append(Float.toString(value.chance()));
        }
    }

    @Override
    public String toString(OpsContainer ops, ChanceResult value) {
        ItemStack stack = value.template().create();

        if (value.chance() >= 1.0F) {
            return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        }

        return BuiltInRegistries.ITEM.getKey(stack.getItem())
                + " @ " + value.chance();
    }

    @Override
    public void validate(RecipeValidationContext ctx,ChanceResult value) {
        ItemStack stack = value.template().create();

        if (stack.isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "Chance result cannot be empty",
                    this,
                    value
            );
        }

        if (value.chance() < 0F || value.chance() > 1F) {
            throw new InvalidRecipeComponentValueException(
                    "Chance must be between 0 and 1",
                    this,
                    value
            );
        }
    }

    @Override
    public ChanceResult wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof ChanceResult cr) {
            return cr;
        }

        if (from instanceof dev.latvian.mods.rhino.NativeArray arr) {

            if (arr.getLength() < 1) {
                throw new IllegalArgumentException("ChanceResult array must have at least 1 element (item)");
            }

            String itemStr = String.valueOf(arr.get(0));

            float chance = 1.0F;
            if (arr.getLength() > 1) {
                chance = Float.parseFloat(String.valueOf(arr.get(1)));
            }

            return new ChanceResult(parseTemplate(itemStr), chance);
        }

        if (from instanceof dev.latvian.mods.rhino.NativeObject obj) {

            Object itemObj = obj.get("item");
            if (itemObj == null) {
                throw new IllegalArgumentException("ChanceResult missing 'item' field");
            }

            String itemStr = String.valueOf(itemObj);

            float chance = 1.0F;
            Object chanceObj = obj.get("chance");
            if (chanceObj != null) {
                chance = Float.parseFloat(String.valueOf(chanceObj));
            }

            return new ChanceResult(parseTemplate(itemStr), chance);
        }

        if (from instanceof String s) {
            return new ChanceResult(parseTemplate(s), 1.0F);
        }

        throw new IllegalArgumentException("Cannot convert to ChanceResult: " + from);
    }

    private ItemStackTemplate parseTemplate(String input) {
        input = input.trim();

        int count = 1;

        if (input.matches("^\\d+x\\s+.+$")) {
            String[] split = input.split("\\s+", 2);
            count = Integer.parseInt(split[0].replace("x", ""));
            input = split[1];
        }

        Identifier id = Identifier.tryParse(input);
        if (id == null) {
            throw new IllegalArgumentException("Invalid item id: " + input);
        }

        String finalInput = input;
        Item item = BuiltInRegistries.ITEM.getOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown item: " + finalInput));

        ItemStack stack = item.getDefaultInstance();
        stack.setCount(count);

        return ItemStackTemplate.fromNonEmptyStack(stack);
    }
}