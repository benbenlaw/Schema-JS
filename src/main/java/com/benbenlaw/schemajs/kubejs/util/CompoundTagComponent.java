package com.benbenlaw.schemajs.kubejs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.NativeObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;

public record CompoundTagComponent(ResourceKey<RecipeComponentType<?>> type, Codec<CompoundTag> codec) implements RecipeComponent<CompoundTag> {

    public static final CompoundTagComponent COMPOUND_TAG = new CompoundTagComponent(RecipeComponentType.builtin("compound_tag"), CompoundTag.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof CompoundTag || from instanceof NativeObject;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, CompoundTag value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof NativeObject obj) {
            return value.equals(convert(obj));
        }

        return false;
    }

    @Override
    public boolean isEmpty(CompoundTag value) {
        return value.isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, CompoundTag value) {
        builder.append(value.toString());
    }

    @Override
    public String toString(OpsContainer ops, CompoundTag value) {
        return value.toString();
    }

    @Override
    public void validate(RecipeValidationContext ctx, CompoundTag value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException("CompoundTag cannot be null", this,null
            );
        }
    }

    public CompoundTag wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof CompoundTag tag) {
            return tag;
        }

        if (from instanceof NativeObject obj) {
            return convert(obj);
        }

        throw new IllegalArgumentException("Cannot convert to CompoundTag: " + from);
    }

    private CompoundTag convert(NativeObject obj) {
        CompoundTag tag = new CompoundTag();

        for (Object keyObj : obj.keySet()) {
            String key = String.valueOf(keyObj);
            Object value = obj.get(keyObj);

            write(tag, key, value);
        }

        return tag;
    }

    private void write(CompoundTag tag, String key, Object value) {

        switch (value) {
            case null -> {
                return;
            }
            case Number n -> {
                tag.putDouble(key, n.doubleValue());
                return;
            }
            case Boolean b -> {
                tag.putBoolean(key, b);
                return;
            }
            case String s -> {
                tag.putString(key, s);
                return;
            }
            case NativeObject obj -> {
                tag.put(key, convert(obj));
                return;
            }
            case java.util.Map<?, ?> map -> {
                NativeObject temp = new NativeObject(new ContextFactory());
                temp.putAll(map);
                tag.put(key, convert(temp));
            }
            default -> {
            }
        }

        throw new IllegalArgumentException("Unsupported NBT value: " + value);
    }
}