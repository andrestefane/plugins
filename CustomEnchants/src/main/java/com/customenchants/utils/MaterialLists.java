package com.customenchants.utils;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Listas de materiales compartidas para los encantamientos.
 */
public class MaterialLists {

    /** Todas las espadas */
    public static final List<Material> SWORDS = Arrays.asList(
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD
    );

    /** Todas las hachas */
    public static final List<Material> AXES = Arrays.asList(
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    );

    /** Espadas + Hachas + Tridente + Maza */
    public static final List<Material> MELEE_WEAPONS = Arrays.asList(
            // Espadas
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            // Hachas
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            // Tridente
            Material.TRIDENT,
            // Maza (1.21+)
            Material.MACE
    );

    /** Picos */
    public static final List<Material> PICKAXES = Arrays.asList(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
    );

    /** Armadura completa */
    public static final List<Material> ARMOR = Arrays.asList(
            Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
            Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    );

    /** Botas */
    public static final List<Material> BOOTS = Arrays.asList(
            Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    );

    /** Todo con durabilidad */
    public static List<Material> ALL_DURABLE() {
        java.util.List<Material> all = new java.util.ArrayList<>();
        all.addAll(MELEE_WEAPONS);
        all.addAll(PICKAXES);
        all.addAll(ARMOR);
        all.addAll(Arrays.asList(Material.BOW, Material.CROSSBOW,
                Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
                Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
                Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
                Material.FISHING_ROD, Material.FLINT_AND_STEEL, Material.SHEARS));
        return all;
    }
}
