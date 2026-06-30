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
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record BlockTagComponent(ResourceKey<RecipeComponentType<?>> type, Codec<TagKey<Block>> codec) implements RecipeComponent<TagKey<Block>> {

    public static final BlockTagComponent BLOCK_TAG = new BlockTagComponent(RecipeComponentType.builtin("block_tag"), TagKey.codec(Registries.BLOCK));

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        if (from instanceof TagKey<?>) {
            return true;
        }
        return from instanceof String s && s.startsWith("#");
    }

    @Override
    public boolean matches(RecipeMatchContext cx, TagKey<Block> value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof TagKey<?> key) {
            return key.equals(value);
        }

        if (m instanceof Block block) {
            return block.defaultBlockState().is(value);
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull TagKey<Block> value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull TagKey<Block> value) {
        builder.append(value.location().toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull TagKey<Block> value) {
        return "#" + value.location();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull TagKey<Block> value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "Block tag cannot be null",
                    this,
                    value
            );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public TagKey<Block> wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof TagKey<?> key) {
            return (TagKey<Block>) key;
        }

        if (from instanceof String s) {
            String stripped = s.startsWith("#") ? s.substring(1) : s;
            Identifier id = Identifier.tryParse(stripped);

            if (id == null) {
                throw new IllegalArgumentException(
                        "Invalid block tag id: " + s
                );
            }

            return TagKey.create(Registries.BLOCK, id);
        }

        throw new IllegalArgumentException(
                "Cannot convert to block tag: " + from
        );
    }
}