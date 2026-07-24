package com.benbenlaw.schemajs.mixin;

import com.benbenlaw.schemajs.kubejs.cucumber.mystical.MASeedOverride;
import com.benbenlaw.schemajs.kubejs.cucumber.mystical.MASeedRecipeOverrides;
import com.blakebr0.mysticalagriculture.MysticalAgriculture;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.config.ModConfigs;
import com.blakebr0.mysticalagriculture.crafting.DynamicRecipeManager;
import com.blakebr0.mysticalagriculture.crafting.recipe.InfusionRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(DynamicRecipeManager.class)
public class DynamicRecipeManagerMixin {

    @Inject(
            method = "makeSeedRecipe(Lcom/blakebr0/mysticalagriculture/api/crop/Crop;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/crafting/RecipeHolder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void schemaJS$makeSeedRecipe(Crop crop, HolderLookup.Provider registries,
                                                CallbackInfoReturnable<RecipeHolder<Recipe<?>>> cir) {

        if (!ModList.get().isLoaded("mysticalagriculture")) return;

        if (!crop.isEnabled() || !crop.getRecipeConfig().isSeedInfusionRecipeEnabled()) return;

        Identifier seedId = BuiltInRegistries.ITEM.getKey(crop.getSeedsItem());
        Optional<MASeedOverride> overrideOpt = MASeedRecipeOverrides.get(seedId);
        if (overrideOpt.isEmpty()) return;

        MASeedOverride override = overrideOpt.get();

        Item essenceItem = override.essence().orElse(crop.getTier().getEssenceItem());
        if (essenceItem == null) { cir.setReturnValue(null); return; }

        Item craftingSeedItem = override.craftingSeed().orElse(crop.getType().getCraftingSeedItem());
        if (craftingSeedItem == null) { cir.setReturnValue(null); return; }

        Ingredient material = override.material().orElse(crop.getCraftingMaterial(registries));
        if (material == null) { cir.setReturnValue(null); return; }

        Ingredient essence = Ingredient.of(essenceItem);
        Ingredient craftingSeed = Ingredient.of(craftingSeedItem);
        List<Ingredient> inputs = List.of(material, essence, material, essence, material, essence, material, essence);
        Identifier id = MysticalAgriculture.resource(crop.getNameWithSuffix("seeds_infusion"));
        ItemStackTemplate result = new ItemStackTemplate(crop.getSeedsItem());

        cir.setReturnValue(new RecipeHolder<>(
                ResourceKey.create(Registries.RECIPE, id),
                new InfusionRecipe(craftingSeed, inputs, result, false)
        ));
    }

    @Inject(
            method = "makeRegularSeedRecipe(Lcom/blakebr0/mysticalagriculture/api/crop/Crop;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/crafting/RecipeHolder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void schemaJS$makeRegularSeedRecipe(Crop crop, HolderLookup.Provider registries,
                                                       CallbackInfoReturnable<RecipeHolder<Recipe<?>>> cir) {

        if (!ModList.get().isLoaded("mysticalagriculture")) return;
        if (!crop.isEnabled() || !crop.getRecipeConfig().isSeedCraftingRecipeEnabled()) return;
        if (!ModConfigs.SEED_CRAFTING_RECIPES.get()) return;

        Identifier seedId = BuiltInRegistries.ITEM.getKey(crop.getSeedsItem());
        Optional<MASeedOverride> overrideOpt = MASeedRecipeOverrides.get(seedId);
        if (overrideOpt.isEmpty()) return;

        MASeedOverride override = overrideOpt.get();

        Item essenceItem = override.essence().orElse(crop.getTier().getEssenceItem());
        if (essenceItem == null) { cir.setReturnValue(null); return; }

        Item craftingSeedItem = override.craftingSeed().orElse(crop.getType().getCraftingSeedItem());
        if (craftingSeedItem == null) { cir.setReturnValue(null); return; }

        Ingredient material = override.material().orElse(crop.getCraftingMaterial(registries));
        if (material == null) { cir.setReturnValue(null); return; }

        Ingredient essence = Ingredient.of(essenceItem);
        Ingredient craftingSeed = Ingredient.of(craftingSeedItem);
        Map<Character, Ingredient> keys = Map.of('M', material, 'E', essence, 'C', craftingSeed);
        List<String> shape = List.of("MEM", "ECE", "MEM");
        Identifier id = MysticalAgriculture.resource(crop.getNameWithSuffix("seeds_vanilla"));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(keys, shape);
        ItemStackTemplate result = new ItemStackTemplate(crop.getSeedsItem());

        cir.setReturnValue(new RecipeHolder<>(
                ResourceKey.create(Registries.RECIPE, id),
                new ShapedRecipe(new Recipe.CommonInfo(false),
                        new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "mysticalagriculture:seeds"),
                        pattern, result)
        ));
    }
}