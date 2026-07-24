package com.benbenlaw.schemajs.kubejs.cucumber.mystical;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class MASeedCraftingEventJS implements KubeEvent {

    public SeedCraftingBuilder seedCrafting(String seedId) {
        Identifier id = Identifier.parse(seedId);
        return new SeedCraftingBuilder(id);
    }

    public static class SeedCraftingBuilder {
        private final Identifier seedId;

        private SeedCraftingBuilder(Identifier seedId) {
            this.seedId = seedId;
        }

        public SeedCraftingBuilder seed(Item craftingSeed) {
            MASeedRecipeOverrides.update(seedId, o -> o.withCraftingSeed(craftingSeed));
            return this;
        }

        public SeedCraftingBuilder essence(Item essence) {
            MASeedRecipeOverrides.update(seedId, o -> o.withEssence(essence));
            return this;
        }

        public SeedCraftingBuilder ingredient(Ingredient material) {
            MASeedRecipeOverrides.update(seedId, o -> o.withMaterial(material));
            return this;
        }
    }
}