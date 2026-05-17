package com.customenchants.commands;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.gui.EnchantGUI;
import com.customenchants.managers.EnchantManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Comando /encantamiento - gestión de encantamientos para admins.
 * /encantamiento lista  → Abre la GUI
 * /encantamiento dar <ID> [nivel] → Da el encantamiento al item en mano
 */
public class EnchantCommand implements CommandExecutor {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final EnchantGUI gui;

    public EnchantCommand(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
        this.gui = new EnchantGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("customenchants.admin")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden usar este comando.");
            return true;
        }

        // Sin argumentos o "lista" → abrir GUI
        if (args.length == 0 || args[0].equalsIgnoreCase("lista")) {
            gui.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("dar") && args.length >= 2) {
            String id = args[1].toUpperCase();
            CustomEnchant enchant = manager.getById(id);
            if (enchant == null) {
                player.sendMessage(ChatColor.RED + "Encantamiento no encontrado: " + id);
                player.sendMessage(ChatColor.YELLOW + "Usa /encantamiento lista para ver los IDs disponibles.");
                return true;
            }

            int level = 1;
            if (args.length >= 3) {
                try {
                    level = Integer.parseInt(args[2]);
                    level = Math.max(1, Math.min(level, enchant.getMaxLevel()));
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Nivel inválido. Usando nivel 1.");
                }
            }

            // /encantamiento dar <ID> [nivel] libro  → crear libro encantado
            boolean asBook = args.length >= 4 && args[3].equalsIgnoreCase("libro");

            if (asBook) {
                ItemStack book = enchant.createBook(level);
                player.getInventory().addItem(book);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6[CustomEnchants] &7Te has dado un libro de " +
                                enchant.getColoredDisplayName() + " " + CustomEnchant.toRoman(level) + "&7."));
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "Sostén un item en la mano (o usa 'libro' al final para crear un libro).");
                return true;
            }

            if (!enchant.canApplyToItem(item)) {
                player.sendMessage(ChatColor.RED + "Este encantamiento no se puede aplicar a este item.");
                player.sendMessage(ChatColor.YELLOW + "Usa: /encantamiento dar " + id + " " + level + " libro  para crear un libro.");
                return true;
            }

            enchant.applyToItem(item, level);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6[CustomEnchants] &7Aplicado " + enchant.getColoredDisplayName() +
                            " " + CustomEnchant.toRoman(level) + " &7a tu item."));
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Uso: /encantamiento <lista | dar <ID> [nivel] [libro]>");
        return true;
    }
}
