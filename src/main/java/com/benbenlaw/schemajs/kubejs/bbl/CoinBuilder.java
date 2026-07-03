package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.infinitystorage.item.InfinityContent;
import com.benbenlaw.infinitystorage.item.InfinityDrive;
import com.benbenlaw.shops.item.CoinItem;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import org.jspecify.annotations.NonNull;

public class CoinBuilder extends ItemBuilder {

    public int value;

    public CoinBuilder(Identifier id) {
        super(id);
    }

    @Info("Value of the coin")
    public CoinBuilder value(int value) {
        this.value = value;
        return this;
    }

    @Override
    public @NonNull Item createObject() {
        return new CoinItem(createItemProperties(), this.value);
    }
}