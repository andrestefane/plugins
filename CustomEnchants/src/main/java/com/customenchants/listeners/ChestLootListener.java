package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Listener para inyectar encantamientos personalizados en el loot de cofres.
 */
public class ChestLootListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    public ChestLootListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        double chance = plugin.getConfig().getDouble("settings.chest-spawn-chance", 0.35);
        if (random.nextDouble() > chance) return;

        List<ItemStack> loot = event.getLoot();
        if (loot == null || loot.isEmpty()) return;

        // 40% de las veces generar un libro encantado custom en lugar de item directo
        if (random.nextDouble() < 0.40) {
            spawnEnchantedBook(loot);
            return;
        }

        // Resto: aplicar encantamiento directo a un item del cofre
        List<ItemStack> equipable = new ArrayList<>();
        for (ItemStack item : loot) {
            if (item != null && isEnchantable(item)) {
                equipable.add(item);
            }
        }

        if (equipable.isEmpty()) {
            spawnEnchantedBook(loot);
            return;
        }

        ItemStack chosen = equipable.get(random.nextInt(equipable.size()));
        Optional<CustomEnchant> enchantOpt = manager.getRandomApplicable(chosen);
        if (enchantOpt.isEmpty()) return;

        CustomEnchant enchant = enchantOpt.get();
        int level = 1 + random.nextInt(enchant.getMaxLevel());
        enchant.applyToItem(chosen, level);
    }

    /** Añade un libro encantado custom al loot del cofre */
    private void spawnEnchantedBook(List<ItemStack> loot) {
        Optional<CustomEnchant> enchantOpt = manager.getAnyRandom();
        if (enchantOpt.isEmpty()) return;
        CustomEnchant enchant = enchantOpt.get();
        int level = 1 + random.nextInt(enchant.getMaxLevel());
        loot.add(enchant.createBook(level));
    }

    private boolean isEnchantable(ItemStack item) {
        if (item == null) return false;
        String type = item.getType().name();
        return type.endsWith("_SWORD") || type.endsWith("_AXE") ||
                type.endsWith("_PICKAXE") || type.endsWith("_SHOVEL") ||
                type.endsWith("_HOE") || type.endsWith("_BOW") ||
                type.endsWith("CROSSBOW") || type.endsWith("_HELMET") ||
                type.endsWith("_CHESTPLATE") || type.endsWith("_LEGGINGS") ||
                type.endsWith("_BOOTS") || type.equals("TRIDENT") ||
                type.equals("BOW");
    }
}
