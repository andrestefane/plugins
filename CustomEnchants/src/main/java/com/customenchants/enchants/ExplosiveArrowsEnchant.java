package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import org.bukkit.Material;

import java.util.Arrays;

/**
 * 💥 FLECHAS EXPLOSIVAS - Las flechas causan una pequeña explosión al impactar.
 * Aplicable a: Arcos, Ballestas
 */
public class ExplosiveArrowsEnchant extends CustomEnchant {

    public ExplosiveArrowsEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "EXPLOSIVE_ARROWS",
                "&6💥 Flechas Explosivas",
                "Las flechas explotan al impactar",
                3,
                Arrays.asList(
                        Material.BOW,
                        Material.CROSSBOW
                )
        );
    }

    public float getExplosionPower(int level) {
        float base = (float) getConfigDouble("explosion-power", 1.5);
        return base + (level - 1) * 0.5f;
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
