package com.customenchants.commands;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.listeners.CreativeTabListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando /librosenchants - abre la GUI de libros encantados custom.
 * Disponible para cualquier jugador.
 */
public class BooksCommand implements CommandExecutor {

    private final CustomEnchantsPlugin plugin;
    private final CreativeTabListener booksGui;

    public BooksCommand(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.booksGui = new CreativeTabListener(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden usar este comando.");
            return true;
        }
        booksGui.openBooksGUI(player);
        return true;
    }
}
