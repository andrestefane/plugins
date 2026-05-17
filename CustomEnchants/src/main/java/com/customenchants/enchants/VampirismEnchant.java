package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * 🧛 VAMPIRISMO - Al matar un mob, absorbe corazones adicionales.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class VampirismEnchant extends CustomEnchant {

    public VampirismEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "VAMPIRISM",
                "&4🧛 Vampirismo",
                "Absorbe vida al matar enemigos",
                2,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public double getHealthPerKill(int level) {
        return getConfigDouble("health-per-kill", 2.0) * level;
    }

    public double getAbsorbChance(int level) {
        return Math.min(1.0, getConfigDouble("absorb-chance", 0.30) * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
