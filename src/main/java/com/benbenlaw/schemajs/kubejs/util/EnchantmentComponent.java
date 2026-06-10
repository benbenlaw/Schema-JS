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
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record EnchantmentComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ResourceKey<Enchantment>> codec) implements RecipeComponent<ResourceKey<Enchantment>> {

    public static final EnchantmentComponent ENCHANTMENT = new EnchantmentComponent(RecipeComponentType.builtin("enchantment"), ResourceKey.codec(Registries.ENCHANTMENT));

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof ResourceKey<?>;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ResourceKey<Enchantment> value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof ResourceKey<?> key) {
            return key.equals(value);
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull ResourceKey<Enchantment> value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull ResourceKey<Enchantment> value) {
        builder.append(value.identifier().toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull ResourceKey<Enchantment> value) {
        return value.identifier().toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull ResourceKey<Enchantment> value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "Enchantment cannot be null",
                    this,
                    value
            );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResourceKey<Enchantment> wrap(RecipeScriptContext cx,Object from) {

        if (from instanceof ResourceKey<?> key) {
            return (ResourceKey<Enchantment>) key;
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);

            if (id == null) {
                throw new IllegalArgumentException(
                        "Invalid enchantment id: " + s
                );
            }

            return ResourceKey.create(
                    Registries.ENCHANTMENT,
                    id
            );
        }

        throw new IllegalArgumentException(
                "Cannot convert to Enchantment: " + from
        );
    }
}