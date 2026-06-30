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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public record EnchantmentHolderComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Holder<Enchantment>> codec) implements RecipeComponent<Holder<Enchantment>> {

    public static final EnchantmentHolderComponent ENCHANTMENT_HOLDER = new EnchantmentHolderComponent(RecipeComponentType.builtin("enchantment_holder"), Enchantment.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof ResourceKey<?> || from instanceof Holder<?>;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Holder<Enchantment> value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof ResourceKey<?> key) {
            return value.is((ResourceKey<Enchantment>) key);
        }

        if (m instanceof Holder<?> holder) {
            return holder.equals(value);
        }

        return false;
    }

    @Override
    public boolean isEmpty(@NonNull Holder<Enchantment> value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, @NonNull Holder<Enchantment> value) {
        builder.append(idOf(value).toString());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, @NonNull Holder<Enchantment> value) {
        return idOf(value).toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Holder<Enchantment> value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException("Enchantment cannot be null", this, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Holder<Enchantment> wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof Holder<?> holder && holder.value() instanceof Enchantment) {
            return (Holder<Enchantment>) holder;
        }

        if (from instanceof ResourceKey<?> key) {
            return resolveHolder(cx, (ResourceKey<Enchantment>) key, from);
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);
            if (id == null) {
                throw new IllegalArgumentException("Invalid enchantment id: " + s);
            }
            return resolveHolder(cx, ResourceKey.create(Registries.ENCHANTMENT, id), from);
        }

        throw new IllegalArgumentException("Cannot convert to Enchantment: " + from);
    }

    private static Holder<Enchantment> resolveHolder(RecipeScriptContext cx, ResourceKey<Enchantment> key, Object original) {
        Registry<Enchantment> registry = cx.registries().lookupOrThrow(Registries.ENCHANTMENT);
        Enchantment value = registry.getValue(key);
        if (value == null) {
            throw new IllegalArgumentException("Unknown enchantment: " + original);
        }
        return registry.wrapAsHolder(value);
    }

    private static Identifier idOf(Holder<Enchantment> value) {
        return value.unwrapKey()
                .map(ResourceKey::identifier)
                .orElseThrow(() -> new IllegalStateException("Enchantment holder has no resource key"));
    }
}