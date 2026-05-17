package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.*;
import com.customenchants.managers.EnchantManager;
import com.customenchants.utils.ParticleUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;

/**
 * Listener principal para efectos de encantamientos en combate.
 */
public class EnchantEffectListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    public EnchantEffectListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();

        // Iniciar tarea de auto-reparación y magnético
        startPeriodicTasks();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // ⚡ GOLPE RELÁMPAGO
            if (enchant instanceof ThunderStrikeEnchant ts) {
                if (random.nextDouble() < ts.getTriggerChance(level)) {
                    target.getWorld().strikeLightningEffect(target.getLocation());
                    target.damage(ts.getDamageMultiplier() * level, attacker);
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        attacker.sendActionBar(ChatColor.YELLOW + "⚡ ¡Golpe Relámpago!");
                    }
                }
            }

            // 🩸 ROBO DE VIDA
            if (enchant instanceof LifeStealEnchant ls) {
                if (random.nextDouble() < ls.getTriggerChance(level)) {
                    double heal = ls.getHealAmount(level);
                    double newHealth = Math.min(attacker.getMaxHealth(), attacker.getHealth() + heal);
                    attacker.setHealth(newHealth);
                    if (plugin.getConfig().getBoolean("settings.show-particles", true)) {
                        ParticleUtils.spawnHeartParticles(attacker.getLocation().add(0, 1, 0));
                    }
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        attacker.sendActionBar(ChatColor.RED + "🩸 +" + String.format("%.1f", heal) + " HP robado");
                    }
                }
            }

            // ❄️ ASPECTO GÉLIDO
            if (enchant instanceof IceAspectEnchant ia) {
                if (random.nextDouble() < ia.getTriggerChance(level)) {
                    target.setFreezeTicks(ia.getFreezeDuration(level));
                    target.getWorld().spawnParticle(Particle.SNOWFLAKE,
                            target.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        attacker.sendActionBar(ChatColor.AQUA + "❄ ¡Congelado!");
                    }
                }
            }

            // 🔥 BERSERKER
            if (enchant instanceof BerserkerEnchant bk) {
                double missingHearts = (attacker.getMaxHealth() - attacker.getHealth()) / 2.0;
                if (missingHearts > 0) {
                    double bonus = bk.getDamageBonus(missingHearts, level);
                    event.setDamage(event.getDamage() * bonus);
                }
            }

            // 🕸️ TELARAÑA
            if (enchant instanceof WebbingEnchant wb) {
                if (random.nextDouble() < wb.getTriggerChance(level)) {
                    org.bukkit.block.Block webBlock = target.getLocation().getBlock();
                    if (!canPlaceTemporaryWeb(webBlock)) continue;

                    webBlock.setType(Material.COBWEB);
                    int duration = wb.getWebDuration(level);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (webBlock.getType() == Material.COBWEB) {
                                webBlock.setType(Material.AIR);
                            }
                        }
                    }.runTaskLater(plugin, duration);
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        attacker.sendActionBar(ChatColor.WHITE + "🕸 ¡Atrapado en telaraña!");
                    }
                }
            }

            // ☠️ FILO VENENOSO
            if (enchant instanceof PoisonEdgeEnchant pe) {
                if (random.nextDouble() < pe.getTriggerChance(level)) {
                    target.addPotionEffect(new PotionEffect(
                            PotionEffectType.POISON,
                            pe.getPoisonDuration(level),
                            pe.getPoisonAmplifier(level)
                    ));
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        attacker.sendActionBar(ChatColor.DARK_GREEN + "☠ ¡Envenenado!");
                    }
                }
            }
        }
    }

    private boolean canPlaceTemporaryWeb(org.bukkit.block.Block block) {
        Material type = block.getType();
        return type == Material.AIR
                || type == Material.CAVE_AIR
                || type == Material.VOID_AIR
                || type == Material.WATER
                || type == Material.LAVA;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // 🧛 VAMPIRISMO
            if (enchant instanceof VampirismEnchant vamp) {
                if (random.nextDouble() < vamp.getAbsorbChance(level)) {
                    double heal = vamp.getHealthPerKill(level);
                    double newHealth = Math.min(killer.getMaxHealth(), killer.getHealth() + heal);
                    killer.setHealth(newHealth);
                    if (plugin.getConfig().getBoolean("settings.show-particles", true)) {
                        ParticleUtils.spawnBloodParticles(killer.getLocation().add(0, 1, 0));
                    }
                    if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                        killer.sendActionBar(ChatColor.DARK_RED + "🧛 +" + String.format("%.1f", heal) + " HP absorbido");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        if (!(event.getEntity() instanceof Arrow)) return;

        ItemStack bow = shooter.getInventory().getItemInMainHand();
        if (bow.getType() != Material.BOW && bow.getType() != Material.CROSSBOW) return;

        Map<CustomEnchant, Integer> enchants = manager.getEnchants(bow);
        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // 💥 FLECHAS EXPLOSIVAS
            if (enchant instanceof ExplosiveArrowsEnchant ea) {
                Location loc = event.getEntity().getLocation();
                loc.getWorld().createExplosion(loc, ea.getExplosionPower(level), false, false, shooter);
                event.getEntity().remove();
                if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
                    shooter.sendActionBar(ChatColor.GOLD + "💥 ¡Flecha Explosiva!");
                }
            }
        }
    }

    // Tarea de auto-reparación y magnético
    private void startPeriodicTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // AUTO-REPARACIÓN - revisar todos los items del inventario
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) continue;
                        Map<CustomEnchant, Integer> enchants = manager.getEnchants(item);
                        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
                            if (entry.getKey() instanceof AutoRepairEnchant ar) {
                                if (item.getType().getMaxDurability() > 0) {
                                    org.bukkit.inventory.meta.Damageable dmg =
                                            (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
                                    if (dmg != null && dmg.getDamage() > 0) {
                                        int repair = ar.getDurabilityPerTick(entry.getValue());
                                        dmg.setDamage(Math.max(0, dmg.getDamage() - repair));
                                        item.setItemMeta(dmg);
                                    }
                                }
                            }
                        }
                    }

                    // MAGNÉTICO - buscar items y xp cercanos
                    for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
                        if (armorPiece == null) continue;
                        Map<CustomEnchant, Integer> enchants = manager.getEnchants(armorPiece);
                        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
                            if (entry.getKey() instanceof MagneticEnchant mag) {
                                double radius = mag.getRadius(entry.getValue());
                                // Atraer items
                                for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                                    if (e instanceof Item itemEntity) {
                                        itemEntity.setVelocity(
                                                player.getLocation().subtract(e.getLocation())
                                                        .toVector().normalize().multiply(0.3)
                                        );
                                    }
                                    // Atraer orbes de XP
                                    if (e instanceof ExperienceOrb orb) {
                                        orb.setVelocity(
                                                player.getLocation().subtract(e.getLocation())
                                                        .toVector().normalize().multiply(0.4)
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L); // cada 10 ticks
    }
}
