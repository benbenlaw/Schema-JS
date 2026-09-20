package com.benbenlaw.schemajs.mixin;

import com.benbenlaw.schemajs.kubejs.cucumber.mystical.MASeedOverride;
import com.benbenlaw.schemajs.kubejs.cucumber.mystical.MASeedRecipeOverrides;
import com.blakebr0.mysticalagriculture.crafting.recipe.InfusionRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InfusionRecipe.class)
public abstract class InfusionRecipeMixin {

    @Shadow @Final @Mutable
    private Ingredient input;

    @Shadow @Final @Mutable
    private List<Ingredient> inputs;

    @Shadow @Final
    private ItemStackTemplate result;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void schemaJS$applyOverride(Ingredient input, List<Ingredient> inputs, ItemStackTemplate result,
                                        boolean transferComponents, CallbackInfo ci) {

        ItemStack resultStack = this.result.create();
        Identifier resultId = BuiltInRegistries.ITEM.getKey(resultStack.getItem());

        MASeedRecipeOverrides.get(resultId).ifPresent(override -> {

            override.craftingSeed().ifPresent(item -> this.input = Ingredient.of(item));

            if (override.essence().isPresent() || override.material().isPresent()) {
                Ingredient essence = override.essence().map(Ingredient::of).orElse(null);
                Ingredient material = override.material().orElse(null);

                List<Ingredient> newInputs = new ArrayList<>(this.inputs.size());
                for (int i = 0; i < this.inputs.size(); i++) {
                    boolean essenceSlot = (i % 2 == 1);
                    if (essenceSlot && essence != null) {
                        newInputs.add(essence);
                    } else if (!essenceSlot && material != null) {
                        newInputs.add(material);
                    } else {
                        newInputs.add(this.inputs.get(i));
                    }
                }
                this.inputs = newInputs;
            }
        });
    }
}