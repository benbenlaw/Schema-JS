package com.benbenlaw.schemajs.kubejs.rifts;

import com.benbenlaw.core.recipe.ChanceResult;
import com.benbenlaw.schemajs.kubejs.bbl.component.ChanceResultComponent;
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
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;

/**
 * Rifts quarry result. Same format as the chance result (["2x minecraft:raw_iron", 30]) but the second value
 * is a relative weight (any number above 0, not a 0-1 chance): the quarry picks one result per cycle using
 * each weight over the total of all weights.
 */
public record RiftsQuarryResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ChanceResult> codec) implements RecipeComponent<ChanceResult> {

    public static final RiftsQuarryResultComponent QUARRY_RESULT = new RiftsQuarryResultComponent(RecipeComponentType.builtin("rifts_quarry_result"), ChanceResult.CODEC);

    private static final ChanceResultComponent BASE = ChanceResultComponent.CHANCE_RESULT;

    @Override
    public TypeInfo typeInfo() {
        return BASE.typeInfo();
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return BASE.hasPriority(cx, from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ChanceResult value, ReplacementMatchInfo match) {
        return BASE.matches(cx, value, match);
    }

    @Override
    public boolean isEmpty(ChanceResult value) {
        return BASE.isEmpty(value);
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, ChanceResult value) {
        BASE.buildUniqueId(builder, value);
    }

    @Override
    public String toString(OpsContainer ops, ChanceResult value) {
        return BASE.toString(ops, value);
    }

    @Override
    public void validate(RecipeValidationContext ctx, ChanceResult value) {
        if (value.template().create().isEmpty()) {
            throw new InvalidRecipeComponentValueException("Quarry result cannot be empty", this, value);
        }

        if (value.chance() <= 0F) {
            throw new InvalidRecipeComponentValueException("Quarry weight must be above 0", this, value);
        }
    }

    @Override
    public ChanceResult wrap(RecipeScriptContext cx, Object from) {
        return BASE.wrap(cx, from);
    }
}
