package com.benbenlaw.schemajs.kubejs.cucumber.mystical;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class MASeedRecipeOverrides {

    private static final Map<Identifier, MASeedOverride> OVERRIDES = new HashMap<>();

    public static void clear() {
        OVERRIDES.clear();
    }

    public static Optional<MASeedOverride> get(Identifier seedId) {
        return Optional.ofNullable(OVERRIDES.get(seedId));
    }

    public static void update(Identifier seedId, UnaryOperator<MASeedOverride> mutator) {
        OVERRIDES.merge(seedId, mutator.apply(MASeedOverride.empty()),
                (existing, ignored) -> mutator.apply(existing));
    }
}