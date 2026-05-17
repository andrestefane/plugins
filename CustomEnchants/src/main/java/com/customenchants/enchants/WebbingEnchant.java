package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * 🕸️ TELARAÑA - Atrapa temporalmente al enemigo al golpear.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class WebbingEnchant extends CustomEnchant {

    public WebbingEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "WEBBING",
                "&f🕸 Telaraña",
                "Atrapa al enemigo con telarañas al golpear",
                2,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public double getTriggerChance(int level) {
        return Math.min(1.0, getConfigDouble("trigger-chance", 0.20) * level);
    }

    public int getWebDuration(int level) {
        return getConfigInt("web-duration-ticks", 100) * level;
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
