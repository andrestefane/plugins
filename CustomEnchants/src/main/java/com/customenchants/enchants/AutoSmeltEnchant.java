package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔥 AUTO-FUNDICIÓN - Las menas se funden automáticamente al minarlas.
 * Aplicable a: Picos
 */
public class AutoSmeltEnchant extends CustomEnchant {

    /** Mapa de mena → resultado fundido */
    public static final Map<Material, Material> SMELT_MAP = new HashMap<>() {{
        // Menas de piedra → lingotes/materiales
        put(Material.IRON_ORE,         Material.IRON_INGOT);
        put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        put(Material.GOLD_ORE,         Material.GOLD_INGOT);
        put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        put(Material.COPPER_ORE,       Material.COPPER_INGOT);
        put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
        put(Material.NETHER_GOLD_ORE,  Material.GOLD_NUGGET);
        put(Material.ANCIENT_DEBRIS,   Material.NETHERITE_SCRAP);

        // Menas que normalmente ya dropean su recurso (coal, diamond, etc.)
        // pero con Smelting Touch convierten la piedra en la mena fundida:
        put(Material.COAL_ORE,         Material.COAL);
        put(Material.DEEPSLATE_COAL_ORE, Material.COAL);
        put(Material.DIAMOND_ORE,      Material.DIAMOND);
        put(Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND);
        put(Material.EMERALD_ORE,      Material.EMERALD);
        put(Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD);
        put(Material.LAPIS_ORE,        Material.LAPIS_LAZULI);
        put(Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_LAZULI);
        put(Material.REDSTONE_ORE,     Material.REDSTONE);
        put(Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE);
        put(Material.NETHER_QUARTZ_ORE, Material.QUARTZ);

        // Sand → glass
        put(Material.SAND,             Material.GLASS);
        put(Material.RED_SAND,         Material.GLASS); // o podría ser BROWN_STAINED_GLASS
        put(Material.COBBLESTONE,      Material.STONE);
        put(Material.COBBLED_DEEPSLATE, Material.DEEPSLATE);
    }};

    public AutoSmeltEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "AUTO_SMELT",
                "&c🔥 Auto-Fundición",
                "Las menas se funden automáticamente al minarlas",
                1,
                MaterialLists.PICKAXES
        );
    }

    /**
     * Devuelve el material fundido para una mena dada, o null si no aplica.
     */
    public Material getSmeltResult(Material ore) {
        return SMELT_MAP.get(ore);
    }

    /**
     * Auto-fundición es incompatible con Silk Touch (no tendría sentido).
     */
    @Override
    public boolean canApplyTo(org.bukkit.inventory.ItemStack item) {
        if (item != null && item.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
            return false;
        }
        return super.canApplyTo(item);
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
