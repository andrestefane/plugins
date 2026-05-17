package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * ❄️ ASPECTO GÉLIDO - Congela al enemigo al golpear.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class IceAspectEnchant extends CustomEnchant {

    public IceAspectEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "ICE_ASPECT",
                "&b❄ Aspecto Gélido",
                "Congela al enemigo al golpear",
                2,
                MaterialLists.MELEE_WEAPONS
        );
    }

    public int getFreezeDuration(int level) {
        int base = getConfigInt("freeze-duration-ticks", 60);
        return base * level;
    }

    public double getTriggerChance(int level) {
        return Math.min(1.0, getConfigDouble("trigger-chance", 0.25) * level);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
