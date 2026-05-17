package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.vanilla.VanillaEnchantSpec;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEnchant {

    protected final CustomEnchantsPlugin plugin;
    private final String id;
    private final String displayName;
    private final String description;
    private final int maxLevel;
    private final List<Material> applicableMaterials;

    public CustomEnchant(CustomEnchantsPlugin plugin, String id, String displayName,
                         String description, int maxLevel, List<Material> applicableMaterials) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.maxLevel = maxLevel;
        this.applicableMaterials = applicableMaterials;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getMaxLevel() { return maxLevel; }
    public List<Material> getApplicableMaterials() { return applicableMaterials; }

    public String getPlainDisplayName() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName));
    }

    public String getColoredDisplayName() {
        return getThemeColor() + getPlainDisplayName();
    }

    public boolean canApplyTo(ItemStack item) {
        if (item == null) return false;
        if (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK) return true;
        return applicableMaterials.contains(item.getType());
    }

    public boolean canApplyToItem(ItemStack item) {
        if (item == null) return false;
        return applicableMaterials.contains(item.getType());
    }

    public int getLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;

        Enchantment vanilla = getVanillaEnchantment();
        if (vanilla == null) return 0;

        if (item.getType() == Material.ENCHANTED_BOOK) {
            return getBookLevel(item);
        }

        return item.containsEnchantment(vanilla) ? item.getEnchantmentLevel(vanilla) : 0;
    }

    public ItemStack applyToItem(ItemStack item, int level) {
        if (level < 1 || level > maxLevel) return item;
        if (item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK) {
            return applyToBook(item, level);
        }
        return applyToRegularItem(item, level);
    }

    public ItemStack applyToBook(ItemStack book, int level) {
        if (level < 1) return book;
        if (book.getType() == Material.BOOK) {
            book.setType(Material.ENCHANTED_BOOK);
        }

        ItemMeta meta = book.getItemMeta();
        Enchantment vanilla = getVanillaEnchantment();
        if (meta instanceof EnchantmentStorageMeta storageMeta && vanilla != null) {
            storageMeta.addStoredEnchant(vanilla, level, true);
            removeOldLore(storageMeta);
            book.setItemMeta(storageMeta);
        }
        return book;
    }

    public ItemStack applyToRegularItem(ItemStack item, int level) {
        if (level < 1) return item;

        Enchantment vanilla = getVanillaEnchantment();
        if (vanilla != null) {
            item.addUnsafeEnchantment(vanilla, level);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            removeOldLore(meta);
            item.setItemMeta(meta);
        }
        return item;
    }

    public int getBookLevel(ItemStack book) {
        if (book == null || book.getType() != Material.ENCHANTED_BOOK) return 0;

        ItemMeta meta = book.getItemMeta();
        Enchantment vanilla = getVanillaEnchantment();
        if (meta instanceof EnchantmentStorageMeta storageMeta && vanilla != null
                && storageMeta.hasStoredEnchant(vanilla)) {
            return storageMeta.getStoredEnchantLevel(vanilla);
        }
        return 0;
    }

    public Enchantment getVanillaEnchantment() {
        return Enchantment.getByKey(new NamespacedKey(VanillaEnchantSpec.NAMESPACE, id.toLowerCase()));
    }

    protected int getConfigInt(String path, int defaultValue) {
        return plugin.getConfig().getInt("enchants." + this.id + "." + path, defaultValue);
    }

    protected double getConfigDouble(String path, double defaultValue) {
        return plugin.getConfig().getDouble("enchants." + this.id + "." + path, defaultValue);
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("enchants." + this.id + ".enabled", true);
    }

    public ItemStack createBook(int level) {
        if (level < 1) level = 1;
        if (level > maxLevel) level = maxLevel;
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        return applyToBook(book, level);
    }

    private void removeOldLore(ItemMeta meta) {
        if (!meta.hasLore()) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        List<String> cleaned = new ArrayList<>(lore);
        String searchTag = getPlainDisplayName();
        cleaned.removeIf(line -> ChatColor.stripColor(line).contains(searchTag));
        meta.setLore(cleaned);
    }

    private ChatColor getThemeColor() {
        return switch (id) {
            case "THUNDER_STRIKE" -> ChatColor.YELLOW;
            case "LIFE_STEAL" -> ChatColor.DARK_RED;
            case "EXPLOSIVE_ARROWS" -> ChatColor.GOLD;
            case "VAMPIRISM" -> ChatColor.DARK_PURPLE;
            case "BERSERKER" -> ChatColor.RED;
            case "ICE_ASPECT" -> ChatColor.AQUA;
            case "MAGNETIC" -> ChatColor.BLUE;
            case "AUTO_REPAIR" -> ChatColor.GREEN;
            case "LUCKY_MINER" -> ChatColor.DARK_GREEN;
            case "WEBBING" -> ChatColor.WHITE;
            case "SOULBOUND" -> ChatColor.LIGHT_PURPLE;
            case "POISON_EDGE" -> ChatColor.DARK_GREEN;
            case "NETHER_SLAYER" -> ChatColor.RED;
            case "XP_BOOST" -> ChatColor.DARK_AQUA;
            case "HEAD_HUNTER" -> ChatColor.DARK_GRAY;
            case "SPAWN_EGG" -> ChatColor.GREEN;
            case "TUNNEL" -> ChatColor.GRAY;
            case "AUTO_SMELT" -> ChatColor.GOLD;
            default -> ChatColor.GRAY;
        };
    }

    public static String toRoman(int num) {
        String[] romanNumerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                result.append(romanNumerals[i]);
                num -= values[i];
            }
        }
        return result.toString();
    }

    public abstract String getFullDescription();
}
