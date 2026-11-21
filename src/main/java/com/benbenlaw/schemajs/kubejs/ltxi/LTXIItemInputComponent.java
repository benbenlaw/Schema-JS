package com.benbenlaw.schemajs.kubejs.ltxi;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import liedge.limacore.recipe.input.RecipeItemInput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * LTXI {@link RecipeItemInput}. Accepts:
 * <pre>
 * "4x minecraft:diamond"              // always consumed
 * ["4x minecraft:diamond", 0.5]       // 50% chance to be consumed (0 = catalyst)
 * {ingredient: "#c:ingots", consume_chance: 0.5}
 * </pre>
 */
public record LTXIItemInputComponent(ResourceKey<RecipeComponentType<?>> type, Codec<RecipeItemInput> codec) implements RecipeComponent<RecipeItemInput> {

    public static final LTXIItemInputComponent ITEM_INPUT = new LTXIItemInputComponent(RecipeComponentType.builtin("ltxi_item_input"), RecipeItemInput.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof List || from instanceof Map || from instanceof RecipeItemInput;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, RecipeItemInput value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(RecipeItemInput value) {
        return value.ingredient().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, RecipeItemInput value) {
        value.ingredient().items().findFirst().ifPresent(h -> h.unwrapKey().ifPresent(builder::append));
    }

    @Override
    public String toString(OpsContainer ops, RecipeItemInput value) {
        String text = value.count() + "x " + value.ingredient();
        return value.consumeChance() >= 1.0F ? text : text + " @ " + value.consumeChance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, RecipeItemInput value) {
        if (value.count() < 1) {
            throw new InvalidRecipeComponentValueException("Item input count must be at least 1", this, value);
        }
        if (value.consumeChance() < 0F || value.consumeChance() > 1F) {
            throw new InvalidRecipeComponentValueException("Consume chance must be between 0 and 1", this, value);
        }
    }

    @Override
    public RecipeItemInput wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof RecipeItemInput input) {
            return input;
        }

        Object ingredient = from;
        float consumeChance = 1.0F;

        if (from instanceof List<?> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("LTXI item input array must have at least 1 element (ingredient)");
            }
            ingredient = list.get(0);
            consumeChance = LTXIArgs.toFloat(LTXIArgs.get(list, 1), 1.0F);
        } else if (from instanceof Map<?, ?> map) {
            ingredient = LTXIArgs.get(map, "ingredient", "item");
            if (ingredient == null) {
                throw new IllegalArgumentException("LTXI item input missing 'ingredient' field");
            }
            consumeChance = LTXIArgs.toFloat(LTXIArgs.get(map, "consume_chance", "consumeChance", "chance"), 1.0F);
        }

        SizedIngredient sized = SizedIngredientComponent.SIZED_INGREDIENT.wrap(cx, ingredient);
        return new RecipeItemInput(sized.ingredient(), sized.count(), consumeChance);
    }
}
