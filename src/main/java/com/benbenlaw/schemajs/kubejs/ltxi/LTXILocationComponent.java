package com.benbenlaw.schemajs.kubejs.ltxi;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeValidationContext;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import liedge.ltxindustries.lib.MachineLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * LTXI {@link MachineLocation} (air scrubbing). Accepts:
 * <pre>
 * "minecraft:the_end"                                  // dimension only
 * {dimension: "minecraft:overworld", needsWaterlog: true}
 * </pre>
 * Biome restrictions are not supported from scripts.
 */
public record LTXILocationComponent(ResourceKey<RecipeComponentType<?>> type, Codec<MachineLocation> codec) implements RecipeComponent<MachineLocation> {

    public static final LTXILocationComponent LOCATION = new LTXILocationComponent(RecipeComponentType.builtin("ltxi_location"), MachineLocation.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof String || from instanceof Map || from instanceof MachineLocation;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, MachineLocation value, ReplacementMatchInfo match) {
        return false;
    }

    @Override
    public boolean isEmpty(MachineLocation value) {
        return false;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, MachineLocation value) {
        value.dimension().ifPresent(builder::append);
    }

    @Override
    public String toString(OpsContainer ops, MachineLocation value) {
        return value.toString();
    }

    @Override
    public void validate(RecipeValidationContext ctx, MachineLocation value) {
    }

    @Override
    public MachineLocation wrap(RecipeScriptContext cx, Object from) {
        if (from instanceof MachineLocation location) {
            return location;
        }

        if (from instanceof Map<?, ?> map) {
            Object dimension = LTXIArgs.get(map, "dimension");
            boolean waterlog = LTXIArgs.toBool(LTXIArgs.get(map, "needsWaterlog", "needs_waterlog"), false);
            return new MachineLocation(dimension == null ? Optional.empty() : Optional.of(dimension(dimension)), Optional.empty(), waterlog);
        }

        return new MachineLocation(Optional.of(dimension(from)), Optional.empty(), false);
    }

    private static ResourceKey<Level> dimension(Object value) {
        return ResourceKey.create(Registries.DIMENSION, Identifier.parse(String.valueOf(value)));
    }
}
