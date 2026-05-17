package com.customenchants.vanilla;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomEnchantsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            for (VanillaEnchantSpec spec : VanillaEnchantSpec.all()) {
                event.registry().register(RegistryKey.ENCHANTMENT.typedKey(VanillaEnchantSpec.NAMESPACE + ":" + spec.keyValue()),
                        builder -> configure(builder, spec));
            }
        }));
    }

    private void configure(EnchantmentRegistryEntry.Builder builder, VanillaEnchantSpec spec) {
        List<ItemType> supportedItems = spec.supportedMaterials().stream()
                .filter(Material::isItem)
                .map(Material::asItemType)
                .toList();

        builder.description(Component.text(spec.name()))
                .supportedItems(RegistrySet.keySetFromValues(RegistryKey.ITEM, supportedItems))
                .primaryItems(RegistrySet.keySetFromValues(RegistryKey.ITEM, supportedItems))
                .weight(5)
                .maxLevel(spec.maxLevel())
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 10))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(20, 10))
                .anvilCost(2)
                .activeSlots(spec.activeSlot())
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, List.<io.papermc.paper.registry.TypedKey<Enchantment>>of()));
    }
}
