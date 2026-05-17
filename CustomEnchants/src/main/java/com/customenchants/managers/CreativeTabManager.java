package com.customenchants.managers;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabManager {

    private final CustomEnchantsPlugin plugin;

    public CreativeTabManager(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
    }

    public List<ItemStack> getAllEnchantedBooks() {
        List<ItemStack> books = new ArrayList<>();
        for (CustomEnchant enchant : plugin.getEnchantManager().getEnabled()) {
            for (int level = 1; level <= enchant.getMaxLevel(); level++) {
                books.add(enchant.createBook(level));
            }
        }
        return books;
    }

    public ItemStack getTabIcon() {
        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.displayName(
                Component.text("Encantamientos Custom", NamedTextColor.GOLD)
            );
            meta.lore(List.of(
                Component.text("Libros con encantamientos personalizados", NamedTextColor.GRAY),
                Component.text(plugin.getEnchantManager().getEnabled().size() + " encantamientos disponibles", NamedTextColor.GRAY)
            ));
            icon.setItemMeta(meta);
        }
        return icon;
    }
}