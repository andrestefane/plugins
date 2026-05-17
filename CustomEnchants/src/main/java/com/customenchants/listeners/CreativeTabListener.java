package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.managers.CreativeTabManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabListener implements Listener {

    // Título de la GUI (solo para mostrar al jugador)
    private static final Component GUI_TITLE_BOOKS =
            Component.text(" Libros Encantados", NamedTextColor.DARK_PURPLE);

    // Clave PDC para marcar la lore extra de creativo en el libro
    private static final String CREATIVE_LORE_KEY = "creative_lore_marker";

    private final CreativeTabManager tabManager;
    private final NamespacedKey creativeLoreKey;

    public CreativeTabListener(CustomEnchantsPlugin plugin) {
        this.tabManager = new CreativeTabManager(plugin);
        this.creativeLoreKey = new NamespacedKey(plugin, CREATIVE_LORE_KEY);
    }

    // -------------------------------------------------------------------------
    // Apertura de GUI
    // -------------------------------------------------------------------------

    public void openBooksGUI(Player player) {
        List<ItemStack> books = tabManager.getAllEnchantedBooks();

        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, GUI_TITLE_BOOKS);

        // Bordes con cristal morado
        ItemStack border = buildBorder();
        for (int i = 0; i < 9; i++)          inv.setItem(i, border);
        for (int i = 45; i < 54; i++)        inv.setItem(i, border);
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9,     border);
            inv.setItem(row * 9 + 8, border);
        }

        // Libros en slots interiores
        int[] innerSlots = getInnerSlots();
        for (int i = 0; i < books.size() && i < innerSlots.length; i++) {
            ItemStack book = books.get(i).clone();
            addCreativeLore(book, player);
            inv.setItem(innerSlots[i], book);
        }

        // Botón informativo central
        inv.setItem(49, buildInfoButton(player));

        player.openInventory(inv);
    }

    // -------------------------------------------------------------------------
    // Evento de click
    // -------------------------------------------------------------------------

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Identificamos la GUI por su título usando el serializer de Adventure
        // (comparar Component directamente es lo más seguro en Paper 1.21)
        Component viewTitle = event.getView().title();
        if (!viewTitle.equals(GUI_TITLE_BOOKS)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.PURPLE_STAINED_GLASS_PANE) return;
        if (clicked.getType() == Material.KNOWLEDGE_BOOK) return;
        if (clicked.getType() != Material.ENCHANTED_BOOK) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            ItemStack bookToGive = clicked.clone();
            removeCreativeLore(bookToGive);
            player.getInventory().addItem(bookToGive);
            player.sendActionBar(
                    Component.text("📖 Libro añadido al inventario", NamedTextColor.GREEN));
        } else {
            player.sendActionBar(
                    Component.text("Solo disponible en modo creativo", NamedTextColor.RED));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers de lore creativo
    // -------------------------------------------------------------------------

    /**
     * Añade dos líneas al lore del libro para indicar que se puede hacer click,
     * y marca el item con una PDC tag para poder identificar y eliminar esas
     * líneas de forma fiable sin depender de comparación de texto visible.
     */
    private void addCreativeLore(ItemStack book, Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) return;
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        else lore = new ArrayList<>(lore); // copia mutable

        lore.add(Component.empty());
        lore.add(Component.text("▶ Click para añadir al inventario", NamedTextColor.GREEN));

        meta.lore(lore);

        // Marcamos cuántas líneas extra añadimos para quitarlas de forma exacta
        meta.getPersistentDataContainer().set(creativeLoreKey, PersistentDataType.INTEGER, 2);

        book.setItemMeta(meta);
    }

    /**
     * Elimina las líneas extra que añadió {@link #addCreativeLore} usando la
     * PDC tag como guía, sin comparar texto serializado.
     */
    private void removeCreativeLore(ItemStack book) {
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return;

        Integer extraLines = meta.getPersistentDataContainer()
                .get(creativeLoreKey, PersistentDataType.INTEGER);
        if (extraLines == null || extraLines <= 0) return;

        List<Component> lore = meta.lore();
        if (lore == null || lore.size() < extraLines) return;

        // Las líneas extra están siempre al final
        List<Component> cleaned = new ArrayList<>(lore.subList(0, lore.size() - extraLines));
        meta.lore(cleaned);
        meta.getPersistentDataContainer().remove(creativeLoreKey);

        book.setItemMeta(meta);
    }

    // -------------------------------------------------------------------------
    // Builders de items auxiliares
    // -------------------------------------------------------------------------

    private ItemStack buildInfoButton(Player player) {
        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = info.getItemMeta();
        if (meta == null) return info;

        meta.displayName(Component.text("Uso de libros", NamedTextColor.GOLD));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Combina el libro con tu arma", NamedTextColor.GRAY));
        lore.add(Component.text("en un yunque para encantarla.", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Combina dos libros del mismo", NamedTextColor.GRAY));
        lore.add(Component.text("nivel para subir al siguiente.", NamedTextColor.GRAY));
        if (player.getGameMode() == GameMode.CREATIVE) {
            lore.add(Component.empty());
            lore.add(Component.text("✔ Click: añadir libro al inventario", NamedTextColor.GREEN));
        }
        meta.lore(lore);
        info.setItemMeta(meta);
        return info;
    }

    private ItemStack buildBorder() {
        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            // Nombre vacío con itálica desactivada para que no aparezca "Air" ni cursiva
            meta.displayName(Component.text(" "));
            glass.setItemMeta(meta);
        }
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