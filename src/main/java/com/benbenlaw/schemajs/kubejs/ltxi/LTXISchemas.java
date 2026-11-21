package com.benbenlaw.schemajs.kubejs.ltxi;

import com.benbenlaw.schemajs.kubejs.util.IdentifierComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.lib.MachineLocation;
import net.minecraft.resources.Identifier;

import java.util.List;

public interface LTXISchemas {

    static RecipeKey<List<RecipeItemInput>> itemInputs() {
        return LTXIItemInputComponent.ITEM_INPUT.asList().inputKey("item_inputs").functionNames("itemInputs");
    }

    static RecipeKey<List<RecipeFluidInput>> fluidInputs() {
        return LTXIFluidInputComponent.FLUID_INPUT.asList().inputKey("fluid_inputs").functionNames("fluidInputs");
    }

    static RecipeKey<List<ItemResult>> itemResults() {
        return LTXIItemResultComponent.ITEM_RESULT.asList().outputKey("item_results").functionNames("itemResults");
    }

    static RecipeKey<List<FluidResult>> fluidResults() {
        return LTXIFluidResultComponent.FLUID_RESULT.asList().outputKey("fluid_results").functionNames("fluidResults");
    }

    static RecipeKey<List<RecipeItemInput>> optItemInputs() {
        return itemInputs().defaultOptional();
    }

    static RecipeKey<List<RecipeFluidInput>> optFluidInputs() {
        return fluidInputs().defaultOptional();
    }

    static RecipeKey<List<ItemResult>> optItemResults() {
        return itemResults().defaultOptional();
    }

    static RecipeKey<List<FluidResult>> optFluidResults() {
        return fluidResults().defaultOptional();
    }

    static RecipeKey<Integer> craftTime() {
        return NumberComponent.INT.otherKey("craft_time").functionNames("craftTime").defaultOptional();
    }

    static RecipeKey<Identifier> mode() {
        return IdentifierComponent.IDENTIFIER.otherKey("mode").functionNames("mode").defaultOptional();
    }

    static RecipeSchema itemMachine() {
        return new RecipeSchema(itemResults(), itemInputs(), optFluidInputs(), optFluidResults(), craftTime(), mode());
    }

    RecipeSchema ARC_SMELTING = itemMachine();
    RecipeSchema ASSEMBLING = itemMachine();
    RecipeSchema ELECTRO_CENTRIFUGING = itemMachine();
    RecipeSchema ENERGIZING = itemMachine();
    RecipeSchema GARDEN_SIMULATING = itemMachine();
    RecipeSchema GEO_SYNTHESIS = itemMachine();
    RecipeSchema GRINDING = itemMachine();
    RecipeSchema MIXING = itemMachine();
    RecipeSchema PRESSING = itemMachine();
    RecipeSchema SIEVING = itemMachine();

    /** Every list is optional, so KubeJS needs the no-argument form declared explicitly: chemical_reacting().fluidInputs(...) */
    static RecipeSchema chemicalReacting() {
        RecipeKey<List<RecipeFluidInput>> fluidIn = optFluidInputs();
        RecipeKey<List<RecipeItemInput>> itemIn = optItemInputs();
        RecipeKey<List<FluidResult>> fluidOut = optFluidResults();
        RecipeKey<List<ItemResult>> itemOut = optItemResults();

        return new RecipeSchema(fluidIn, itemIn, fluidOut, itemOut, craftTime(), mode())
                .constructor()
                .constructor(fluidIn, itemIn, fluidOut, itemOut);
    }

    RecipeSchema CHEMICAL_REACTING = chemicalReacting();

    RecipeSchema AIR_SCRUBBING = new RecipeSchema(
            LTXILocationComponent.LOCATION.otherKey("location"),
            optItemResults(), optFluidResults(), craftTime(), mode());

    RecipeSchema FABRICATING = new RecipeSchema(
            LTXIItemResultComponent.ITEM_RESULT.outputKey("result"),
            itemInputs(),
            NumberComponent.INT.otherKey("energy_required").functionNames("energyRequired"),
            StringComponent.STRING.otherKey("group").functionNames("group").defaultOptional());
}
