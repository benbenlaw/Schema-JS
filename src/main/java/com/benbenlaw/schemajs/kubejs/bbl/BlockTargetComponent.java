package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.BlockTargetCodec;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public record BlockTargetComponent(ResourceKey<RecipeComponentType<?>> type, Codec<BlockTarget> codec) implements RecipeComponent<BlockTarget> {

    public static final BlockTargetComponent BLOCK_TARGET = new BlockTargetComponent(RecipeComponentType.builtin("block_target"), BlockTargetCodec.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(String.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof BlockTarget;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, BlockTarget value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof String s) {
            return value.toString().contains(s);
        }

        if (m instanceof BlockTarget t) {
            return value.equals(t);
        }

        return false;
    }

    @Override
    public boolean isEmpty(BlockTarget value) {
        return value == null;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, BlockTarget value) {
        builder.append(value.toString());
    }

    @Override
    public String toString(OpsContainer ops, BlockTarget value) {
        return value.toString();
    }

    @Override
    public void validate(RecipeValidationContext ctx, BlockTarget value) {
        if (value == null) {
            throw new InvalidRecipeComponentValueException(
                    "BlockTarget cannot be null",
                    this,
                    null
            );
        }
    }

    @Override
    public BlockTarget wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof BlockTarget t) {
            return t;
        }

        if (from instanceof String s) {
            s = s.trim();

            if (s.startsWith("#")) {
                Identifier id = Identifier.tryParse(s.substring(1));
                if (id == null) {
                    throw new IllegalArgumentException("Invalid block tag: " + s);
                }
                return new BlockTarget.Tag(TagKey.create(Registries.BLOCK, id));
            }

            BlockState state = BlockWrapper.parseBlockState(cx.cx(), s);
            return new BlockTarget.Single(state);
        }

        throw new IllegalArgumentException("Cannot convert to BlockTarget: " + from);
    }
}