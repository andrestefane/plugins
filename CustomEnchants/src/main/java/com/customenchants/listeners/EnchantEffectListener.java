package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.*;
import com.customenchants.managers.EnchantManager;
import com.customenchants.utils.ParticleUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
        startPeriodicTasks();
    }

    // -------------------------------------------------------------------------
    // Daño en combate cuerpo a cuerpo
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // GOLPE RELÁMPAGO
            if (enchant instanceof ThunderStrikeEnchant ts) {
                if (random.nextDouble() < ts.getTriggerChance(level)) {
                    target.getWorld().strikeLightningEffect(target.getLocation());
                    target.damage(ts.getDamageMultiplier() * level, attacker);
                    sendActionBar(attacker, " ¡Golpe Relámpago!", NamedTextColor.YELLOW);
                }
            }

            // ROBO DE VIDA
            if (enchant instanceof LifeStealEnchant ls) {
                if (random.nextDouble() < ls.getTriggerChance(level)) {
                    double heal = ls.getHealAmount(level);
                    double maxHp = getMaxHealth(attacker);
                    attacker.setHealth(Math.min(maxHp, attacker.getHealth() + heal));
                    if (plugin.getConfig().getBoolean("settings.show-particles", true)) {
                        ParticleUtils.spawnHeartParticles(attacker.getLocation().add(0, 1, 0));
                    }
                    sendActionBar(attacker, " +" + String.format("%.1f", heal) + " HP robado", NamedTextColor.RED);
                }
            }

            // ASPECTO GÉLIDO
            if (enchant instanceof IceAspectEnchant ia) {
                if (random.nextDouble() < ia.getTriggerChance(level)) {
                    target.setFreezeTicks(ia.getFreezeDuration(level));
                    target.getWorld().spawnParticle(
                            Particle.SNOWFLAKE,
                            target.getLocation().add(0, 1, 0),
                            15, 0.3, 0.5, 0.3, 0.05
                    );
                    sendActionBar(attacker, " ¡Congelado!", NamedTextColor.AQUA);
                }
            }

            // BERSERKER
            if (enchant instanceof BerserkerEnchant bk) {
                double missingHearts = (getMaxHealth(attacker) - attacker.getHealth()) / 2.0;
                if (missingHearts > 0) {
                    double bonus = bk.getDamageBonus(missingHearts, level);
                    event.setDamage(event.getDamage() * bonus);
                }
            }

            // TELARAÑA
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
                    sendActionBar(attacker, " ¡Atrapado en telaraña!", NamedTextColor.WHITE);
                }
            }

            // FILO VENENOSO
            if (enchant instanceof PoisonEdgeEnchant pe) {
                if (random.nextDouble() < pe.getTriggerChance(level)) {
                    target.addPotionEffect(new PotionEffect(
                            PotionEffectType.POISON,
                            pe.getPoisonDuration(level),
                            pe.getPoisonAmplifier(level)
                    ));
                    sendActionBar(attacker, " ¡Envenenado!", NamedTextColor.DARK_GREEN);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Muerte de entidad
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(weapon);

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // VAMPIRISMO
            if (enchant instanceof VampirismEnchant vamp) {
                if (random.nextDouble() < vamp.getAbsorbChance(level)) {
                    double heal = vamp.getHealthPerKill(level);
                    double maxHp = getMaxHealth(killer);
                    killer.setHealth(Math.min(maxHp, killer.getHealth() + heal));

                    if (plugin.getConfig().getBoolean("settings.show-particles", true)) {
                        ParticleUtils.spawnBloodParticles(killer.getLocation().add(0, 1, 0));
                    }
                    sendActionBar(killer,
                            " +" + String.format("%.1f", heal) + " HP absorbido",
                            NamedTextColor.DARK_RED);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Impacto de proyectil
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        if (!(event.getEntity() instanceof Arrow)) return;

        ItemStack bow = shooter.getInventory().getItemInMainHand();
        if (bow.getType() != Material.BOW && bow.getType() != Material.CROSSBOW) return;

        Map<CustomEnchant, Integer> enchants = manager.getEnchants(bow);
        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int level = entry.getValue();

            // FLECHAS EXPLOSIVAS
            if (enchant instanceof ExplosiveArrowsEnchant ea) {
                Location loc = event.getEntity().getLocation();
                World world = loc.getWorld();
                if (world == null) continue;
                // createExplosion(Location, float power, boolean fire, boolean breakBlocks, Entity source)
                world.createExplosion(loc, ea.getExplosionPower(level), false, false, shooter);
                event.getEntity().remove();
                sendActionBar(shooter, " ¡Flecha Explosiva!", NamedTextColor.GOLD);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Tarea periódica: auto-reparación y magnético
    // -------------------------------------------------------------------------

    private void startPeriodicTasks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handleAutoRepair(player);
                    handleMagnetic(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 10L);
    }

    private void handleAutoRepair(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            // Solo items con durabilidad
            if (item.getType().getMaxDurability() <= 0) continue;
            if (!(item.getItemMeta() instanceof Damageable dmg)) continue;
            if (dmg.getDamage() <= 0) continue;

            Map<CustomEnchant, Integer> enchants = manager.getEnchants(item);
            for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
                if (!(entry.getKey() instanceof AutoRepairEnchant ar)) continue;
                int repair = ar.getDurabilityPerTick(entry.getValue());
                dmg.setDamage(Math.max(0, dmg.getDamage() - repair));
                item.setItemMeta(dmg);
            }
        }
    }

    private void handleMagnetic(Player player) {
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece == null) continue;
            Map<CustomEnchant, Integer> enchants = manager.getEnchants(armorPiece);
            for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
                if (!(entry.getKey() instanceof MagneticEnchant mag)) continue;
                double radius = mag.getRadius(entry.getValue());

                for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                    if (e instanceof Item itemEntity) {
                        itemEntity.setVelocity(
                                player.getLocation().subtract(e.getLocation())
                                        .toVector().normalize().multiply(0.3)
                        );
                    } else if (e instanceof ExperienceOrb orb) {
                        orb.setVelocity(
                                player.getLocation().subtract(e.getLocation())
                                        .toVector().normalize().multiply(0.4)
                        );
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utilidades internas
    // -------------------------------------------------------------------------

    /**
     * Obtiene la salud máxima de un {@link Player} sin usar el método deprecated
     * {@code getMaxHealth()}.
     */
    private double getMaxHealth(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        return attr != null ? attr.getValue() : 20.0;
    }

    /**
     * Envía un action bar solo si la opción está habilitada en el config.
     */
    private void sendActionBar(Player player, String message, NamedTextColor color) {
        if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
            player.sendActionBar(Component.text(message, color));
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
}