package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import org.bukkit.Material;

import java.util.Arrays;

/**
 * 🍀 MINERO AFORTUNADO - Probabilidad de duplicar drops al minar.
 * Aplicable a: Picos
 */
public class LuckyMinerEnchant extends CustomEnchant {

    public LuckyMinerEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "LUCKY_MINER",
                "&2🍀 Minero Afortunado",
                "Probabilidad de duplicar drops al minar",
                3,
                Arrays.asList(
                        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE,
                        Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE,
                        Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
                )
        );
    }

    public double getDoubleDropChance(int level) {
        return Math.min(1.0, getConfigDouble("double-drop-chance", 0.20) * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
