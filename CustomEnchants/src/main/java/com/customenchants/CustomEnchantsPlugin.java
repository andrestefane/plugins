package com.customenchants;

import com.customenchants.enchants.*;
import com.customenchants.listeners.*;
import com.customenchants.managers.EnchantManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomEnchantsPlugin extends JavaPlugin {

    private static CustomEnchantsPlugin instance;
    private EnchantManager enchantManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Inicializar manager
        enchantManager = new EnchantManager(this);

        // Registrar encantamientos
        registerEnchantments();

        // Registrar listeners
        registerListeners();

        // Registrar comandos
        registerCommands();

        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║   CustomEnchants v1.0.0        ║");
        getLogger().info("║   18 encantamientos cargados   ║");
        getLogger().info("║   ¡Plugin activo correctamente!║");
        getLogger().info("╚════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        getLogger().info("[CustomEnchants] Plugin desactivado.");
    }

    private void registerEnchantments() {
        // Armas cuerpo a cuerpo
        enchantManager.register(new ThunderStrikeEnchant(this));
        enchantManager.register(new LifeStealEnchant(this));
        enchantManager.register(new VampirismEnchant(this));
        enchantManager.register(new BerserkerEnchant(this));
        enchantManager.register(new IceAspectEnchant(this));
        enchantManager.register(new WebbingEnchant(this));
        enchantManager.register(new PoisonEdgeEnchant(this));
        // Nuevos para armas cuerpo a cuerpo
        enchantManager.register(new NetherSlayerEnchant(this));
        enchantManager.register(new XpBoostEnchant(this));
        enchantManager.register(new HeadHunterEnchant(this));
        enchantManager.register(new SpawnEggEnchant(this));
        // Arcos / ballestas
        enchantManager.register(new ExplosiveArrowsEnchant(this));
        // Armadura
        enchantManager.register(new MagneticEnchant(this));
        // Picos
        enchantManager.register(new LuckyMinerEnchant(this));
        enchantManager.register(new TunnelEnchant(this));
        enchantManager.register(new AutoSmeltEnchant(this));
        // Universal
        enchantManager.register(new AutoRepairEnchant(this));
        enchantManager.register(new SoulboundEnchant(this));
        getLogger().info("[CustomEnchants] " + enchantManager.getAll().size() + " encantamientos registrados.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EnchantEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new ChestLootListener(this), this);
        getServer().getPluginManager().registerEvents(new VillagerTradeListener(this), this);
        getServer().getPluginManager().registerEvents(new EnchantTableListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
        getServer().getPluginManager().registerEvents(new TunnelMiningListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        CreativeTabListener creativeTabListener = new CreativeTabListener(this);
        getServer().getPluginManager().registerEvents(creativeTabListener, this);
        getServer().getPluginManager().registerEvents(new com.customenchants.gui.GUIListener(this), this);
    }

    private void registerCommands() {
        registerPaperCommand("encantamiento", "Gestiona los encantamientos personalizados",
                List.of("ce", "customenchant"), new com.customenchants.commands.EnchantCommand(this));
        registerPaperCommand("listarce", "Lista todos los encantamientos personalizados disponibles",
                List.of(), new com.customenchants.commands.ListEnchantsCommand(this));
        registerPaperCommand("librosenchants", "Muestra todos los libros encantados personalizados disponibles",
                List.of("libros", "cebooks"), new com.customenchants.commands.BooksCommand(this));
    }

    private void registerPaperCommand(String name, String description, List<String> aliases, CommandExecutor executor) {
        registerCommand(name, description, aliases, new BasicCommand() {
            @Override
            public void execute(CommandSourceStack source, String[] args) {
                executor.onCommand(source.getSender(), null, name, args);
            }
        });
    }

    public static CustomEnchantsPlugin getInstance() {
        return instance;
    }

    public EnchantManager getEnchantManager() {
        return enchantManager;
    }
}
