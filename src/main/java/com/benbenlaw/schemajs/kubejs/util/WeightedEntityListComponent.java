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
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;

public record WeightedEntityListComponent(ResourceKey<RecipeComponentType<?>> type, Codec<List<WeightedEntityListComponent.Entry>> codec) implements RecipeComponent<List<WeightedEntityListComponent.Entry>> {

    public record Entry(String entity, int weight) {}

    public static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("entity").forGetter(Entry::entity),
            Codec.INT.fieldOf("weight").forGetter(Entry::weight)
    ).apply(instance, Entry::new));

    public static final Codec<List<Entry>> LIST_CODEC = ENTRY_CODEC.listOf();

    public static final WeightedEntityListComponent ENTITY_LIST = new WeightedEntityListComponent(
                    RecipeComponentType.builtin("entity_list"),
                    LIST_CODEC
            );

    @Override
    public Codec<List<Entry>> codec() {
        return codec;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String
                || from instanceof NativeArray
                || from instanceof NativeObject
                || from instanceof List<?>;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, List<Entry> value, ReplacementMatchInfo match) {
        return true;
    }

    @Override
    public boolean isEmpty(@NonNull List<Entry> value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull List<Entry> value) {
        builder.append("weighted_entity_list");
    }

    @Override
    public String toString(@NonNull OpsContainer ops, @NonNull List<Entry> value) {
        return "WeightedEntityList";
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull List<Entry> value) {
        if (value == null || value.isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "WeightedEntityList cannot be empty",
                    this,
                    value
            );
        }
    }

    @Override
    public List<Entry> wrap(RecipeScriptContext cx, Object from) {

        List<Entry> out = new java.util.ArrayList<>();

        if (from instanceof NativeArray arr) {

            for (int i = 0; i < arr.getLength(); i++) {
                Object entry = arr.get(i);

                if (entry instanceof NativeArray pair && pair.getLength() >= 1) {

                    String id = String.valueOf(pair.get(0));
                    int weight = 1;

                    if (pair.getLength() > 1 && pair.get(1) instanceof Number n) {
                        weight = n.intValue();
                    }

                    out.add(new Entry(id, weight));
                }
            }

            return out;
        }

        if (from instanceof List<?> list) {

            for (Object entry : list) {

                if (entry instanceof Entry e) {
                    out.add(e);
                }
                else if (entry instanceof NativeArray pair && pair.getLength() >= 1) {

                    String id = String.valueOf(pair.get(0));
                    int weight = 1;

                    if (pair.getLength() > 1 && pair.get(1) instanceof Number n) {
                        weight = n.intValue();
                    }

                    out.add(new Entry(id, weight));
                }
            }

            return out;
        }

        throw new IllegalArgumentException(
                "Cannot convert to WeightedEntityList: " + from
        );
    }
}