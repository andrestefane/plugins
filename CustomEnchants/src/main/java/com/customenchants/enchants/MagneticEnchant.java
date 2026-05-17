package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import org.bukkit.Material;

import java.util.Arrays;

/**
 * 🧲 MAGNÉTICO - Atrae items y XP cercanos automáticamente.
 * Aplicable a: Botas
 */
public class MagneticEnchant extends CustomEnchant {

    public MagneticEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "MAGNETIC",
                "&5🧲 Magnético",
                "Atrae items y experiencia cercanos",
                3,
                Arrays.asList(
                        Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS,
                        Material.IRON_BOOTS, Material.GOLDEN_BOOTS,
                        Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
                )
        );
    }

    public double getRadius(int level) {
        double base = getConfigDouble("radius", 5.0);
        return base + (level - 1) * 2.0;
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
