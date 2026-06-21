package com.benbenlaw.schemajs.kubejs.cucumber;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record MASeedOverride(Optional<Item> craftingSeed, Optional<Item> essence, Optional<Ingredient> material) {

    public static MASeedOverride empty() {
        return new MASeedOverride(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public MASeedOverride withCraftingSeed(Item item) {
        return new MASeedOverride(Optional.of(item), essence, material);
    }

    public MASeedOverride withEssence(Item item) {
        return new MASeedOverride(craftingSeed, Optional.of(item), material);
    }

    public MASeedOverride withMaterial(Ingredient ingredient) {
        return new MASeedOverride(craftingSeed, essence, Optional.of(ingredient));
    }
}