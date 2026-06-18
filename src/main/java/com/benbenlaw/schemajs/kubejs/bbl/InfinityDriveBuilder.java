package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.infinitystorage.item.InfinityContent;
import com.benbenlaw.infinitystorage.item.InfinityDrive;
import com.benbenlaw.strainers.item.FluidDropItem;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NonNull;

public class InfinityDriveBuilder extends ItemBuilder {

    public FluidStackTemplate fluidStack;
    public ItemStackTemplate itemStack;

    public InfinityDriveBuilder(Identifier id) {
        super(id);
    }

    @Info("String of the fluid eg minecraft:water")
    public InfinityDriveBuilder fluidContent(String fluid) {
        Fluid resolved = BuiltInRegistries.FLUID.getValue(Identifier.parse(fluid));
        if (resolved == Fluids.EMPTY) {
            throw new IllegalArgumentException("Unknown fluid id '" + fluid + "' for drive " + id);
        }
        this.fluidStack = new FluidStackTemplate(resolved, 1000);
        return this;
    }

    @Info("String of the item eg minecraft:diamond")
    public InfinityDriveBuilder itemContent(String item) {
        this.itemStack = new ItemStackTemplate(BuiltInRegistries.ITEM.getValue(Identifier.parse(item)));
        return this;
    }

    @Override
    public @NonNull Item createObject() {

        InfinityDrive drive;

        if (fluidStack != null) {
            drive = new InfinityDrive(createItemProperties(), InfinityContent.of(fluidStack));
        }
        else if (itemStack != null) {
            drive = new InfinityDrive(createItemProperties(), InfinityContent.of(itemStack));
        } else {
            throw new IllegalStateException("InfinityDriveBuilder for " + id + " has no fluid or item set!");
        }

        return drive;
    }
}