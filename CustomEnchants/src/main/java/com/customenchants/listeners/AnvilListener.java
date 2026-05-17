package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Listener del yunque para encantamientos custom.
 *
 * Casos soportados:
 *   A) Item normal  + Libro custom  → item con encantamiento transferido
 *   B) Libro custom + Libro custom  → libro con nivel combinado (I+I=II, etc.)
 */
public class AnvilListener implements Listener {

    private final EnchantManager manager;

    public AnvilListener(CustomEnchantsPlugin plugin) {
        this.manager = plugin.getEnchantManager();
    }
    private AnvilView getAnvilView(PrepareAnvilEvent event) {
        return (AnvilView) event.getView();
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack left  = anvil.getFirstItem();
        ItemStack right = anvil.getSecondItem();

        if (left == null || right == null) return;
        if (right.getType() != Material.ENCHANTED_BOOK) return;

        Map<CustomEnchant, Integer> rightEnchants = manager.getEnchantsFromBook(right);
        if (rightEnchants.isEmpty()) return;

        if (left.getType() == Material.ENCHANTED_BOOK) {
            combineBooks(event, left, rightEnchants);
        } else {
            applyBookToItem(event, left, rightEnchants);
        }
    }

    // ── CASO A: item + libro ──────────────────────────────────────────────────

    private void applyBookToItem(PrepareAnvilEvent event,
                                  ItemStack left,
                                  Map<CustomEnchant, Integer> bookEnchants) {
        ItemStack result = left.clone();
        int extraCost = 0;
        int applied = 0;

        for (Map.Entry<CustomEnchant, Integer> entry : bookEnchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int bookLevel = entry.getValue();

            if (!enchant.canApplyToItem(result)) continue;
            if (!isCompatible(enchant, result)) continue;

            int currentLevel = enchant.getLevel(result);
            int finalLevel;

            if (currentLevel == bookLevel && currentLevel < enchant.getMaxLevel()) {
                finalLevel = currentLevel + 1;
            } else if (bookLevel > currentLevel) {
                finalLevel = Math.min(bookLevel, enchant.getMaxLevel());
            } else {
                continue;
            }

            enchant.applyToItem(result, finalLevel);
            extraCost += finalLevel * 2;
            applied++;
        }

        if (applied == 0) return;
        event.setResult(result);
        AnvilView view = getAnvilView(event);
        view.setRepairCost(Math.max(1, view.getRepairCost()) + extraCost);
    }

    // ── CASO B: libro + libro ─────────────────────────────────────────────────

    private void combineBooks(PrepareAnvilEvent event,
                               ItemStack leftBook,
                               Map<CustomEnchant, Integer> rightEnchants) {

        Map<CustomEnchant, Integer> leftEnchants  = manager.getEnchantsFromBook(leftBook);
        Map<CustomEnchant, Integer> finalEnchants = new LinkedHashMap<>(leftEnchants);
        int extraCost = 0;
        boolean changed = false;

        for (Map.Entry<CustomEnchant, Integer> entry : rightEnchants.entrySet()) {
            CustomEnchant enchant = entry.getKey();
            int rightLevel = entry.getValue();
            int leftLevel  = leftEnchants.getOrDefault(enchant, 0);

            int finalLevel;
            if (leftLevel == rightLevel && leftLevel < enchant.getMaxLevel()) {
                finalLevel = leftLevel + 1;
            } else {
                finalLevel = Math.max(leftLevel, rightLevel);
            }

            if (finalLevel > leftLevel) {
                finalEnchants.put(enchant, finalLevel);
                extraCost += finalLevel;
                changed = true;
            }
        }

        if (!changed) return;

        // Construir libro resultado desde cero con todos los enchants finales
        ItemStack resultBook = new ItemStack(Material.ENCHANTED_BOOK);
        for (Map.Entry<CustomEnchant, Integer> entry : finalEnchants.entrySet()) {
            resultBook = entry.getKey().applyToBook(resultBook, entry.getValue());
        }

        event.setResult(resultBook);
        AnvilView view = getAnvilView(event);
        view.setRepairCost(Math.max(1, view.getRepairCost()) + extraCost);
    }

    // ── COMPATIBILIDAD ────────────────────────────────────────────────────────

    private boolean isCompatible(CustomEnchant enchant, ItemStack item) {
        if (enchant instanceof com.customenchants.enchants.AutoRepairEnchant ar) {
            return !ar.hasMending(item);
        }
        if (enchant instanceof com.customenchants.enchants.AutoSmeltEnchant) {
            return !item.containsEnchantment(org.bukkit.enchantments.Enchantment.SILK_TOUCH);
        }
        return true;
    }
}
