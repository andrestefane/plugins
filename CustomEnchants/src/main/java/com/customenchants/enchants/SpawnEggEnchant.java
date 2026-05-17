package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * 🥚 CAPTURADOR - Probabilidad de que los mobs suelten su spawn egg al morir.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class SpawnEggEnchant extends CustomEnchant {

    public SpawnEggEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "SPAWN_EGG",
                "&a🥚 Capturador",
                "Probabilidad de que los mobs suelten su spawn egg",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    /**
     * Probabilidad de soltar spawn egg. Nivel 1 → 3%, 2 → 6%, 3 → 9%
     */
    public double getDropChance(int level) {
        return getConfigDouble("drop-chance-per-level", 0.03) * level;
    }

    /**
     * Devuelve el Material del spawn egg para el tipo de entidad dado.
     * Retorna null si no existe spawn egg para ese mob.
     */
    public Material getSpawnEgg(EntityType type) {
        String name = type.name() + "_SPAWN_EGG";
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null; // No existe spawn egg para este mob (e.g. WITHER, ENDER_DRAGON)
        }
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
