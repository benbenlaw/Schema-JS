package com.benbenlaw.schemajs.kubejs.ae2.compoment;

import appeng.recipes.transform.TransformCircumstance;
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
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public record TransformCircumstanceComponent(
        ResourceKey<RecipeComponentType<?>> type,
        Codec<TransformCircumstance> codec
) implements RecipeComponent<TransformCircumstance> {

    public static final TransformCircumstanceComponent INSTANCE = new TransformCircumstanceComponent(
            RecipeComponentType.builtin("transform_circumstance"),
            TransformCircumstance.CODEC
    );

    @Override
    public @NonNull TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean isEmpty(@NonNull TransformCircumstance value) {
        return false;
    }

    @Override
    public boolean hasPriority(@NonNull RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof Map || from instanceof TransformCircumstance;
    }

    @Override
    public boolean matches(@NonNull RecipeMatchContext cx, @NonNull TransformCircumstance value, ReplacementMatchInfo match) {
        Object m = match.match();
        if (m instanceof TransformCircumstance other) {
            return value.equals(other);
        }
        return false;
    }

    @Override
    public void buildUniqueId(@NonNull UniqueIdBuilder builder, TransformCircumstance value) {
        if (value.isExplosion()) {
            builder.append("explosion");
        } else if (value.isFluid()) {
            builder.append("fluid_" + encodeTag(value));
        }
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, TransformCircumstance value) {
        if (value.isExplosion()) return "explosion";
        return "#" + encodeTag(value);
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull TransformCircumstance value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "TransformCircumstance cannot be null",
                    this,
                    null
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull TransformCircumstance wrap(@NonNull RecipeScriptContext cx, Object from) {
        if (from instanceof TransformCircumstance t) {
            return t;
        }

        if (from instanceof String s) {
            s = s.trim();

            if (s.equalsIgnoreCase("explosion")) {
                return TransformCircumstance.explosion();
            }

            if (s.startsWith("#")) {
                Identifier id = Identifier.tryParse(s.substring(1));
                if (id == null) {
                    throw new IllegalArgumentException("Invalid fluid tag: " + s);
                }
                return TransformCircumstance.fluid(TagKey.create(Registries.FLUID, id));
            }

            throw new IllegalArgumentException(
                    "TransformCircumstance string must be 'explosion' or a fluid tag like '#minecraft:water', got: " + s);
        }

        if (from instanceof Map<?, ?> raw) {
            Map<String, Object> map = (Map<String, Object>) raw;

            String typeName = requireString(map, "type", "TransformCircumstance");

            return switch (typeName) {
                case "explosion" -> TransformCircumstance.explosion();
                case "fluid" -> {
                    String tag = requireString(map, "tag", "fluid TransformCircumstance");
                    Identifier id = Identifier.tryParse(tag);
                    if (id == null) {
                        throw new IllegalArgumentException("Invalid fluid tag id: " + tag);
                    }
                    yield TransformCircumstance.fluid(TagKey.create(Registries.FLUID, id));
                }
                default -> throw new IllegalArgumentException(
                        "Unknown TransformCircumstance type: " + typeName + " (must be 'explosion' or 'fluid')");
            };
        }

        throw new IllegalArgumentException("Cannot convert to TransformCircumstance: " + from);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Extracts the tag location string from a fluid TransformCircumstance by
     * encoding it with its codec and reading back the "tag" field.
     * Falls back to an empty string if encoding fails.
     */
    private static String encodeTag(TransformCircumstance value) {
        try {
            var ops = net.minecraft.nbt.NbtOps.INSTANCE;
            var encoded = TransformCircumstance.CODEC.encodeStart(ops, value).getOrThrow();
            if (encoded instanceof net.minecraft.nbt.CompoundTag compound && compound.contains("tag")) {
                return Objects.requireNonNull(compound.get("tag")).toString();
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    private static String requireString(Map<String, Object> map, String key, String context) {
        Object val = map.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Missing '" + key + "' in " + context);
        }
        return val.toString();
    }
}