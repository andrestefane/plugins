package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import org.bukkit.Material;

import java.util.Arrays;

/**
 * 🔧 AUTO-REPARACIÓN - Repara automáticamente el item con el tiempo.
 * INCOMPATIBLE con Mending de Minecraft vanilla.
 * Aplicable a: Todos los items con durabilidad
 */
public class AutoRepairEnchant extends CustomEnchant {

    public AutoRepairEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "AUTO_REPAIR",
                "&a🔧 Auto-Reparación",
                "El item se repara solo con el tiempo",
                3,
                Arrays.asList(
                        Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
                        Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
                        Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                        Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
                        Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
                        Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
                        Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
                        Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                        Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
                        Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                        Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
                        Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
                        Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
                        Material.BOW, Material.CROSSBOW, Material.TRIDENT
                )
        );
    }

    public int getRepairIntervalTicks() {
        return getConfigInt("repair-interval-ticks", 6000);
    }

    public int getDurabilityPerTick(int level) {
        return getConfigInt("durability-per-level", 5) * level;
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }

    /**
     * Comprueba si el item tiene el encantamiento Mending de vanilla.
     * Si tiene Mending, Auto-Reparación no se puede aplicar.
     */
    public boolean hasMending(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.containsEnchantment(org.bukkit.enchantments.Enchantment.MENDING);
    }

    @Override
    public boolean canApplyTo(org.bukkit.inventory.ItemStack item) {
        if (hasMending(item)) return false;
        return super.canApplyTo(item);
    }
}
