package com.benbenlaw.schemajs.kubejs.rifts;

import com.benbenlaw.rifts.recipe.util.BlockStateResult;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Rifts converter output: a block state such as "minecraft:blue_ice" or "minecraft:furnace[lit=true]".
 */
public record RiftsBlockStateResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<BlockStateResult> codec) implements RecipeComponent<BlockStateResult> {

    public static final RiftsBlockStateResultComponent BLOCK_STATE_RESULT = new RiftsBlockStateResultComponent(RecipeComponentType.builtin("rifts_block_state_result"), BlockStateResult.CODEC.codec());

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof BlockStateResult;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, BlockStateResult value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(BlockStateResult value) {
        return value == null;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, BlockStateResult value) {
        builder.append(value.toString());
    }

    @Override
    public String toString(OpsContainer ops, BlockStateResult value) {
        return value.toString();
    }

    @Override
    public void validate(RecipeValidationContext ctx, BlockStateResult value) {
    }

    @Override
    public BlockStateResult wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof BlockStateResult result) {
            return result;
        }

        if (from instanceof String s) {
            BlockState state = BlockWrapper.parseBlockState(cx.cx(), s.trim());
            return new BlockStateResult(state);
        }

        throw new IllegalArgumentException("Cannot convert to Rifts BlockStateResult: " + from);
    }
}
