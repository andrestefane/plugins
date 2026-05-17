package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.enchants.LuckyMinerEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Listener para el encantamiento LuckyMiner al minar bloques.
 */
public class MiningListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    public MiningListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        Map<CustomEnchant, Integer> enchants = manager.getEnchants(tool);
        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            if (entry.getKey() instanceof LuckyMinerEnchant lm) {
                int level = entry.getValue();
                if (random.nextDouble() < lm.getDoubleDropChance(level)) {
                    // Duplicar drops
                    Collection<ItemStack> drops = event.getBlock().getDrops(tool);
                    if (!drops.isEmpty()) {
                        List<ItemStack> extraDrops = new ArrayList<>(drops);
                        event.setDropItems(true);
                        for (ItemStack drop : extraDrops) {
                            event.getBlock().getWorld().dropItemNaturally(
                                    event.getBlock().getLocation(), drop);
                        }
                        if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                            player.sendActionBar(ChatColor.GREEN + "🍀 ¡Doble drop!");
                        }
                    }
                }
            }
        }
    }
}
