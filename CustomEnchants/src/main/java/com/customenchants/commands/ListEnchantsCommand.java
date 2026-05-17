package com.customenchants.commands;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.gui.EnchantGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando /listarce - abre la GUI de encantamientos personalizados.
 */
public class ListEnchantsCommand implements CommandExecutor {

    private final EnchantGUI gui;

    public ListEnchantsCommand(CustomEnchantsPlugin plugin) {
        this.gui = new EnchantGUI(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores pueden usar este comando.", NamedTextColor.RED));
            return true;
        }
        gui.open(player);
        return true;
    }
}
