package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.strainers.item.FluidDropItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;

public class DropItemBuilder extends ItemBuilder {

    public Fluid fluid;

    public DropItemBuilder(Identifier id) {
        super(id);
    }

    @Info("Fluid id each drop is 250mb of fluid")
    public DropItemBuilder fluid(String fluidId) {
        this.fluid = BuiltInRegistries.FLUID.getValue(Identifier.tryParse(fluidId));
        return this;
    }

    @Override
    public @NonNull Item createObject() {
        if (fluid == null) {
            throw new IllegalStateException("DropItemBuilder for " + id + " has no fluid set!");
        }
        return new FluidDropItem(createItemProperties(), fluid);
    }
}