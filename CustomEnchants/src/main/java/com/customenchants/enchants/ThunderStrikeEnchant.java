package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * ⚡ GOLPE RELÁMPAGO - Al golpear, probabilidad de caer un rayo sobre el enemigo.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class ThunderStrikeEnchant extends CustomEnchant {

    public ThunderStrikeEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "THUNDER_STRIKE",
                "&e⚡ Golpe Relámpago",
                "Chance de invocar un rayo al golpear",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public double getTriggerChance(int level) {
        double base = getConfigDouble("trigger-chance", 0.15);
        return base * level;
    }

    public double getDamageMultiplier() {
        return getConfigDouble("damage-multiplier", 2.5);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
