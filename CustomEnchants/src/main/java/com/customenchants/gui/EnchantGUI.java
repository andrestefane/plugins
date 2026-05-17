package com.customenchants.gui;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Construye y gestiona la GUI de encantamientos personalizados.
 */
public class EnchantGUI {

    private static final String GUI_TITLE = ChatColor.DARK_PURPLE + "✨ Encantamientos Personalizados";
    private static final int GUI_SIZE = 54; // 6 filas

    // Materiales representativos para cada encantamiento
    private static final java.util.Map<String, Material> ENCHANT_ICONS = new java.util.HashMap<>() {{
        put("THUNDER_STRIKE",   Material.LIGHTNING_ROD);
        put("LIFE_STEAL",       Material.NETHER_STAR);
        put("EXPLOSIVE_ARROWS", Material.TNT);
        put("VAMPIRISM",        Material.SPIDER_EYE);
        put("BERSERKER",        Material.BLAZE_POWDER);
        put("ICE_ASPECT",       Material.PACKED_ICE);
        put("MAGNETIC",         Material.COMPASS);
        put("AUTO_REPAIR",      Material.ANVIL);
        put("LUCKY_MINER",      Material.EMERALD);
        put("WEBBING",          Material.COBWEB);
        put("SOULBOUND",        Material.ENDER_PEARL);
        put("POISON_EDGE",      Material.POISONOUS_POTATO);
        // Nuevos
        put("NETHER_SLAYER",    Material.BLAZE_ROD);
        put("XP_BOOST",         Material.EXPERIENCE_BOTTLE);
        put("HEAD_HUNTER",      Material.SKELETON_SKULL);
        put("SPAWN_EGG",        Material.CREEPER_SPAWN_EGG);
        put("TUNNEL",           Material.RAIL);
        put("AUTO_SMELT",       Material.FURNACE);
    }};

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;

    public EnchantGUI(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    /**
     * Abre la GUI para el jugador dado.
     */
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // Rellenar bordes con cristal decorativo
        fillBorders(inv);

        // Colocar encantamientos en el centro (filas 2-5, columnas 2-8)
        List<CustomEnchant> enchants = manager.getEnabled();
        int[] slots = getCenterSlots(enchants.size());

        for (int i = 0; i < enchants.size() && i < slots.length; i++) {
            CustomEnchant enchant = enchants.get(i);
            inv.setItem(slots[i], buildEnchantItem(enchant, player));
        }

        // Panel de información en la última fila
        inv.setItem(48, buildInfoItem(player));
        inv.setItem(50, buildBooksButton());

        player.openInventory(inv);
    }

    private ItemStack buildEnchantItem(CustomEnchant enchant, Player player) {
        Material icon = ENCHANT_ICONS.getOrDefault(enchant.getId(), Material.BOOK);
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Nombre del encantamiento
        meta.setDisplayName(enchant.getColoredDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "─────────────────");
        lore.add(ChatColor.GRAY + "📖 " + enchant.getDescription());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Nivel máximo: " + ChatColor.WHITE + CustomEnchant.toRoman(enchant.getMaxLevel()));
        lore.add("");

        // Mostrar en qué items aplica (resumido)
        String applies = getAppliesTo(enchant);
        lore.add(ChatColor.AQUA + "Aplica a: " + ChatColor.WHITE + applies);
        lore.add("");

        // Dónde conseguirlo
        lore.add(ChatColor.GREEN + "¿Dónde conseguirlo?");
        lore.add(ChatColor.GRAY + " ⬦ Cofres del mundo");
        lore.add(ChatColor.GRAY + " ⬦ Aldeanos armeros/herreros");
        lore.add(ChatColor.GRAY + " ⬦ Mesa de encantamientos (nv. 15+)");

        // Info extra para admins
        if (player.hasPermission("customenchants.admin")) {
            lore.add("");
            lore.add(ChatColor.GOLD + "[Admin] ID: " + ChatColor.WHITE + enchant.getId());
            lore.add(ChatColor.GOLD + "Cmd: " + ChatColor.WHITE + "/encantamiento dar " + enchant.getId() + " <nivel>");
        }

        lore.add(ChatColor.DARK_GRAY + "─────────────────");

        meta.setLore(lore);
        // Efecto de encantamiento visual
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    private void fillBorders(Inventory inv) {
        ItemStack border = buildBorderGlass();
        // Fila 1 (0-8) y fila 6 (45-53)
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(45 + i, border);
        }
        // Columnas 1 y 9 (filas 2-5)
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9, border);
            inv.setItem(row * 9 + 8, border);
        }
    }

    private ItemStack buildBorderGlass() {
        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        return glass;
    }

    private ItemStack buildInfoItem(Player player) {
        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = info.getItemMeta();
        if (meta == null) return info;
        meta.setDisplayName(ChatColor.GOLD + "ℹ Información");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Total de encantamientos: " + ChatColor.WHITE + manager.getEnabled().size());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Los encantamientos custom aparecen");
        lore.add(ChatColor.YELLOW + "en el lore del item en gris.");
        if (player.hasPermission("customenchants.admin")) {
            lore.add("");
            lore.add(ChatColor.GOLD + "[Admin] Usa /encantamiento dar <ID> <nivel>");
            lore.add(ChatColor.GOLD + "para dar encantamientos manualmente.");
        }
        meta.setLore(lore);
        info.setItemMeta(meta);
        return info;
    }

    /** Botón para abrir la lista de libros encantados */
    private ItemStack buildBooksButton() {
        ItemStack btn = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = btn.getItemMeta();
        if (meta == null) return btn;
        meta.setDisplayName(ChatColor.AQUA + "📖 Ver Libros Encantados");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Muestra todos los libros");
        lore.add(ChatColor.GRAY + "encantados disponibles.");
        lore.add("");
        lore.add(ChatColor.GREEN + "▶ Click para abrir");
        meta.setLore(lore);
        btn.setItemMeta(meta);
        return btn;
    }

    // Slots centrales (evitando bordes)
    private int[] getCenterSlots(int count) {
        // Slots disponibles: filas 2-5, columnas 2-8 = 28 slots
        int[] available = new int[28];
        int idx = 0;
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                available[idx++] = row * 9 + col;
            }
        }
        return available;
    }

    private String getAppliesTo(CustomEnchant enchant) {
        List<Material> mats = enchant.getApplicableMaterials();
        if (mats.isEmpty()) return "Ninguno";
        // Detectar categorías
        boolean hasSwords = mats.stream().anyMatch(m -> m.name().endsWith("_SWORD"));
        boolean hasAxes = mats.stream().anyMatch(m -> m.name().endsWith("_AXE"));
        boolean hasPickaxes = mats.stream().anyMatch(m -> m.name().endsWith("_PICKAXE"));
        boolean hasBows = mats.stream().anyMatch(m -> m.name().equals("BOW") || m.name().equals("CROSSBOW"));
        boolean hasArmor = mats.stream().anyMatch(m -> m.name().endsWith("_BOOTS") || m.name().endsWith("_HELMET"));

        List<String> categories = new ArrayList<>();
        if (hasSwords) categories.add("Espadas");
        if (hasAxes) categories.add("Hachas");
        if (hasPickaxes) categories.add("Picos");
        if (hasBows) categories.add("Arcos");
        if (hasArmor) categories.add("Armadura");
        return String.join(", ", categories);
    }
}
