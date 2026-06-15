package com.benbenlaw.schemajs.kubejs.ae2.compoment;

import appeng.recipes.entropy.EntropyRecipe.BlockOutput;
import appeng.recipes.entropy.EntropyRecipe.FluidOutput;
import appeng.recipes.entropy.EntropyRecipe.Output;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public record EntropyOutputComponent(ResourceKey<RecipeComponentType<?>> type, Codec<Output> codec) implements RecipeComponent<Output> {

    public static final EntropyOutputComponent INSTANCE = new EntropyOutputComponent(
            RecipeComponentType.builtin("entropy_output"),
            Output.CODEC
    );

    @Override
    public @NonNull TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean isEmpty(@NonNull Output value) {
        return value.block().isEmpty() && value.fluid().isEmpty() && value.drops().isEmpty();
    }

    @Override
    public boolean hasPriority(@NonNull RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof Map || from instanceof Output;
    }

    @Override
    public void buildUniqueId(@NonNull UniqueIdBuilder builder, Output value) {
        value.block().ifPresent(b -> builder.append("block_" + BuiltInRegistries.BLOCK.getKey(b.block())));
        value.fluid().ifPresent(f -> builder.append("fluid_" + BuiltInRegistries.FLUID.getKey(f.fluid())));
        if (!value.drops().isEmpty()) builder.append("drops_" + value.drops().size());
    }

    @Override
    public @NonNull String toString(@NonNull OpsContainer ops, Output value) {
        var sb = new StringBuilder("{");
        value.block().ifPresent(b -> sb.append("block=").append(BuiltInRegistries.BLOCK.getKey(b.block())));
        value.fluid().ifPresent(f -> {
            if (sb.length() > 1) sb.append(", ");
            sb.append("fluid=").append(BuiltInRegistries.FLUID.getKey(f.fluid()));
        });
        if (!value.drops().isEmpty()) {
            if (sb.length() > 1) sb.append(", ");
            sb.append("drops=").append(value.drops().size()).append(" items");
        }
        return sb.append("}").toString();
    }

    @Override
    public void validate(@NonNull RecipeValidationContext ctx, @NonNull Output value) {
        if (isEmpty(value)) {
            throw new InvalidRecipeComponentValueException(
                    "EntropyRecipe Output must have at least a block, fluid, or drops", this, null);
        }
    }

    @Override
    public @NonNull Output wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof Output o) return o;

        if (from instanceof String s) {
            s = s.trim();

            Fluid fluid = resolveFluid(s);
            if (fluid != Fluids.EMPTY) {
                return new Output(Optional.empty(), Optional.of(new FluidOutput(fluid, false, Map.of())), List.of());
            }

            try {
                BlockState state = BlockStateComponent.BLOCK_STATE_STRING.wrap(cx, s);
                if (state.getBlock() != Blocks.AIR) {
                    return new Output(Optional.of(new BlockOutput(state.getBlock(), false, Map.of())), Optional.empty(), List.of());
                }
            } catch (Exception ignored) {}

            ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, s);
            if (!stack.isEmpty()) {
                return new Output(Optional.empty(), Optional.empty(),
                        List.of(ItemStackTemplate.fromNonEmptyStack(stack)));
            }

            throw new IllegalArgumentException(
                    "EntropyRecipe Output: '" + s + "' is not a valid block, fluid, or item");
        }

        throw new IllegalArgumentException("Cannot convert to EntropyRecipe Output: " + from);
    }

    private static Fluid resolveFluid(String s) {
        int bracket = s.indexOf('[');
        String cleanId = bracket >= 0 ? s.substring(0, bracket) : s;
        return BuiltInRegistries.FLUID.getValue(Identifier.parse(cleanId));
    }
}
