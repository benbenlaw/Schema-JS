package com.benbenlaw.schemajs.kubejs.ltxi;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.SizedFluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import liedge.limacore.recipe.input.RecipeFluidInput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * LTXI {@link RecipeFluidInput}. Accepts:
 * <pre>
 * "1000x minecraft:water"
 * ["1000x #minecraft:water", 0]       // 0 = never consumed
 * {ingredient: "1000x minecraft:water", consume_chance: 0.5}
 * </pre>
 */
public record LTXIFluidInputComponent(ResourceKey<RecipeComponentType<?>> type, Codec<RecipeFluidInput> codec) implements RecipeComponent<RecipeFluidInput> {

    public static final LTXIFluidInputComponent FLUID_INPUT = new LTXIFluidInputComponent(RecipeComponentType.builtin("ltxi_fluid_input"), RecipeFluidInput.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof List || from instanceof Map || from instanceof RecipeFluidInput;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, RecipeFluidInput value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(RecipeFluidInput value) {
        return value.count() <= 0;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, RecipeFluidInput value) {
        builder.append(value.ingredient().toString());
    }

    @Override
    public String toString(OpsContainer ops, RecipeFluidInput value) {
        String text = value.count() + "x " + value.ingredient();
        return value.consumeChance() >= 1.0F ? text : text + " @ " + value.consumeChance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, RecipeFluidInput value) {
        if (value.count() < 1) {
            throw new InvalidRecipeComponentValueException("Fluid input amount must be at least 1", this, value);
        }
        if (value.consumeChance() < 0F || value.consumeChance() > 1F) {
            throw new InvalidRecipeComponentValueException("Consume chance must be between 0 and 1", this, value);
        }
    }

    @Override
    public RecipeFluidInput wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof RecipeFluidInput input) {
            return input;
        }

        Object ingredient = from;
        float consumeChance = 1.0F;

        if (from instanceof List<?> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("LTXI fluid input array must have at least 1 element (ingredient)");
            }
            ingredient = list.get(0);
            consumeChance = LTXIArgs.toFloat(LTXIArgs.get(list, 1), 1.0F);
        } else if (from instanceof Map<?, ?> map) {
            ingredient = LTXIArgs.get(map, "ingredient", "fluid");
            if (ingredient == null) {
                throw new IllegalArgumentException("LTXI fluid input missing 'ingredient' field");
            }
            consumeChance = LTXIArgs.toFloat(LTXIArgs.get(map, "consume_chance", "consumeChance", "chance"), 1.0F);
        }

        SizedFluidIngredient sized = SizedFluidIngredientComponent.SIZED_FLUID_INGREDIENT.wrap(cx, ingredient);
        return new RecipeFluidInput(sized.ingredient(), sized.amount(), consumeChance);
    }
}
