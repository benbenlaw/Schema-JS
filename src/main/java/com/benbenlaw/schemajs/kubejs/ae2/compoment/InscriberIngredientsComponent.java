package com.benbenlaw.schemajs.kubejs.ae2.compoment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Wraps [top?, middle, bottom?] as a list in KubeJS scripts but serializes
 * to AE2's expected { top, middle, bottom } ingredient object structure.
 *
 * List ordering: [top, middle, bottom]
 *   1 element  → middle only
 *   2 elements → top + middle
 *   3 elements → top + middle + bottom
 */
public record InscriberIngredientsComponent(ResourceKey<RecipeComponentType<?>> type,
        Codec<InscriberIngredients> codec
) implements RecipeComponent<InscriberIngredientsComponent.InscriberIngredients> {

    /**
     * The serialized form matching AE2's Ingredients record codec exactly.
     */
    public record InscriberIngredients(
            Optional<Ingredient> top,
            Ingredient middle,
            Optional<Ingredient> bottom) {

        public static final Codec<InscriberIngredients> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Ingredient.CODEC.optionalFieldOf("top").forGetter(InscriberIngredients::top),
                Ingredient.CODEC.fieldOf("middle").forGetter(InscriberIngredients::middle),
                Ingredient.CODEC.optionalFieldOf("bottom").forGetter(InscriberIngredients::bottom))
                .apply(builder, InscriberIngredients::new));
    }

    public static final InscriberIngredientsComponent INSTANCE = new InscriberIngredientsComponent(
            RecipeComponentType.builtin("inscriber_ingredients"),
            InscriberIngredients.CODEC
    );

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(List.class);
    }

    @Override
    public boolean isEmpty(InscriberIngredients value) {
        return value == null || value.middle().isEmpty();
    }

    @Override
    public boolean hasPriority(@NonNull RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof List || from instanceof InscriberIngredients;
    }

    @Override
    public boolean matches(@NonNull RecipeMatchContext cx, @NonNull InscriberIngredients value, @NonNull ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, InscriberIngredients value) {
        value.top().ifPresent(t -> builder.append("top"));
        builder.append("middle");
        value.bottom().ifPresent(b -> builder.append("bottom"));
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, InscriberIngredients value) {
        return "[top=" + value.top().isPresent() + ", middle, bottom=" + value.bottom().isPresent() + "]";
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull InscriberIngredients value) {
        if (isEmpty(value)) {
            throw new InvalidRecipeComponentValueException(
                    "InscriberRecipe requires at least a middle ingredient", this, null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull InscriberIngredients wrap(@NonNull RecipeScriptContext cx, Object from) {
        if (from instanceof InscriberIngredients i) return i;

        if (from instanceof List<?> raw) {
            List<Object> list = (List<Object>) raw;

            if (list.isEmpty()) {
                throw new IllegalArgumentException("InscriberRecipe ingredients list cannot be empty");
            }

            Optional<Ingredient> top = Optional.empty();
            Ingredient middle;
            Optional<Ingredient> bottom = Optional.empty();

            switch (list.size()) {
                case 1 -> {
                    middle = IngredientComponent.INGREDIENT.wrap(cx, list.get(0));
                }
                case 2 -> {
                    top = wrapOptional(cx, list.get(0));
                    middle = IngredientComponent.INGREDIENT.wrap(cx, list.get(1));
                }
                default -> {
                    top = wrapOptional(cx, list.get(0));
                    middle = IngredientComponent.INGREDIENT.wrap(cx, list.get(1));
                    bottom = wrapOptional(cx, list.get(2));
                }
            }

            return new InscriberIngredients(top, middle, bottom);
        }

        // Single ingredient = middle only
        Ingredient middle = IngredientComponent.INGREDIENT.wrap(cx, from);
        return new InscriberIngredients(Optional.empty(), middle, Optional.empty());
    }

    private static Optional<Ingredient> wrapOptional(RecipeScriptContext cx, Object from) {
        if (from == null) return Optional.empty();
        Ingredient ingredient = IngredientComponent.INGREDIENT.wrap(cx, from);
        if (ingredient == null || ingredient.isEmpty()) return Optional.empty();
        return Optional.of(ingredient);
    }
}
