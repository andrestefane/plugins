package com.customenchants.gui;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.listeners.CreativeTabListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Listener para bloquear clicks dentro de las GUIs de CustomEnchants
 * y gestionar la navegación entre ellas.
 */
public class GUIListener implements Listener {

    private static final String GUI_TITLE_MAIN  = ChatColor.DARK_PURPLE + "✨ Encantamientos Personalizados";

    private final CustomEnchantsPlugin plugin;
    private final CreativeTabListener booksGui;

    public GUIListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.booksGui = new CreativeTabListener(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        // GUI principal de encantamientos
        if (title.equals(GUI_TITLE_MAIN)) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            // Click en el botón de libros (ENCHANTED_BOOK con lore que dice "Ver Libros")
            if (clicked.getType() == Material.ENCHANTED_BOOK && hasLoreLine(clicked, "Ver Libros")) {
                player.closeInventory();
                booksGui.openBooksGUI(player);
            }
        }
    }

    private boolean hasLoreLine(ItemStack item, String text) {
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        List<String> lore = meta.getLore();
        if (lore == null) return false;
        return lore.stream().anyMatch(l -> ChatColor.stripColor(l).contains(text));
    }
}
