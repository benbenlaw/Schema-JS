package com.benbenlaw.schemajs.kubejs.cucumber.mystical;

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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record SoulExtractionResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Result> codec) implements RecipeComponent<SoulExtractionResultComponent.Result> {

    public record Result(Identifier type, double souls) {}

    public static final Codec<Result> RESULT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("type").forGetter(Result::type),
                    Codec.DOUBLE.fieldOf("souls").forGetter(Result::souls)
            ).apply(instance, Result::new));

    public static final SoulExtractionResultComponent SOUL_RESULT =
            new SoulExtractionResultComponent(
                    RecipeComponentType.builtin("soul_result"),
                    RESULT_CODEC
            );

    @Override
    public Codec<Result> codec() {
        return codec;
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof NativeObject || from instanceof Result;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Result value, ReplacementMatchInfo match) {
        Object rawMatch = match.match();
        if (rawMatch instanceof Ingredient ingredient) {
            var item = BuiltInRegistries.ITEM.getValue(value.type());
            if (item == null || item == Items.AIR) {
                return false;
            }
            return ingredient.test(new ItemStack(item));
        }
        return false;
    }

    @Override
    public boolean isEmpty(@NonNull Result value) {
        return value == null || value.type == null;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull Result value) {
        builder.append(value.type.toString());
    }

    @Override
    public String toString(@NonNull OpsContainer ops, @NonNull Result value) {
        return value.type + " (" + value.souls + ")";
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Result value) {
        if (value == null || value.type == null) {
            throw new InvalidRecipeComponentValueException(
                    "SoulExtraction result cannot be empty",
                    this,
                    value
            );
        }
    }

    @Override
    public Result wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof NativeArray arr) {

            if (arr.getLength() < 1) {
                throw new IllegalArgumentException("Soul result cannot be empty");
            }

            String type = String.valueOf(arr.get(0));

            double souls = 0.1;

            if (arr.getLength() > 1 && arr.get(1) instanceof Number n) {
                souls = n.doubleValue();
            }

            return new Result(
                    Identifier.parse(type),
                    souls
            );
        }

        if (from instanceof Result r) {
            return r;
        }

        if (from instanceof String s) {
            return new Result(Identifier.parse(s), 0.1);
        }

        throw new IllegalArgumentException(
                "Cannot convert to SoulExtractionResult: " + from
        );
    }
}