package com.benbenlaw.schemajs.kubejs.bbl.component;

import com.benbenlaw.structureloot.recipe.StructureLootRecipe.LootRoll;
import com.benbenlaw.structureloot.recipe.StructureLootRecipe.LootContextType;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;

public record LootRollComponent(ResourceKey<RecipeComponentType<?>> type, Codec<LootRoll> codec) implements RecipeComponent<LootRoll> {

    public static final LootRollComponent LOOT_ROLL = new LootRollComponent(RecipeComponentType.builtin("loot_roll"), LootRoll.CODEC);

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Object.class);
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
        return from instanceof LootRoll || from instanceof Identifier || from instanceof String
                || from instanceof NativeObject || from instanceof NativeArray;
    }

    @Override
    public boolean matches(RecipeMatchContext cx, LootRoll value, ReplacementMatchInfo match) {
        Object m = match.match();

        if (m instanceof LootRoll roll) {
            return roll.table().equals(value.table());
        }

        if (m instanceof Identifier id) {
            return id.equals(value.table());
        }

        if (m instanceof String s) {
            Identifier id = Identifier.tryParse(s);
            return id != null && id.equals(value.table());
        }

        return false;
    }

    @Override
    public boolean isEmpty(LootRoll value) {
        return value == null;
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, LootRoll value) {
        builder.append(value.table().toString());
        builder.append(value.type().toName());
    }

    @Override
    public String toString(OpsContainer ops, LootRoll value) {
        return value.table().toString() + " (" + value.type().toName() + ")";
    }

    @Override
    public void validate(RecipeValidationContext ctx, LootRoll value) {
        if (value == null || value.table() == null) {
            throw new InvalidRecipeComponentValueException(
                    "LootRoll must have a valid table identifier",
                    this,
                    value
            );
        }

        if (value.type() == LootContextType.BLOCK && value.blockId().isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "LootRoll of type 'block' must specify a 'block' identifier",
                    this,
                    value
            );
        }

        if (value.type() == LootContextType.ENTITY && value.entityId().isEmpty()) {
            throw new InvalidRecipeComponentValueException(
                    "LootRoll of type 'entity' must specify an 'entity' identifier",
                    this,
                    value
            );
        }
    }

    @Override
    public LootRoll wrap(RecipeScriptContext cx, Object from) {

        if (from instanceof LootRoll roll) {
            return roll;
        }

        if (from instanceof Identifier id) {
            return LootRoll.generic(id);
        }

        if (from instanceof String s) {
            Identifier id = Identifier.tryParse(s);
            if (id == null) {
                throw new IllegalArgumentException("Invalid identifier: " + s);
            }
            return LootRoll.generic(id);
        }

        if (from instanceof NativeArray arr) {
            int len = (int) arr.getLength();

            if (len == 1) {
                Identifier table = parseIdentifier(arr.get(0), "table");
                return LootRoll.generic(table);
            }

            if (len == 2) {
                LootContextType lootType = LootContextType.fromString(String.valueOf(arr.get(0)));
                Identifier table = parseIdentifier(arr.get(1), "table");

                if (lootType != LootContextType.GENERIC) {
                    throw new IllegalArgumentException(
                            "LootRoll array of length 2 only supports type 'generic' — " +
                                    "use [type, id, table] for 'block'/'entity'");
                }

                return LootRoll.generic(table);
            }

            LootContextType lootType = LootContextType.fromString(String.valueOf(arr.get(0)));
            Identifier id = parseIdentifier(arr.get(1), lootType == LootContextType.BLOCK ? "block" : "entity");
            Identifier table = parseIdentifier(arr.get(2), "table");

            return switch (lootType) {
                case BLOCK -> LootRoll.block(table, id);
                case ENTITY -> LootRoll.entity(table, id);
                case GENERIC -> LootRoll.generic(table);
            };
        }

        if (from instanceof NativeObject obj) {
            Object tableObj = obj.get("table");
            if (tableObj == null) {
                throw new IllegalArgumentException("LootRoll missing 'table' field");
            }

            Identifier table = Identifier.tryParse(String.valueOf(tableObj));
            if (table == null) {
                throw new IllegalArgumentException("Invalid 'table' identifier: " + tableObj);
            }

            Object typeObj = obj.get("type");
            LootContextType lootType = typeObj != null
                    ? LootContextType.fromString(String.valueOf(typeObj))
                    : LootContextType.GENERIC;

            return switch (lootType) {
                case BLOCK -> {
                    Object blockObj = obj.get("block");
                    if (blockObj == null) {
                        throw new IllegalArgumentException("LootRoll of type 'block' must specify a 'block' field");
                    }
                    Identifier blockId = Identifier.tryParse(String.valueOf(blockObj));
                    if (blockId == null) {
                        throw new IllegalArgumentException("Invalid 'block' identifier: " + blockObj);
                    }
                    yield LootRoll.block(table, blockId);
                }
                case ENTITY -> {
                    Object entityObj = obj.get("entity");
                    if (entityObj == null) {
                        throw new IllegalArgumentException("LootRoll of type 'entity' must specify an 'entity' field");
                    }
                    Identifier entityId = Identifier.tryParse(String.valueOf(entityObj));
                    if (entityId == null) {
                        throw new IllegalArgumentException("Invalid 'entity' identifier: " + entityObj);
                    }
                    yield LootRoll.entity(table, entityId);
                }
                case GENERIC -> LootRoll.generic(table);
            };
        }

        throw new IllegalArgumentException("Cannot convert to LootRoll: " + from);
    }

    private static Identifier parseIdentifier(Object raw, String fieldName) {
        if (raw == null) {
            throw new IllegalArgumentException("LootRoll array missing '" + fieldName + "' value");
        }
        Identifier id = Identifier.tryParse(String.valueOf(raw));
        if (id == null) {
            throw new IllegalArgumentException("Invalid '" + fieldName + "' identifier: " + raw);
        }
        return id;
    }
}