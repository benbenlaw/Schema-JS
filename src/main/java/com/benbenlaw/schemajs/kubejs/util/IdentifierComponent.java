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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record IdentifierComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Identifier> codec) implements RecipeComponent<Identifier> {

    public static final IdentifierComponent IDENTIFIER = new IdentifierComponent(RecipeComponentType.builtin("identifier"), Identifier.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof Identifier || from instanceof String;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Identifier value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof Identifier id) {
            return id.equals(value);
        }

        if (m instanceof String s) {
            Identifier id = Identifier.tryParse(s);
            return id != null && id.equals(value);
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull Identifier value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull Identifier value) {
        builder.append(value.toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull Identifier value) {
        return value.toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Identifier value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "Identifier cannot be null",
                    this,
                    value
            );
        }
    }

    @Override
    public Identifier wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof Identifier id) {
            return id;
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);

            if (id == null) {
                throw new IllegalArgumentException(
                        "Invalid identifier: " + s
                );
            }

            return id;
        }

        throw new IllegalArgumentException(
                "Cannot convert to Identifier: " + from
        );
    }
}