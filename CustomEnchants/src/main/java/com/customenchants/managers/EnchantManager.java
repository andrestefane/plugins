package com.customenchants.managers;

import com.customenchants.CustomEnchantsPlugin;
import com.customenchants.enchants.CustomEnchant;
import com.customenchants.enchants.CustomEnchantWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Gestiona todos los encantamientos personalizados registrados.
 */
public class EnchantManager {

    private final CustomEnchantsPlugin plugin;
    private final Map<String, CustomEnchant> enchants = new LinkedHashMap<>();
    private final Map<String, CustomEnchantWrapper> wrappers = new LinkedHashMap<>();

    public EnchantManager(CustomEnchantsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra un encantamiento personalizado en el sistema.
     * @param enchant El encantamiento a registrar
     */
    public void register(CustomEnchant enchant) {
        String id = enchant.getId().toLowerCase();
        enchants.put(id, enchant);

        CustomEnchantWrapper wrapper = new CustomEnchantWrapper(enchant);
        wrappers.put(id, wrapper);

        plugin.getLogger().info("Registrado encantamiento: " + enchant.getPlainDisplayName());
    }

    /**
     * Obtiene el wrapper interno para un encantamiento.
     */
    public CustomEnchantWrapper getWrapper(String id) {
        return wrappers.get(id.toLowerCase());
    }

    public CustomEnchant getById(String id) {
        return enchants.get(id.toLowerCase());
    }

    public Collection<CustomEnchant> getAll() {
        return enchants.values();
    }

    public List<CustomEnchant> getEnabled() {
        List<CustomEnchant> list = new ArrayList<>();
        for (CustomEnchant e : enchants.values()) {
            if (e.isEnabled()) list.add(e);
        }
        return list;
    }

    /**
     * Obtiene todos los encantamientos personalizados presentes en un item.
     * @return Map<CustomEnchant, nivel>
     */
    public Map<CustomEnchant, Integer> getEnchants(ItemStack item) {
        Map<CustomEnchant, Integer> result = new LinkedHashMap<>();
        if (item == null) return result;
        for (CustomEnchant enchant : enchants.values()) {
            int level = enchant.getLevel(item);
            if (level > 0) result.put(enchant, level);
        }
        return result;
    }

    /**
     * Devuelve un encantamiento random que se puede aplicar al item dado.
     * Los libros siempre se consideran aplicables.
     */
    public Optional<CustomEnchant> getRandomApplicable(ItemStack item) {
        List<CustomEnchant> applicable = new ArrayList<>();
        for (CustomEnchant e : getEnabled()) {
            if (e.canApplyTo(item)) applicable.add(e);
        }
        if (applicable.isEmpty()) return Optional.empty();
        return Optional.of(applicable.get(new Random().nextInt(applicable.size())));
    }

    /**
     * Obtiene un encantamiento random de todos los disponibles.
     */
    public Optional<CustomEnchant> getAnyRandom() {
        List<CustomEnchant> enabled = getEnabled();
        if (enabled.isEmpty()) return Optional.empty();
        return Optional.of(enabled.get(new Random().nextInt(enabled.size())));
    }

    /**
     * Obtiene todos los encantamientos personalizados presentes en un libro.
     * Alias para getEnchants() - funciona igual para libros encantados.
     * @return Map<CustomEnchant, nivel>
     */
    public Map<CustomEnchant, Integer> getEnchantsFromBook(ItemStack book) {
        return getEnchants(book);
    }

    /**
     * Elimina un encantamiento personalizado del sistema.
     * @param id El ID del encantamiento a eliminar
     */
    public void unregister(String id) {
        String key = id.toLowerCase();
        CustomEnchant enchant = enchants.remove(key);
        if (enchant != null) {
            wrappers.remove(key);
            plugin.getLogger().info("Desregistrado encantamiento: " + enchant.getPlainDisplayName());
        }
    }
}
