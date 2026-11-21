package com.benbenlaw.schemajs.kubejs.rifts;

import com.benbenlaw.rifts.recipe.CrusherRecipe.BonusOutput;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.SizedIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Rifts crusher bonus output. Accepts:
 * <pre>
 * ["#c:dusts/copper", 0.5]                     // 50% chance of a bonus item
 * ["2x minecraft:flint", 0.1]
 * {item: "#c:dusts/copper", chance: 0.5}
 * </pre>
 */
public record RiftsBonusOutputComponent(ResourceKey<RecipeComponentType<?>> type, Codec<BonusOutput> codec) implements RecipeComponent<BonusOutput> {

    public static final RiftsBonusOutputComponent BONUS_OUTPUT = new RiftsBonusOutputComponent(RecipeComponentType.builtin("rifts_bonus_output"), BonusOutput.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof List || from instanceof Map || from instanceof BonusOutput;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, BonusOutput value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(BonusOutput value) {
        return value == null;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, BonusOutput value) {
        builder.append(value.toString());
    }

    @Override
    public String toString(OpsContainer ops, BonusOutput value) {
        return value.item() + " @ " + value.chance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, BonusOutput value) {
        if (value.chance() < 0F || value.chance() > 1F) {
            throw new InvalidRecipeComponentValueException("Bonus chance must be between 0 and 1", this, value);
        }
    }

    @Override
    public BonusOutput wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof BonusOutput bonus) {
            return bonus;
        }

        Object item;
        float chance = 1.0F;

        if (from instanceof List<?> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("Rifts bonus output array must have at least 1 element (item)");
            }
            item = list.get(0);
            chance = LTXIOrDefault.toFloat(list.size() > 1 ? list.get(1) : null, 1.0F);
        } else if (from instanceof Map<?, ?> map) {
            item = map.get("item") != null ? map.get("item") : map.get("ingredient");
            if (item == null) {
                throw new IllegalArgumentException("Rifts bonus output missing 'item' field");
            }
            chance = LTXIOrDefault.toFloat(map.get("chance"), 1.0F);
        } else {
            throw new IllegalArgumentException("Cannot convert to Rifts bonus output: " + from);
        }

        SizedIngredient sized = SizedIngredientComponent.SIZED_INGREDIENT.wrap(cx, item);
        return new BonusOutput(sized, chance);
    }

    private static final class LTXIOrDefault {
        static float toFloat(Object value, float fallback) {
            if (value == null) {
                return fallback;
            }
            if (value instanceof Number n) {
                return n.floatValue();
            }
            return Float.parseFloat(String.valueOf(value));
        }
    }
}
