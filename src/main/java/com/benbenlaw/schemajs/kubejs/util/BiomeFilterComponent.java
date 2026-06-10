package com.benbenlaw.schemajs.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

public record BiomeFilterComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Optional<TagKey<Biome>>> codec) implements RecipeComponent<Optional<TagKey<Biome>>> {

    public static final Codec<Optional<TagKey<Biome>>> CODEC =
            Codec.STRING.xmap(
                    BiomeFilterComponent::parseOptional,
                    opt -> opt.map(tag -> tag.location().toString()).orElse(null)
            );

    public static final BiomeFilterComponent BIOME_FILTER =
            new BiomeFilterComponent(
                    RecipeComponentType.builtin("biome_filter"),
                    CODEC
            );

    private static Optional<TagKey<Biome>> parseOptional(String s) {
        if (s == null || s.isEmpty()) {
            return Optional.empty();
        }

        if (s.startsWith("#")) {
            return Optional.of(TagKey.create(
                    Registries.BIOME,
                    Identifier.parse(s.substring(1))
            ));
        }

        return Optional.of(TagKey.create(
                Registries.BIOME,
                Identifier.parse(s)
        ));
    }

    @Override
    public Codec<Optional<TagKey<Biome>>> codec() {
        return codec;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from == null;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Optional<TagKey<Biome>> value, ReplacementMatchInfo match) {
        return true;
    }

    @Override
    public boolean isEmpty(@NonNull Optional<TagKey<Biome>> value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull Optional<TagKey<Biome>> value) {
        value.ifPresent(tag -> builder.append(tag.location().toString()));
    }

    @Override
    public String toString(@NonNull OpsContainer ops, @NonNull Optional<TagKey<Biome>> value) {
        return value.map(tag -> tag.location().toString()).orElse("any");
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Optional<TagKey<Biome>> value) {
    }

    @Override
    public Optional<TagKey<Biome>> wrap(RecipeScriptContext cx, Object from) {

        if (from == null) return Optional.empty();

        if (from instanceof String s) {
            return parseOptional(s);
        }

        throw new IllegalArgumentException("Invalid biome: " + from);
    }
}