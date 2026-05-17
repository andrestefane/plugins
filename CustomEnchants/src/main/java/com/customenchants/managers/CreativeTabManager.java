package com.customenchants.managers;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la pestaña del menú creativo con todos los libros encantados custom.
 *
 * Paper 1.21+ soporta pestañas creativas personalizadas vía la API de ItemGroups.
 * Registramos todos los libros (uno por encantamiento y nivel) en la pestaña.
 */
public class CreativeTabManager {

    private final CustomEnchantsPlugin plugin;

    public CreativeTabManager(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Devuelve la lista de todos los libros encantados custom para mostrar en el creativo.
     * Se usa en el listener de tabs.
     */
    public List<ItemStack> getAllEnchantedBooks() {
        List<ItemStack> books = new ArrayList<>();
        for (CustomEnchant enchant : plugin.getEnchantManager().getEnabled()) {
            for (int level = 1; level <= enchant.getMaxLevel(); level++) {
                books.add(enchant.createBook(level));
            }
        }
        return books;
    }

    /**
     * Crea el item icono de la pestaña creativa.
     */
    public ItemStack getTabIcon() {
        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "✨ Encantamientos Custom");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Libros con encantamientos personalizados");
            lore.add(ChatColor.GRAY + "" + plugin.getEnchantManager().getEnabled().size() + " encantamientos disponibles");
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }
}
