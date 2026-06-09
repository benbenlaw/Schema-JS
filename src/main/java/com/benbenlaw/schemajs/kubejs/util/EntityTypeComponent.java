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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record EntityTypeComponent(ResourceKey<RecipeComponentType<?>> type, Codec<EntityType<?>> codec) implements RecipeComponent<EntityType<?>> {

    public static final EntityTypeComponent ENTITY_TYPE = new EntityTypeComponent(RecipeComponentType.builtin("entity_type"), BuiltInRegistries.ENTITY_TYPE.byNameCodec());

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof EntityType<?> || from instanceof String;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, EntityType<?> value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof EntityType<?> et) {
            return et == value;
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull EntityType<?> value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull EntityType<?> value) {
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(value);
        builder.append(id.toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull EntityType<?> value) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(value).toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull EntityType<?> value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "EntityType cannot be null",
                    this,
                    value
            );
        }
    }

    @SuppressWarnings("unchecked")
    public EntityType<?> wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof EntityType<?> et) {
            return et;
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);
            if (id == null) {
                throw new IllegalArgumentException("Invalid entity id: " + s);
            }

            return BuiltInRegistries.ENTITY_TYPE.getOptional(id)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown entity: " + s));
        }

        throw new IllegalArgumentException("Cannot convert to EntityType: " + from);
    }
}