package com.benbenlaw.schemajs.kubejs.rifts;

import com.benbenlaw.rifts.recipe.util.BlockTarget;
import com.benbenlaw.rifts.recipe.util.BlockTargetCodec;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Rifts converter input: a block state ("minecraft:water[level=0]") or a block tag ("#minecraft:logs").
 */
public record RiftsBlockTargetComponent(ResourceKey<RecipeComponentType<?>> type, Codec<BlockTarget> codec) implements RecipeComponent<BlockTarget> {

    public static final RiftsBlockTargetComponent BLOCK_TARGET = new RiftsBlockTargetComponent(RecipeComponentType.builtin("rifts_block_target"), BlockTargetCodec.CODEC);

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
    }

    @Override
    public BlockTarget wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof BlockTarget target) {
            return target;
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

        throw new IllegalArgumentException("Cannot convert to Rifts BlockTarget: " + from);
    }
}
