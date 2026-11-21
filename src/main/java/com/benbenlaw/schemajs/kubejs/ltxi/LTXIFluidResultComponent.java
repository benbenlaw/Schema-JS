package com.benbenlaw.schemajs.kubejs.ltxi;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ResultCount;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * LTXI {@link FluidResult}. Accepts:
 * <pre>
 * "250x ltxi:argon"
 * ["250x ltxi:argon", 0.25]                // 25% chance to be produced
 * ["250x ltxi:argon", 0.25, false]         // third value = required
 * {fluid: "ltxi:argon", min: 100, max: 250, chance: 0.5, required: false}
 * </pre>
 */
public record LTXIFluidResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<FluidResult> codec) implements RecipeComponent<FluidResult> {

    public static final LTXIFluidResultComponent FLUID_RESULT = new LTXIFluidResultComponent(RecipeComponentType.builtin("ltxi_fluid_result"), FluidResult.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof List || from instanceof Map || from instanceof FluidResult;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, FluidResult value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(FluidResult value) {
        return value.count().max() <= 0;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, FluidResult value) {
        value.typeHolder().unwrapKey().ifPresent(builder::append);
    }

    @Override
    public String toString(OpsContainer ops, FluidResult value) {
        ResultCount count = value.count();
        String text = count.max() + "x " + value.typeHolder().getRegisteredName();
        return count.chance() >= 1.0F ? text : text + " @ " + count.chance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, FluidResult value) {
        ResultCount count = value.count();
        if (count.min() < 0 || count.max() < 1 || count.min() > count.max()) {
            throw new InvalidRecipeComponentValueException("Fluid result amount is invalid (min " + count.min() + ", max " + count.max() + ")", this, value);
        }
        if (count.chance() < 0F || count.chance() > 1F) {
            throw new InvalidRecipeComponentValueException("Chance must be between 0 and 1", this, value);
        }
    }

    @Override
    public FluidResult wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof FluidResult result) {
            return result;
        }

        Object fluid = from;
        float chance = 1.0F;
        Boolean required = null;
        Integer min = null;
        Integer max = null;

        if (from instanceof List<?> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("LTXI fluid result array must have at least 1 element (fluid)");
            }
            fluid = list.get(0);
            chance = LTXIArgs.toFloat(LTXIArgs.get(list, 1), 1.0F);
            if (list.size() > 2) {
                required = LTXIArgs.toBool(list.get(2), true);
            }
        } else if (from instanceof Map<?, ?> map) {
            fluid = LTXIArgs.get(map, "fluid", "id");
            if (fluid == null) {
                throw new IllegalArgumentException("LTXI fluid result missing 'fluid' field");
            }
            chance = LTXIArgs.toFloat(map.get("chance"), 1.0F);
            if (map.get("required") != null) {
                required = LTXIArgs.toBool(map.get("required"), true);
            }
            if (map.get("min") != null || map.get("max") != null) {
                min = LTXIArgs.toInt(map.get("min"), 1);
                max = LTXIArgs.toInt(map.get("max"), min);
            }
        }

        FluidStack stack = FluidStackComponent.FLUID_STACK.wrap(cx, fluid);

        ResultCount count;
        if (min != null) {
            count = ResultCount.of(min, max, chance);
        } else if (chance >= 1.0F) {
            count = ResultCount.exactly(stack.getAmount());
        } else {
            count = ResultCount.exactlyRandom(stack.getAmount(), chance);
        }

        return FluidResult.of(stack.typeHolder(), count, stack.getComponentsPatch(), required != null ? required : true);
    }
}
