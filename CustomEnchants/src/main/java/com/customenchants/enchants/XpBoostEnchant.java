package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * ✨ COSECHADOR DE ALMAS - Los mobs sueltan más experiencia al morir.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class XpBoostEnchant extends CustomEnchant {

    public XpBoostEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "XP_BOOST",
                "&3✨ Cosechador de Almas",
                "Los mobs sueltan más experiencia al morir",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    /**
     * Multiplicador de XP adicional.
     * Nivel 1 → +25%, Nivel 2 → +50%, Nivel 3 → +75%
     */
    public double getXpMultiplier(int level) {
        return 1.0 + (getConfigDouble("xp-bonus-per-level", 0.25) * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
