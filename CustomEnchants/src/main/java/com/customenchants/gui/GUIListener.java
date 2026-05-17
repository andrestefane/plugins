package com.customenchants.gui;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.listeners.CreativeTabListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

    private static final Component GUI_TITLE_MAIN = Component.text("✨ Encantamientos Personalizados", NamedTextColor.LIGHT_PURPLE);

    private final CreativeTabListener booksGui;

    public GUIListener(CustomEnchantsPlugin plugin) {
        this.booksGui = new CreativeTabListener(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Component title = event.getView().title();

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
        List<Component> lore = meta.lore();
        if (lore == null) return false;
        return lore.stream()
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .anyMatch(line -> line.contains(text));
    }
}
