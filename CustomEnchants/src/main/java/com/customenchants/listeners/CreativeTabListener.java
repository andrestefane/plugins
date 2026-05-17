package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.CreativeTabManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa una "pestaña" creativa para los libros encantados custom.
 *
 * Como la API de Paper 1.21 no expone registro de pestañas creativas custom
 * de forma directa sin DataPacks, usamos el comando /listarce y una GUI propia.
 * Este listener añade un botón en la GUI de lista que permite sacar libros
 * directamente al inventario en modo creativo.
 *
 * Adicionalmente, cuando un jugador en creativo escribe /listarce, la GUI
 * incluye botones para dar cada libro directamente.
 */
public class CreativeTabListener implements Listener {

    private static final String GUI_TITLE_BOOKS = ChatColor.DARK_PURPLE + "📖 Libros Encantados";

    private final CustomEnchantsPlugin plugin;
    private final CreativeTabManager tabManager;

    public CreativeTabListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.tabManager = new CreativeTabManager(plugin);
    }

    /**
     * Abre la GUI de libros encantados para el jugador dado.
     */
    public void openBooksGUI(Player player) {
        List<ItemStack> books = tabManager.getAllEnchantedBooks();

        // Calcular tamaño del inventario (múltiplo de 9, mínimo 9)
        int size = (int) Math.ceil(books.size() / 9.0) * 9;
        size = Math.max(9, Math.min(size + 9, 54)); // máximo 54 slots, con fila extra para bordes

        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, GUI_TITLE_BOOKS);

        // Rellenar bordes
        ItemStack border = buildBorder();
        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = 45; i < 54; i++) inv.setItem(i, border);
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9, border);
            inv.setItem(row * 9 + 8, border);
        }

        // Colocar libros en slots interiores
        int[] innerSlots = getInnerSlots();
        for (int i = 0; i < books.size() && i < innerSlots.length; i++) {
            ItemStack book = books.get(i).clone();
            // Añadir indicación en lore para modo creativo
            addCreativeLore(book, player);
            inv.setItem(innerSlots[i], book);
        }

        // Botón de info en slot central inferior
        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + "ℹ Uso de libros");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Combina el libro con tu arma");
            lore.add(ChatColor.GRAY + "en un yunque para encantarla.");
            lore.add("");
            lore.add(ChatColor.GRAY + "Combina dos libros del mismo");
            lore.add(ChatColor.GRAY + "nivel para subir al siguiente.");
            if (player.getGameMode() == GameMode.CREATIVE) {
                lore.add("");
                lore.add(ChatColor.GREEN + "✔ Click: añadir libro al inventario");
            }
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(49, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE_BOOKS)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.PURPLE_STAINED_GLASS_PANE) return;
        if (clicked.getType() == Material.KNOWLEDGE_BOOK) return;
        if (clicked.getType() != Material.ENCHANTED_BOOK) return;

        // Solo en creativo: dar el libro al jugador
        if (player.getGameMode() == GameMode.CREATIVE) {
            ItemStack bookToGive = clicked.clone();
            // Limpiar la lore extra de "Click para añadir"
            removeCreativeLore(bookToGive);
            player.getInventory().addItem(bookToGive);
            player.sendActionBar(ChatColor.GREEN + "📖 Libro añadido al inventario");
        } else {
            player.sendActionBar(ChatColor.RED + "Solo disponible en modo creativo");
        }
    }

    private void addCreativeLore(ItemStack book, Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) return;
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "▶ Click para añadir al inventario");
        meta.setLore(lore);
        book.setItemMeta(meta);
    }

    private void removeCreativeLore(ItemStack book) {
        ItemMeta meta = book.getItemMeta();
        if (meta == null || !meta.hasLore()) return;
        List<String> lore = meta.getLore();
        if (lore == null) return;
        lore.removeIf(line -> ChatColor.stripColor(line).contains("Click para añadir"));
        lore.removeIf(String::isEmpty); // quitar línea vacía extra que añadimos
        meta.setLore(lore);
        book.setItemMeta(meta);
    }

    private ItemStack buildBorder() {
        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) { meta.setDisplayName(" "); glass.setItemMeta(meta); }
        return glass;
    }

    private int[] getInnerSlots() {
        int[] slots = new int[28];
        int idx = 0;
        for (int row = 1; row <= 4; row++)
            for (int col = 1; col <= 7; col++)
                slots[idx++] = row * 9 + col;
        return slots;
    }
}
