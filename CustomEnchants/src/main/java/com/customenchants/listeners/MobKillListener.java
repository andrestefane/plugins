package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.*;
import com.customenchants.managers.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.Random;

/**
 * Listener para efectos de encantamientos al matar mobs:
 * - NetherSlayer (daño extra a mobs del Nether)
 * - XpBoost (más experiencia)
 * - HeadHunter (drop de cabeza)
 * - SpawnEgg (drop de spawn egg)
 */
public class MobKillListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    public MobKillListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // ✨ COSECHADOR DE ALMAS — más XP
            if (enchant instanceof XpBoostEnchant xp) {
                double multiplier = xp.getXpMultiplier(level);
                int bonus = (int) Math.floor(event.getDroppedExp() * (multiplier - 1.0));
                if (bonus > 0) {
                    event.setDroppedExp(event.getDroppedExp() + bonus);
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        killer.sendActionBar(Component.text(" +" + bonus + " XP extra", NamedTextColor.AQUA));
                    }
                }
            }

            // CAZADOR DE CABEZAS — drop de cabeza
            // ESTO NO FUNCIONA CORRECTAMENTE!!!!!!!!!
            if (enchant instanceof HeadHunterEnchant hh) {
                if (random.nextDouble() < hh.getDropChance(level)) {
                    Material headMat = hh.getHeadMaterial(entity.getType());
                    ItemStack head = new ItemStack(headMat);
                
                    if (headMat == Material.PLAYER_HEAD) {
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        if (meta != null) {
                            meta.displayName(
                                Component.text("Cabeza de " + formatMobName(entity.getType().name()),
                                NamedTextColor.YELLOW)
                            );
                            head.setItemMeta(meta);
                        }
                    }
                
                    event.getDrops().add(head);
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true))
                        {
                        killer.sendActionBar(Component.text("¡Cabeza obtenida!", NamedTextColor.DARK_GRAY));
                    }
                }
            }
            //  CAPTURADOR — drop de spawn egg
            if (enchant instanceof SpawnEggEnchant se) {
                if (random.nextDouble() < se.getDropChance(level)) {
                    Material egg = se.getSpawnEgg(entity.getType());
                    if (egg != null) {
                        event.getDrops().add(new ItemStack(egg));
                        if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                            killer.sendActionBar(Component.text(" ¡Spawn Egg obtenida!", NamedTextColor.GREEN));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForNether(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        org.bukkit.entity.Entity target = event.getEntity();
        if (!NetherSlayerEnchant.isNetherMob(target.getType())) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            if (entry.getKey() instanceof NetherSlayerEnchant ns) {
                int level = entry.getValue();
                double multiplier = ns.getDamageMultiplier(level);
                event.setDamage(event.getDamage() * multiplier);
                if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                    attacker.sendActionBar(Component.text(" ¡Cazador del Nether x" +
                            String.format("%.1f", multiplier) + "!", NamedTextColor.GOLD));
                }
                break;
            }
        }
    }

    private String formatMobName(String enumName) {
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
