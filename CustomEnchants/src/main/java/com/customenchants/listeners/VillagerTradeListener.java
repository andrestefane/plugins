package com.customenchants.listeners;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.managers.EnchantManager;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Optional;
import java.util.Random;

/**
 * Listener para que los aldeanos puedan ofrecer items con encantamientos custom.
 */
public class VillagerTradeListener implements Listener {

    private final CustomEnchantsPlugin plugin;
    private final EnchantManager manager;
    private final Random random = new Random();

    public VillagerTradeListener(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;

        Villager.Profession profession = villager.getProfession();
        if (profession != Villager.Profession.ARMORER &&
                profession != Villager.Profession.WEAPONSMITH &&
                profession != Villager.Profession.TOOLSMITH &&
                profession != Villager.Profession.LIBRARIAN) {
            return;
        }

        double chance = plugin.getConfig().getDouble("settings.villager-trade-chance", 0.25);
        if (random.nextDouble() > chance) return;

        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        // Los libreros ofrecen libros encantados custom
        if (profession == Villager.Profession.LIBRARIAN) {
            offerEnchantedBook(event);
            return;
        }

        // El resto ofrece items encantados directamente
        if (result == null || !isEnchantable(result)) return;

        Optional<CustomEnchant> enchantOpt = manager.getRandomApplicable(result);
        if (enchantOpt.isEmpty()) return;

        CustomEnchant enchant = enchantOpt.get();
        int level = 1 + random.nextInt(enchant.getMaxLevel());
        ItemStack enchanted = enchant.applyToItem(result.clone(), level);

        int uses = recipe.getMaxUses();
        boolean expReward = recipe.hasExperienceReward();
        int emeraldCost = Math.min(getEmeraldCost(result) + (level * 5), 64);

        MerchantRecipe newRecipe = new MerchantRecipe(enchanted, uses);
        newRecipe.addIngredient(new ItemStack(Material.EMERALD, emeraldCost));
        newRecipe.setExperienceReward(expReward);
        event.setRecipe(newRecipe);
    }

    /** El librero ofrece un libro encantado custom a cambio de esmeraldas */
    private void offerEnchantedBook(VillagerAcquireTradeEvent event) {
        Optional<CustomEnchant> enchantOpt = manager.getAnyRandom();
        if (enchantOpt.isEmpty()) return;

        CustomEnchant enchant = enchantOpt.get();
        int level = 1 + random.nextInt(enchant.getMaxLevel());
        ItemStack book = enchant.createBook(level);

        int emeraldCost = Math.min(10 + (level * 8), 64);
        MerchantRecipe bookRecipe = new MerchantRecipe(book, event.getRecipe().getMaxUses());
        bookRecipe.addIngredient(new ItemStack(Material.EMERALD, emeraldCost));
        bookRecipe.addIngredient(new ItemStack(Material.BOOK));
        bookRecipe.setExperienceReward(true);
        event.setRecipe(bookRecipe);
    }

    private boolean isEnchantable(ItemStack item) {
        String type = item.getType().name();
        return type.endsWith("_SWORD") || type.endsWith("_AXE") ||
                type.endsWith("_PICKAXE") || type.endsWith("_SHOVEL") ||
                type.endsWith("_HELMET") || type.endsWith("_CHESTPLATE") ||
                type.endsWith("_LEGGINGS") || type.endsWith("_BOOTS") ||
                type.equals("BOW") || type.equals("CROSSBOW") || type.equals("TRIDENT");
    }

    private int getEmeraldCost(ItemStack item) {
        String type = item.getType().name();
        if (type.startsWith("NETHERITE")) return 30;
        if (type.startsWith("DIAMOND")) return 20;
        if (type.startsWith("IRON")) return 10;
        return 8;
    }
}
