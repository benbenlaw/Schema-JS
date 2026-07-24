package com.benbenlaw.schemajs.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record DataComponentTypeListComponent(
        ResourceKey<RecipeComponentType<?>> type,
        Codec<List<DataComponentType<?>>> codec
) implements RecipeComponent<List<DataComponentType<?>>> {

    public static final DataComponentTypeListComponent DATA_COMPONENT_TYPE_LIST =
            new DataComponentTypeListComponent(
                    RecipeComponentType.builtin("data_component_type_list"),
                    DataComponentType.CODEC.listOf()
            );

    @Override
    public Codec<List<DataComponentType<?>>> codec() {
        return codec;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(List.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof NativeArray || from instanceof List;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, List<DataComponentType<?>> value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(@NonNull List<DataComponentType<?>> value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull List<DataComponentType<?>> value) {
        for (var type : value) {
            var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
            if (key != null) builder.append(key.toString());
        }
    }

    @Override
    public String toString(@NonNull OpsContainer ops, @NonNull List<DataComponentType<?>> value) {
        return value.stream()
                .map(t -> String.valueOf(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(t)))
                .toList()
                .toString();
    }

    @Override
    public List<DataComponentType<?>> wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof NativeArray arr) {
            var list = new ArrayList<DataComponentType<?>>();
            for (int i = 0; i < (int) arr.getLength(); i++) {
                String id = String.valueOf(arr.get(i));
                var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.parse(id));
                if (type == null) {
                    throw new IllegalArgumentException("Unknown data component type: " + id);
                }
                list.add(type);
            }
            return list;
        }

        throw new IllegalArgumentException("Cannot convert to DataComponentType list: " + from);
    }
}