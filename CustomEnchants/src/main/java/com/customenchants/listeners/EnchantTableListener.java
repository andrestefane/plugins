package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;

/**
 * Listener para anadir encantamientos custom en la mesa de encantamientos.
 */
public class EnchantTableListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    // Probabilidad de que al encantar en la mesa tambien se anada un custom (20%)
    private static final double EXTRA_ENCHANT_CHANCE = 0.20;

    public EnchantTableListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        int level = event.getExpLevelCost();

        int minLevel = plugin.getConfig().getInt("settings.enchant-table-min-level", 15);
        if (level < minLevel) return;

        double chance = Math.min(0.6, EXTRA_ENCHANT_CHANCE + (level / 100.0));
        if (random.nextDouble() > chance) return;

        ItemStack item = event.getItem();
        Optional<CustomEnchant> enchantOpt = manager.getRandomApplicable(item);
        if (enchantOpt.isEmpty()) return;

        CustomEnchant enchant = enchantOpt.get();

        int enchantLevel;
        if (level >= 30) enchantLevel = enchant.getMaxLevel();
        else if (level >= 20) enchantLevel = Math.max(1, enchant.getMaxLevel() - 1);
        else enchantLevel = 1;

        EnchantingInventory enchantingInventory = event.getInventory() instanceof EnchantingInventory inventory
                ? inventory
                : null;

        final int finalLevel = enchantLevel;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ItemStack current = enchantingInventory != null ? enchantingInventory.getItem() : event.getItem();
            if (current == null || current.getType().isAir()) return;
            if (enchant.getLevel(current) > 0) return;

            enchant.applyToItem(current, finalLevel);
            if (enchantingInventory != null) {
                enchantingInventory.setItem(current);
            } else {
                event.setItem(current);
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6[CustomEnchants] &7Tu item tambien recibio " +
                            enchant.getColoredDisplayName() + " " +
                            CustomEnchant.toRoman(finalLevel) + "&7!"));
        }, 1L);
    }
}
