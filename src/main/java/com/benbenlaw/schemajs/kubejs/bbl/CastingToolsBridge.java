package com.benbenlaw.schemajs.kubejs.bbl;

import com.benbenlaw.castingtools.item.CTDataComponent;
import com.benbenlaw.castingtools.item.ModifierComponent;
import com.benbenlaw.castingtools.modifier.Modifier;
import com.benbenlaw.castingtools.modifier.ModifierRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CastingToolsBridge {

    public static boolean hasModifierEnchant(ItemStack stack, String enchantId) {
        return getModifierEnchantLevel(stack, enchantId) > 0;
    }

    public static int getModifierEnchantLevel(ItemStack stack, String enchantId) {
        ModifierComponent comp = stack.get(CTDataComponent.MODIFIER_COMPONENT);
        if (comp == null) return 0;

        Identifier target = Identifier.parse(enchantId);
        int total = 0;

        for (var entry : comp.modifiers().entrySet()) {
            Modifier modifier = ModifierRegistry.MODIFIER_REGISTRY.getValue(entry.getKey());
            if (modifier == null) continue;

            Optional<Identifier> associated = modifier.getAssociatedEnchantment();
            if (associated.isPresent() && associated.get().equals(target)) {
                total += entry.getValue();
            }
        }
        return total;
    }
}