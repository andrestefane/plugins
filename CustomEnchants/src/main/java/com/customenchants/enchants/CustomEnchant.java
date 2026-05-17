package com.customenchants.enchants;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.vanilla.VanillaEnchantSpec;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
//import io.papermc.paper.datacomponent.DataComponentType;
//import io.papermc.paper.registry.TypedKey;
//import io.papermc.paper.registry.tag.Tag;
//import io.papermc.paper.registry.tag.TagKey;

// Import of net.kyori.adventure for better text handling in the future, if needed

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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

    public String getPlainDisplayName()
    {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(displayName);
        return PlainTextComponentSerializer.plainText().serialize(component);
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

    public ItemStack applyToBook(ItemStack book, int level)
    {
        if (level < 1 || book == null) return book;

        ItemStack result = book.clone();

        if (result.getType() != Material.ENCHANTED_BOOK)
        {
            result = new ItemStack(Material.ENCHANTED_BOOK);
        }
        ItemMeta meta = result.getItemMeta();
        Enchantment vanilla = getVanillaEnchantment();

        if (vanilla == null) {
            // Optionally log or handle missing enchantment
            return result;
        }

        if (meta instanceof EnchantmentStorageMeta storageMeta)
        {
            storageMeta.addStoredEnchant(vanilla, level, true);
            removeOldLore(storageMeta);
            result.setItemMeta(storageMeta);
        }
        return (result);
    }

    public ItemStack applyToRegularItem(ItemStack item, int level) {
        if (level < 1) return item;

        Enchantment vanilla = getVanillaEnchantment();
        if (vanilla == null) {
            // Optionally log or handle missing enchantment
            return item;
        }
        item.addUnsafeEnchantment(vanilla, level);

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

    public Enchantment getVanillaEnchantment()
    {
        NamespacedKey key = new NamespacedKey(VanillaEnchantSpec.NAMESPACE, id.toLowerCase());
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
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

    private void removeOldLore(ItemMeta meta)
    {
        if (!meta.hasLore()) return;

        List<Component> lore = meta.lore();
        if (lore == null) return;

        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        String searchTag = getPlainDisplayName();

        List<Component> cleaned = lore.stream()
            .filter(component -> {
                String text = serializer.serialize(component);
                return !text.contains(searchTag);
        })
        .toList();
        meta.lore(cleaned);
    }

    private TextColor getThemeColor()
    {
        return switch (id)
        {
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
