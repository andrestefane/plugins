package com.customenchants.gui;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Construye y gestiona la GUI de encantamientos personalizados.
 */
public class EnchantGUI {

    private static final Component GUI_TITLE = Component.text("✨ Encantamientos Personalizados", NamedTextColor.LIGHT_PURPLE);
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

    private final EnchantManager manager;

    public EnchantGUI(CustomEnchantsPlugin plugin) {
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

        // Nombre del encantamiento - convertir de String coloreado a Component
        Component displayName = Component.text(enchant.getPlainDisplayName())
                .color(getColorFromEnchant(enchant.getId()));
        meta.displayName(displayName);

        // Lore con Components
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("─────────────────", NamedTextColor.DARK_GRAY));
        lore.add(Component.text("📖 " + enchant.getDescription(), NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Nivel máximo: " + CustomEnchant.toRoman(enchant.getMaxLevel()), NamedTextColor.YELLOW));
        lore.add(Component.empty());

        // Mostrar en qué items aplica (resumido)
        String applies = getAppliesTo(enchant);
        lore.add(Component.text("Aplica a: " + applies, NamedTextColor.AQUA));
        lore.add(Component.empty());

        // Dónde conseguirlo
        lore.add(Component.text("¿Dónde conseguirlo?", NamedTextColor.GREEN));
        lore.add(Component.text(" ⬦ Cofres del mundo", NamedTextColor.GRAY));
        lore.add(Component.text(" ⬦ Aldeanos armeros/herreros", NamedTextColor.GRAY));
        lore.add(Component.text(" ⬦ Mesa de encantamientos (nv. 15+)", NamedTextColor.GRAY));

        // Info extra para admins
        if (player.hasPermission("customenchants.admin")) {
            lore.add(Component.empty());
            lore.add(Component.text("[Admin] ID: " + enchant.getId(), NamedTextColor.GOLD));
            lore.add(Component.text("Cmd: /encantamiento dar " + enchant.getId() + " <nivel>", NamedTextColor.WHITE));
        }

        lore.add(Component.empty());
        lore.add(Component.text("─────────────────", NamedTextColor.DARK_GRAY));

        meta.lore(lore);
        
        // Efecto de encantamiento visual - usar Enchantment.UNBREAKING sin deprecación
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

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
            meta.displayName(Component.text(" "));
            glass.setItemMeta(meta);
        }
        return glass;
    }

    private ItemStack buildInfoItem(Player player) {
        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = info.getItemMeta();
        if (meta == null) return info;
        
        meta.displayName(Component.text("ℹ Información", NamedTextColor.GOLD));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Total de encantamientos: " + manager.getEnabled().size(), NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("Los encantamientos custom aparecen", NamedTextColor.YELLOW));
        lore.add(Component.text("en el lore del item en gris.", NamedTextColor.YELLOW));
        
        if (player.hasPermission("customenchants.admin")) {
            lore.add(Component.empty());
            lore.add(Component.text("[Admin] Usa /encantamiento dar <ID> <nivel>", NamedTextColor.GOLD));
            lore.add(Component.text("para dar encantamientos manualmente.", NamedTextColor.GOLD));
        }
        
        meta.lore(lore);
        info.setItemMeta(meta);
        return info;
    }

    /** Botón para abrir la lista de libros encantados */
    private ItemStack buildBooksButton() {
        ItemStack btn = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = btn.getItemMeta();
        if (meta == null) return btn;
        
        meta.displayName(Component.text("📖 Ver Libros Encantados", NamedTextColor.AQUA));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Muestra todos los libros", NamedTextColor.GRAY));
        lore.add(Component.text("encantados disponibles.", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("▶ Click para abrir", NamedTextColor.GREEN));
        
        meta.lore(lore);
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

    private TextColor getColorFromEnchant(String id) {
        return switch (id) {
            case "THUNDER_STRIKE" -> NamedTextColor.YELLOW;
            case "LIFE_STEAL" -> NamedTextColor.DARK_RED;
            case "EXPLOSIVE_ARROWS" -> NamedTextColor.GOLD;
            case "VAMPIRISM" -> NamedTextColor.DARK_PURPLE;
            case "BERSERKER" -> NamedTextColor.RED;
            case "ICE_ASPECT" -> NamedTextColor.AQUA;
            case "MAGNETIC" -> NamedTextColor.BLUE;
            case "AUTO_REPAIR" -> NamedTextColor.GREEN;
            case "LUCKY_MINER" -> NamedTextColor.DARK_GREEN;
            case "WEBBING" -> NamedTextColor.WHITE;
            case "SOULBOUND" -> NamedTextColor.LIGHT_PURPLE;
            case "POISON_EDGE" -> NamedTextColor.DARK_GREEN;
            case "NETHER_SLAYER" -> NamedTextColor.RED;
            case "XP_BOOST" -> NamedTextColor.DARK_AQUA;
            case "HEAD_HUNTER" -> NamedTextColor.DARK_GRAY;
            case "SPAWN_EGG" -> NamedTextColor.GREEN;
            case "TUNNEL" -> NamedTextColor.GRAY;
            case "AUTO_SMELT" -> NamedTextColor.GOLD;
            default -> NamedTextColor.GRAY;
        };
    }
}