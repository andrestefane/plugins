package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.enchants.SoulboundEnchant;
import com.customenchants.managers.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gestiona el encantamiento Soulbound al morir el jugador.
 */
public class PlayerDeathListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;

    public PlayerDeathListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> toKeep = new ArrayList<>();

        // Revisar inventario
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            Map<CustomEnchant, Integer> enchants = manager.getEnchants(item);
            for (CustomEnchant enchant : enchants.keySet()) {
                if (enchant instanceof SoulboundEnchant) {
                    toKeep.add(item);
                    break;
                }
            }
        }

        if (toKeep.isEmpty()) return;

        // Remover los items soulbound de los drops
        event.getDrops().removeAll(toKeep);

        // Devolver al jugador cuando respawnee
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                for (ItemStack item : toKeep) {
                    player.getInventory().addItem(item);
                }
                player.sendMessage(Component.text("&6[CustomEnchants] Tu" + (toKeep.size() > 1 ? "s " + toKeep.size() + " items vinculados han" : " item vinculado ha") +
                                " vuelto a ti gracias al encantamiento &dVinculado al Alma&7.", NamedTextColor.GOLD));
            }
        }, 20L);
    }
}
