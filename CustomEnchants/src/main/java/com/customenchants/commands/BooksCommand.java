package com.customenchants.commands;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.listeners.CreativeTabListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


/**
 * Comando /librosenchants - abre la GUI de libros encantados custom.
 * Disponible para cualquier jugador.
 */
public class BooksCommand implements CommandExecutor
{

    private final CreativeTabListener booksGui;

    public BooksCommand(CustomEnchantsPlugin plugin)
    {
        this.booksGui = new CreativeTabListener(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                Component.text("Solo jugadores pueden usar este comando.")
                    .color(NamedTextColor.RED)
            );
            return true;
        }

        booksGui.openBooksGUI(player);
        return true;
    }
}