package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.utils.MaterialLists;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * 💀 VINCULADO AL ALMA - El item se mantiene al morir en lugar de caer.
 * Aplicable a: Cualquier equipo
 */
public class SoulboundEnchant extends CustomEnchant {

    public SoulboundEnchant(CustomEnchantsPlugin plugin) {
        super(plugin,
                "SOULBOUND",
                "&d💀 Vinculado al Alma",
                "El item no se pierde al morir",
                1,
                buildList()
        );
    }

    private static List<Material> buildList() {
        List<Material> all = new ArrayList<>();
        all.addAll(MaterialLists.MELEE_WEAPONS);
        all.addAll(MaterialLists.PICKAXES);
        all.addAll(MaterialLists.ARMOR);
        all.add(Material.BOW);
        all.add(Material.CROSSBOW);
        return all;
    }

    @Override
    public String getFullDescription() {
        return getDescription() + " (Nivel máximo: " + getMaxLevel() + ")";
    }
}
