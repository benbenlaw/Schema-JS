package com.benbenlaw.schemajs.kubejs.ltxi;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.ResultCount;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * LTXI {@link ItemResult}. Accepts:
 * <pre>
 * "4x minecraft:diamond"
 * ["4x minecraft:diamond", 0.5]            // 50% chance to be produced
 * ["4x minecraft:diamond", 0.5, false]     // third value = required
 * {item: "minecraft:diamond", min: 1, max: 3, chance: 0.5, required: false}
 * </pre>
 */
public record LTXIItemResultComponent(ResourceKey<RecipeComponentType<?>> type, Codec<ItemResult> codec) implements RecipeComponent<ItemResult> {

    public static final LTXIItemResultComponent ITEM_RESULT = new LTXIItemResultComponent(RecipeComponentType.builtin("ltxi_item_result"), ItemResult.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof List || from instanceof Map || from instanceof ItemResult || ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ItemResult value, ReplacementMatchInfo match) {
        return match.match() instanceof ItemMatch m && m.matches(cx, value.display(), match.exact());
    }

    @Override
    public boolean isEmpty(ItemResult value) {
        return value.display().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, ItemResult value) {
        value.typeHolder().unwrapKey().ifPresent(builder::append);
    }

    @Override
    public String toString(OpsContainer ops, ItemResult value) {
        ResultCount count = value.count();
        String text = count.max() + "x " + value.typeHolder().getRegisteredName();
        return count.chance() >= 1.0F ? text : text + " @ " + count.chance();
    }

    @Override
    public void validate(RecipeValidationContext ctx, ItemResult value) {
        ResultCount count = value.count();
        if (count.min() < 0 || count.max() < 1 || count.min() > count.max()) {
            throw new InvalidRecipeComponentValueException("Item result count is invalid (min " + count.min() + ", max " + count.max() + ")", this, value);
        }
        if (count.chance() < 0F || count.chance() > 1F) {
            throw new InvalidRecipeComponentValueException("Chance must be between 0 and 1", this, value);
        }
    }

    @Override
    public ItemResult wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof ItemResult result) {
            return result;
        }

        Object item = from;
        float chance = 1.0F;
        Boolean required = null;
        Integer min = null;
        Integer max = null;

        if (from instanceof List<?> list) {
            if (list.isEmpty()) {
                throw new IllegalArgumentException("LTXI item result array must have at least 1 element (item)");
            }
            item = list.get(0);
            chance = LTXIArgs.toFloat(LTXIArgs.get(list, 1), 1.0F);
            if (list.size() > 2) {
                required = LTXIArgs.toBool(list.get(2), true);
            }
        } else if (from instanceof Map<?, ?> map) {
            item = LTXIArgs.get(map, "item", "id");
            if (item == null) {
                throw new IllegalArgumentException("LTXI item result missing 'item' field");
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

        ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, item);

        ResultCount count;
        if (min != null) {
            count = ResultCount.of(min, max, chance);
        } else if (chance >= 1.0F) {
            count = ResultCount.exactly(stack.getCount());
        } else {
            count = ResultCount.exactlyRandom(stack.getCount(), chance);
        }

        return ItemResult.of(stack.typeHolder(), count, stack.getComponentsPatch(), required != null ? required : true);
    }
}
