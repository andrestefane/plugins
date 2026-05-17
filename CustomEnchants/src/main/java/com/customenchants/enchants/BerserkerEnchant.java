package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * 🔥 BERSERKER - Cuanto menos vida tiene el jugador, más daño hace.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class BerserkerEnchant extends CustomEnchant {

    public BerserkerEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "BERSERKER",
                "&c🔥 Berserker",
                "Más daño cuanta menos vida tienes",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    /**
     * Calcula el bonus de daño basado en corazones perdidos.
     * @param missingHearts corazones que faltan (health perdida / 2)
     * @param level nivel del encantamiento
     * @return multiplicador de daño adicional
     */
    public double getDamageBonus(double missingHearts, int level) {
        double bonusPerHeart = getConfigDouble("damage-bonus-per-heart-lost", 0.05);
        return 1.0 + (missingHearts * bonusPerHeart * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
