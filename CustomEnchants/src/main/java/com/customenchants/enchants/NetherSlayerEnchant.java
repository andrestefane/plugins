package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;

/**
 * 🔱 CAZADOR DEL NETHER - Hace más daño a todos los mobs del Nether.
 * (Blazes, Piglins, Hoglin, Ghast, Wither Skeleton, Magma Cube, Strider, etc.)
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class NetherSlayerEnchant extends CustomEnchant {

    public NetherSlayerEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "NETHER_SLAYER",
                "&6🔱 Cazador del Nether",
                "Más daño a todos los mobs del Nether",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    /**
     * Multiplicador de daño adicional contra mobs del Nether.
     * Nivel 1 → +50%, Nivel 2 → +100%, Nivel 3 → +150%
     */
    public double getDamageMultiplier(int level) {
        return 1.0 + (getConfigDouble("damage-bonus-per-level", 0.5) * level);
    }

    /**
     * Comprueba si el mob es nativo del Nether.
     */
    public static boolean isNetherMob(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case BLAZE, GHAST, WITHER_SKELETON, PIGLIN, PIGLIN_BRUTE,
                 HOGLIN, ZOGLIN, MAGMA_CUBE, STRIDER, ZOMBIFIED_PIGLIN,
                 WITHER -> true;
            default -> false;
        };
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
