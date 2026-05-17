package com.customenchants.commands;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.gui.EnchantGUI;
import com.customenchants.managers.EnchantManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    private final EnchantManager manager;
    private final EnchantGUI gui;

    public EnchantCommand(CustomEnchantsPlugin plugin) {
        this.manager = plugin.getEnchantManager();
        this.gui = new EnchantGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("customenchants.admin")) {
            sender.sendMessage(Component.text("No tienes permiso para usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores pueden usar este comando.", NamedTextColor.RED));
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
                player.sendMessage(Component.text("Encantamiento no encontrado: " + id, NamedTextColor.RED));
                player.sendMessage(Component.text("Usa /encantamiento lista para ver los IDs disponibles.", NamedTextColor.YELLOW));
                return true;
            }

            int level = 1;
            if (args.length >= 3) {
                try {
                    level = Integer.parseInt(args[2]);
                    level = Math.max(1, Math.min(level, enchant.getMaxLevel()));
                } catch (NumberFormatException e) {
                    player.sendMessage(Component.text("Nivel inválido. Usando nivel 1.", NamedTextColor.RED));
                }
            }

            // /encantamiento dar <ID> [nivel] libro  → crear libro encantado
            boolean asBook = args.length >= 4 && args[3].equalsIgnoreCase("libro");

            if (asBook) {
                ItemStack book = enchant.createBook(level);
                player.getInventory().addItem(book);
                player.sendMessage(Component.text("Te has dado un libro de " + enchant.getColoredDisplayName() + " " + CustomEnchant.toRoman(level) + ".", NamedTextColor.GREEN));
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                player.sendMessage(Component.text("Sostén un item en la mano (o usa 'libro' al final para crear un libro).", NamedTextColor.RED));
                return true;
            }

            if (!enchant.canApplyToItem(item)) {
                player.sendMessage(Component.text("Este encantamiento no se puede aplicar a este item.", NamedTextColor.RED));
                player.sendMessage(Component.text("Usa: /encantamiento dar " + id + " " + level + " libro  para crear un libro.", NamedTextColor.YELLOW));
                return true;
            }

            enchant.applyToItem(item, level);
            player.sendMessage(convertLegacy("&6[CustomEnchants] &7Aplicado " + enchant.getColoredDisplayName() +
                    " " + CustomEnchant.toRoman(level) + " &7a tu item."));
            return true;
        }

        player.sendMessage(Component.text("Uso: /encantamiento <lista | dar <ID> [nivel] [libro]>", NamedTextColor.YELLOW));
        return true;
    }

    private Component convertLegacy(String legacyText) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
    }
}
