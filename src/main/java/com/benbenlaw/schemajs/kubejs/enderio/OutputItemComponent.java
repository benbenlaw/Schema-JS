package com.benbenlaw.schemajs.kubejs.enderio;

import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe.OutputItem;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

public record OutputItemComponent(ResourceKey<RecipeComponentType<?>> type, Codec<OutputItem> codec) implements RecipeComponent<OutputItem> {

    public static final OutputItemComponent OUTPUT_ITEM = new OutputItemComponent(RecipeComponentType.builtin("output_item"), OutputItem.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return ItemWrapper.TYPE_INFO;
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        if (from instanceof String s && s.startsWith("#")) {
            return true;
        }
        return ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, OutputItem value, ReplacementMatchInfo match) {
        if (match.match() instanceof ItemMatch m) {
            ItemStack stack = value.getItemStackTemplate().map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);

            return !stack.isEmpty()
                    && m.matches(cx, stack, match.exact());
        }

        return false;
    }

    @Override
    public boolean isEmpty(OutputItem value) {
        return !value.isPresent();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, OutputItem value) {
        value.getItemStackTemplate().ifPresent(template -> {
            ItemStack stack = template.create();
            if (!stack.isEmpty()) {
                builder.append(stack.typeHolder().getKey());
                builder.append(Float.toString(value.chance()));
            }
        });
    }

    @Override
    public String toString(OpsContainer ops, OutputItem value) {
        return value.getItemStackTemplate()
                .map(template -> {
                    ItemStack stack = template.create();
                    String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    return value.chance() >= 1.0F ? id : id + " @ " + value.chance();
                })
                .orElse("empty");
    }

    @Override
    public void validate(RecipeValidationContext ctx, OutputItem value) {
        if (!value.isPresent()) {
            throw new InvalidRecipeComponentValueException(
                    "Output item cannot be empty",
                    this,
                    value
            );
        }

        if (value.chance() < 0F || value.chance() > 1F) {
            throw new InvalidRecipeComponentValueException(
                    "Chance must be between 0 and 1",
                    this,
                    value
            );
        }
    }

    @Override
    public OutputItem wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof OutputItem oi) {
            return oi;
        }

        if (from instanceof NativeArray arr) {
            if (arr.getLength() < 1) {
                throw new IllegalArgumentException("OutputItem array must have at least 1 element (item)");
            }

            Object itemObj = arr.get(0);
            float chance = 1.0F;
            boolean optional = false;

            if (arr.getLength() > 1) {
                chance = Float.parseFloat(String.valueOf(arr.get(1)));
            }
            if (arr.getLength() > 2) {
                optional = Boolean.TRUE.equals(arr.get(2));
            }

            return wrapItemLike(cx, itemObj, chance, optional);
        }

        if (from instanceof NativeObject obj) {
            Object itemObj = obj.get("item");
            Object tagObj = obj.get("tag");

            float chance = 1.0F;
            Object chanceObj = obj.get("chance");
            if (chanceObj != null) {
                chance = Float.parseFloat(String.valueOf(chanceObj));
            }

            boolean optional = Boolean.TRUE.equals(obj.get("optional"));

            if (tagObj instanceof String tagStr) {
                int count = obj.get("count") instanceof Number n ? n.intValue() : 1;
                return buildTagOutput(tagStr, count, chance, optional);
            }

            if (itemObj == null) {
                throw new IllegalArgumentException("OutputItem missing 'item' or 'tag' field");
            }

            return wrapItemLike(cx, itemObj, chance, optional);
        }

        if (from instanceof String s && s.startsWith("#")) {
            String[] parts = s.trim().split("\\s+");
            int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            return buildTagOutput(parts[0], count, 1.0F, false);
        }

        // Falls through to ItemStackComponent for plain strings, Item, ItemStack, etc.
        return wrapItemLike(cx, from, 1.0F, false);
    }

    private static OutputItem wrapItemLike(RecipeScriptContext cx, Object itemObj, float chance, boolean optional) {
        ItemStack stack = ItemStackComponent.ITEM_STACK.wrap(cx, itemObj);
        ItemStackTemplate template = ItemStackTemplate.fromNonEmptyStack(stack);
        return OutputItem.of(template, chance, optional);
    }

    private static OutputItem buildTagOutput(String tagStr, int count, float chance, boolean optional) {
        String stripped = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;
        Identifier id = Identifier.tryParse(stripped);
        if (id == null) {
            throw new IllegalArgumentException("Invalid tag id for OutputItem: " + tagStr);
        }

        TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
        return OutputItem.of(tag, count, chance, optional);
    }
}