package com.customenchants.enchants;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper para encantamientos personalizados.
 * No extiende Enchantment directamente para evitar incompatibilidades con Paper 1.21.8.
 */
public class CustomEnchantWrapper {
    
    private final CustomEnchant customEnchant;
    private final NamespacedKey key;
    
    public CustomEnchantWrapper(CustomEnchant customEnchant) {
        this.customEnchant = customEnchant;
        this.key = new NamespacedKey("customenchants", customEnchant.getId().toLowerCase());
    }
    
    public @NotNull NamespacedKey getKey() {
        return key;
    }
    
    public @NotNull Component displayName(int level) {
        return LegacyComponentSerializer.legacySection().deserialize(
                customEnchant.getColoredDisplayName() + " §7" + CustomEnchant.toRoman(level));
    }
    
    public int getMaxLevel() {
        return customEnchant.getMaxLevel();
    }
    
    public int getStartLevel() {
        return 1;
    }
    
    public boolean isTreasure() {
        return false;
    }
    
    public boolean isCursed() {
        return false;
    }
    
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return customEnchant.canApplyTo(item);
    }
    
    public CustomEnchant getCustomEnchant() {
        return customEnchant;
    }
}
