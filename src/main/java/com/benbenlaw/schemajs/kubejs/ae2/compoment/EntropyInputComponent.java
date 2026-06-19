package com.benbenlaw.schemajs.kubejs.ae2.compoment;

import appeng.recipes.entropy.EntropyRecipe;
import appeng.recipes.entropy.EntropyRecipe.BlockInput;
import appeng.recipes.entropy.EntropyRecipe.FluidInput;
import appeng.recipes.entropy.EntropyRecipe.Input;
import appeng.recipes.entropy.PropertyValueMatcher;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public record EntropyInputComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Input> codec) implements RecipeComponent<Input> {

    public static final EntropyInputComponent INSTANCE = new EntropyInputComponent(
            RecipeComponentType.builtin("entropy_input"),
            Input.CODEC
    );

    @Override
    public @NonNull TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean isEmpty(@NonNull Input value) {
        return value.block().isEmpty() && value.fluid().isEmpty();
    }

    @Override
    public boolean hasPriority(@NonNull RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof Map || from instanceof Input;
    }

    @Override
    public void buildUniqueId(@NonNull UniqueIdBuilder builder, Input value) {
        value.block().ifPresent(b -> builder.append("block_" + BuiltInRegistries.BLOCK.getKey(b.block())));
        value.fluid().ifPresent(f -> builder.append("fluid_" + BuiltInRegistries.FLUID.getKey(f.fluid())));
    }

    @Override
    public boolean matches(RecipeMatchContext cx, Input value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, Input value) {
        var sb = new StringBuilder("{");
        value.block().ifPresent(b -> sb.append("block=").append(BuiltInRegistries.BLOCK.getKey(b.block())));
        value.fluid().ifPresent(f -> {
            if (sb.length() > 1) sb.append(", ");
            sb.append("fluid=").append(BuiltInRegistries.FLUID.getKey(f.fluid()));
        });
        return sb.append("}").toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Input value) {
        if (isEmpty(value)) {
            throw new InvalidRecipeComponentValueException(
                    "EntropyRecipe Input must have at least a block or fluid", this, null);
        }
    }

    @Override
    public @NonNull Input wrap(@NonNull RecipeScriptContext cx, Object from) {
        if (from instanceof Input i) return i;

        if (from instanceof String s) {
            s = s.trim();

            BlockState state = BlockStateComponent.BLOCK_STATE_STRING.wrap(cx, s);

            Fluid fluid = resolveFluid(s);
            if (fluid != Fluids.EMPTY) {
                return new Input(Optional.empty(), Optional.of(new FluidInput(fluid, Map.of())));
            }

            if (state.getBlock() != Blocks.AIR) {
                return new Input(Optional.of(blockInputFromState(state)), Optional.empty());
            }

            throw new IllegalArgumentException(
                    "EntropyRecipe Input: '" + s + "' is not a valid block or fluid");
        }

        throw new IllegalArgumentException("Cannot convert to EntropyRecipe Input: " + from);
    }

    private static BlockInput blockInputFromState(BlockState state) {
        Block block = state.getBlock();
        Map<String, PropertyValueMatcher> properties = new HashMap<>();

        BlockState defaultState = block.defaultBlockState();
        for (var property : state.getProperties()) {
            addInputProperty(state, defaultState, property, properties);
        }

        return new BlockInput(block, properties);
    }

    private static <T extends Comparable<T>> void addInputProperty(
            BlockState state, BlockState defaultState,
            Property<T> property,
            Map<String, PropertyValueMatcher> properties) {
        T value = state.getValue(property);
        T defaultValue = defaultState.getValue(property);
        if (!value.equals(defaultValue)) {
            properties.put(property.getName(), parseExactMatcher(property.getName(value)));
        }
    }

    private static PropertyValueMatcher parseExactMatcher(String value) {
        var decoded = PropertyValueMatcher.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                net.minecraft.nbt.StringTag.valueOf(value));
        return decoded.getOrThrow();
    }

    private static Fluid resolveFluid(String id) {
        int bracket = id.indexOf('[');
        String cleanId = bracket >= 0 ? id.substring(0, bracket) : id;
        return BuiltInRegistries.FLUID.getValue(Identifier.parse(cleanId));
    }
}
