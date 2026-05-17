package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * 🩸 ROBO DE VIDA - Cura al jugador al golpear enemigos.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class LifeStealEnchant extends CustomEnchant {

    public LifeStealEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "LIFE_STEAL",
                "&c🩸 Robo de Vida",
                "Roba vida al golpear enemigos",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public double getHealAmount(int level) {
        return getConfigDouble("heal-per-hit", 1.5) * level;
    }

    public double getTriggerChance(int level) {
        return Math.min(1.0, getConfigDouble("trigger-chance", 0.20) * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
