package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * TUNEL - Mina un area alrededor del bloque objetivo.
 * Nivel 1 -> 2x2
 * Nivel 2 -> 3x3
 * Nivel 3 -> 3x3x2
 * Nivel 4 -> 3x3x3
 * Nivel 5 -> 3x3x4
 *
 * Los niveles altos aumentan la profundidad para mantener tuneles centrados.
 * Aplicable a: Picos
 */
public class TunnelEnchant extends CustomEnchant {

    public TunnelEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "TUNNEL",
                "&6Tunel",
                "Mina un tunel centrado y mas profundo por nivel",
                5,
                MaterialLists.PICKAXES
        );
    }

    public int getRadius(int level) {
        return 1;
    }

    public int getSideSize(int level) {
        return level == 1 ? 2 : 3;
    }

    public int getDepth(int level) {
        return Math.max(1, level - 1);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel maximo: " + getMaxLevel() + ")";
    }
}
