package com.benbenlaw.schemajs.kubejs.bbl.builder;

import com.benbenlaw.infinitystorage.item.InfinityContent;
import com.benbenlaw.infinitystorage.item.InfinityDrive;
import dev.latvian.mods.kubejs.item.ItemBuilder;
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

    private Identifier fluidId;
    private Identifier itemId;

    public InfinityDriveBuilder(Identifier id) {
        super(id);
    }

    @Info("String of the fluid eg minecraft:water")
    public InfinityDriveBuilder fluidContent(String fluid) {
        this.fluidId = Identifier.parse(fluid);
        return this;
    }

    @Info("String of the item eg minecraft:diamond")
    public InfinityDriveBuilder itemContent(String item) {
        this.itemId = Identifier.parse(item);
        return this;
    }

    @Override
    public @NonNull Item createObject() {
        if (fluidId != null) {
            Fluid resolved = BuiltInRegistries.FLUID.getValue(fluidId);
            if (resolved == Fluids.EMPTY) {
                throw new IllegalStateException("Unknown fluid id '" + fluidId + "' for drive " + id);
            }
            return new InfinityDrive(createItemProperties(), InfinityContent.of(new FluidStackTemplate(resolved, 1000)));
        }

        if (itemId != null) {
            Item resolved = BuiltInRegistries.ITEM.getValue(itemId);
            if (resolved == Items.AIR) {
                throw new IllegalStateException("Unknown item id '" + itemId + "' for drive " + id);
            }
            return new InfinityDrive(createItemProperties(), InfinityContent.of(new ItemStackTemplate(resolved)));
        }

        throw new IllegalStateException("InfinityDriveBuilder for " + id + " has no fluid or item set!");
    }
}