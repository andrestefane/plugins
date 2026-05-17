package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * 💀 CAZADOR DE CABEZAS - Probabilidad de que los mobs suelten su cabeza al morir.
 * Aplicable a: Espadas, Hachas, Tridente, Maza
 */
public class HeadHunterEnchant extends CustomEnchant {

    public HeadHunterEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "HEAD_HUNTER",
                "&8💀 Cazador de Cabezas",
                "Probabilidad de que los mobs suelten su cabeza",
                3,
                MaterialLists.MELEE_WEAPONS
        );
    }

    /**
     * Probabilidad de soltar cabeza. Nivel 1 → 5%, 2 → 10%, 3 → 15%
     */
    public double getDropChance(int level) {
        return getConfigDouble("drop-chance-per-level", 0.05) * level;
    }

    /**
     * Devuelve el Material de la cabeza correspondiente al tipo de entidad.
     * Retorna null si no tiene cabeza vanilla o skull definida.
     */
    public Material getHeadMaterial(EntityType type) {
        return switch (type) {
            case SKELETON       -> Material.SKELETON_SKULL;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED -> Material.ZOMBIE_HEAD;
            case CREEPER        -> Material.CREEPER_HEAD;
            case PLAYER         -> Material.PLAYER_HEAD;
            case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN -> Material.PIGLIN_HEAD;
            // Para el resto usamos una cabeza de mob genérica (skull con texture vía NBT sería ideal,
            // pero aquí devolvemos PLAYER_HEAD como fallback — se puede extender con NBT si se desea)
            default             -> Material.PLAYER_HEAD;
        };
    }
    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }}
