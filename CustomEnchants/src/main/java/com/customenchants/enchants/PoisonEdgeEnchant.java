package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * ☠️ FILO VENENOSO - Envenena al enemigo al golpear.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class PoisonEdgeEnchant extends CustomEnchant {

    public PoisonEdgeEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "POISON_EDGE",
                "&2☠ Filo Venenoso",
                "Envenena a los enemigos al golpear",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public double getTriggerChance(int level) {
        return Math.min(1.0, getConfigDouble("trigger-chance", 0.20) * level);
    }

    public int getPoisonDuration(int level) {
        return getConfigInt("poison-duration-ticks", 100) * level;
    }

    public int getPoisonAmplifier(int level) {
        return level - 1; // 0 = Veneno I, 1 = Veneno II, etc.
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
