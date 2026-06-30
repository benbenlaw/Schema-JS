package com.benbenlaw.schemajs.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record DimensionComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ResourceKey<Level>> codec) implements RecipeComponent<ResourceKey<Level>> {

    public static final DimensionComponent DIMENSION = new DimensionComponent(RecipeComponentType.builtin("dimension"), ResourceKey.codec(Registries.DIMENSION));

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof ResourceKey<?>;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ResourceKey<Level> value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof ResourceKey<?> key) {
            return key.equals(value);
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull ResourceKey<Level> value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull ResourceKey<Level> value) {
        builder.append(value.identifier().toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull ResourceKey<Level> value) {
        return value.identifier().toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull ResourceKey<Level> value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "Dimension cannot be null",
                    this,
                    value
            );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResourceKey<Level> wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof ResourceKey<?> key) {
            return (ResourceKey<Level>) key;
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);

            if (id == null) {
                throw new IllegalArgumentException(
                        "Invalid dimension id: " + s
                );
            }

            return ResourceKey.create(
                    Registries.DIMENSION,
                    id
            );
        }

        throw new IllegalArgumentException(
                "Cannot convert to Dimension: " + from
        );
    }
}