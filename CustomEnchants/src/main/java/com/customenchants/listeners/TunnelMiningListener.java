package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.AutoSmeltEnchant;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.enchants.TunnelEnchant;
import com.customenchants.managers.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Listener para encantamientos de mineria avanzada:
 * - Tunel: mina un area centrada y profundiza en niveles altos.
 * - Auto-Fundicion: funde menas al romperlas.
 */
public class TunnelMiningListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Set<UUID> breaking = new HashSet<>();

    public TunnelMiningListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (breaking.contains(player.getUniqueId())) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        Map<CustomEnchant, Integer> enchants = manager.getEnchants(tool);

        int tunnelLevel = 0;
        boolean hasAutoSmelt = false;

        for (Map.Entry<CustomEnchant, Integer> entry : enchants.entrySet()) {
            if (entry.getKey() instanceof TunnelEnchant) {
                tunnelLevel = entry.getValue();
            }
            if (entry.getKey() instanceof AutoSmeltEnchant) {
                hasAutoSmelt = true;
            }
        }

        if (hasAutoSmelt) {
            applyAutoSmelt(event);
        }

        if (tunnelLevel > 0 && !player.isSneaking() && isMineableBlock(event.getBlock())) {
            applyTunnel(event, player, tool, tunnelLevel, hasAutoSmelt);
        }
    }

    private void applyTunnel(BlockBreakEvent event, Player player, ItemStack tool,
                             int level, boolean autoSmelt) {
        Block origin = event.getBlock();

        // Evitamos tamanos pares en niveles altos: 4x4 y 6x6 no tienen centro real.
        // Nivel 1: 2x2. Nivel 2: 3x3. Niveles 3-5: 3x3 con mas profundidad.
        int sideSize = level == 1 ? 2 : 3;
        int depth = Math.max(1, level - 1);
        int minOffset = level == 1 ? 0 : -1;
        int maxOffset = 1;

        BlockFace face = getPlayerFace(player);
        List<Block> toBreak = new ArrayList<>();

        for (int d = 0; d < depth; d++) {
            for (int u = minOffset; u <= maxOffset; u++) {
                for (int v = minOffset; v <= maxOffset; v++) {
                    if (d == 0 && u == 0 && v == 0) continue;

                    Block b = getRelativeBlock(origin, face, u, v, d);
                    if (b != null && isMineableBlock(b) && !b.equals(origin)) {
                        toBreak.add(b);
                    }
                }
            }
        }

        breaking.add(player.getUniqueId());
        try {
            for (Block b : toBreak) {
                BlockBreakEvent extra = new BlockBreakEvent(b, player);
                plugin.getServer().getPluginManager().callEvent(extra);
                if (extra.isCancelled()) continue;

                if (autoSmelt) applyAutoSmeltBlock(b, player);
                else breakWithDrops(b, player, tool);
            }
        } finally {
            breaking.remove(player.getUniqueId());
        }

        if (plugin.getConfig().getBoolean("settings.show-action-bar", true)) {
            String depthText = depth > 1 ? " x" + depth : "";
            player.sendActionBar(Component.text("Tunel " + sideSize + "x" + sideSize + depthText, NamedTextColor.GOLD));
        }
    }

    private BlockFace getPlayerFace(Player player) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        if (pitch < -45) return BlockFace.UP;
        if (pitch > 45) return BlockFace.DOWN;

        yaw = ((yaw % 360) + 360) % 360;
        if (yaw < 45 || yaw >= 315) return BlockFace.SOUTH;
        if (yaw < 135) return BlockFace.WEST;
        if (yaw < 225) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    private Block getRelativeBlock(Block origin, BlockFace face, int u, int v, int depth) {
        return switch (face) {
            case NORTH -> origin.getRelative(u, v, -depth);
            case SOUTH -> origin.getRelative(u, v, depth);
            case EAST -> origin.getRelative(depth, v, u);
            case WEST -> origin.getRelative(-depth, v, u);
            case UP -> origin.getRelative(u, depth, v);
            case DOWN -> origin.getRelative(u, -depth, v);
            default -> null;
        };
    }

    private void breakWithDrops(Block block, Player player, ItemStack tool) {
        Collection<ItemStack> drops = block.getDrops(tool, player);
        block.setType(Material.AIR);
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }

        if (tool.getType().getMaxDurability() > 0) {
            org.bukkit.inventory.meta.Damageable dmg = (org.bukkit.inventory.meta.Damageable) tool.getItemMeta();
            if (dmg != null) {
                dmg.setDamage(dmg.getDamage() + 1);
                tool.setItemMeta(dmg);
            }
        }
    }

    private void applyAutoSmelt(BlockBreakEvent event) {
        Block block = event.getBlock();
        AutoSmeltEnchant ae = getAutoSmeltEnchant(manager);
        if (ae == null) return;

        Material smelt = ae.getSmeltResult(block.getType());
        if (smelt == null) return;

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(smelt));
    }

    private void applyAutoSmeltBlock(Block block, Player player) {
        AutoSmeltEnchant ae = getAutoSmeltEnchant(manager);
        if (ae == null) {
            block.breakNaturally();
            return;
        }

        Material smelt = ae.getSmeltResult(block.getType());
        if (smelt != null) {
            block.setType(Material.AIR);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(smelt));
        } else {
            block.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    private AutoSmeltEnchant getAutoSmeltEnchant(EnchantManager mgr) {
        CustomEnchant e = mgr.getById("AUTO_SMELT");
        return e instanceof AutoSmeltEnchant ae ? ae : null;
    }

    private boolean isMineableBlock(Block block) {
        Material m = block.getType();
        if (m == Material.AIR || m == Material.WATER || m == Material.LAVA) return false;
        if (m == Material.BEDROCK) return false;
        if (block.getState() instanceof org.bukkit.block.Container) return false;
        return block.getType().getHardness() >= 0;
    }
}
