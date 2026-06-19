package com.benbenlaw.schemajs.kubejs.cucumber;

import com.blakebr0.cucumber.crafting.OutputResolver;
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
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record OutputResolverComponent(ResourceKey<RecipeComponentType<?>> type, Codec<OutputResolver> codec) implements RecipeComponent<OutputResolver> {

    public static final OutputResolverComponent OUTPUT_RESOLVER = new OutputResolverComponent(
                    RecipeComponentType.builtin("output_resolver"),
                    OutputResolver.RESULT_CODEC.xmap(
                            OutputResolver.Item::new,
                            OutputResolver::resolve
                    )
            );

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String
                || from instanceof NativeObject
                || from instanceof OutputResolver;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, OutputResolver value, ReplacementMatchInfo match) {
        Object rawMatch = match.match();
        if (rawMatch instanceof Ingredient ingredient) {
            ItemStack stack = value.resolve().create();
            if (stack.isEmpty()) {
                return false;
            }
            return ingredient.test(stack);
        }
        return false;
    }

    @Override
    public boolean isEmpty(@NonNull OutputResolver value) {
        return value.resolve().create().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull OutputResolver value) {
        ItemStack stack = value.resolve().create();

        if (!stack.isEmpty()) {
            builder.append(stack.typeHolder().getKey());
        }
    }

    @Override
    public String toString(
            @NonNull OpsContainer ops,
            @NonNull OutputResolver value
    ) {
        return value.resolve().toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull OutputResolver value) {
        if (value.resolve().create().isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "OutputResolver resolved to empty stack",
                    this,
                    value
            );
        }
    }

    @Override
    public OutputResolver wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof OutputResolver resolver) {
            return resolver;
        }

        if (from instanceof NativeArray array) {
            int len = (int) array.getLength();

            if (len < 1) {
                throw new IllegalArgumentException("OutputResolver array cannot be empty");
            }

            String id = String.valueOf(array.get(0));

            int count = 1;

            if (len > 1) {
                count = ((Number) array.get(1)).intValue();
            }

            if (id.startsWith("#")) {
                return new OutputResolver.Tag(
                        id.substring(1),
                        count
                );
            }

            var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));

            return new OutputResolver.Item(new ItemStackTemplate(item, count));
        }

        // "minecraft:planks"
        if (from instanceof String s) {

            if (s.startsWith("#")) {
                return new OutputResolver.Tag(
                        s.substring(1),
                        1
                );
            }

            var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(s));

            return new OutputResolver.Item(new ItemStackTemplate(item, 1)
            );
        }

        throw new IllegalArgumentException(
                "Cannot convert object to OutputResolver: " + from
        );
    }

    private ItemStack parseItem(String input) {

        input = input.trim();

        if (input.matches("^\\d+x\\s+.+$")) {

            String[] parts = input.split("\\s+", 2);
            int count = Integer.parseInt(parts[0].replace("x", ""));
            String itemId = parts[1];
            Identifier id = Identifier.parse(itemId);

            return new ItemStack(BuiltInRegistries.ITEM.getValue(id), count);
        }

        return new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(input)));
    }
}