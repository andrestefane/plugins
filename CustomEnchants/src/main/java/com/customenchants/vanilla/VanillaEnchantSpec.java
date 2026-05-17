package com.customenchants.vanilla;

import com.customenchants.utils.MaterialLists;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Arrays;
import java.util.List;

public record VanillaEnchantSpec(
        String id,
        String name,
        String description,
        int maxLevel,
        List<Material> supportedMaterials,
        EquipmentSlotGroup activeSlot
) {

    public static final String NAMESPACE = "customenchants";

    public static List<VanillaEnchantSpec> all() {
        return List.of(
                melee("THUNDER_STRIKE", "Golpe Relampago", "Chance de invocar un rayo al golpear", 3),
                melee("LIFE_STEAL", "Robo de Vida", "Roba vida al golpear enemigos", 3),
                bow("EXPLOSIVE_ARROWS", "Flechas Explosivas", "Las flechas explotan al impactar", 3),
                melee("VAMPIRISM", "Vampirismo", "Absorbe vida al matar enemigos", 2),
                melee("BERSERKER", "Berserker", "Aumenta el dano cuanto menos vida tienes", 3),
                melee("ICE_ASPECT", "Aspecto Gelido", "Congela enemigos al golpearlos", 2),
                armor("MAGNETIC", "Magnetico", "Atrae drops cercanos al jugador", 3),
                durable("AUTO_REPAIR", "Auto-Reparacion", "El item se repara solo con el tiempo", 3),
                pickaxe("LUCKY_MINER", "Minero Afortunado", "Probabilidad de duplicar drops al minar", 3),
                melee("WEBBING", "Telarana", "Atrapa enemigos en telaranas", 2),
                durable("SOULBOUND", "Vinculado al Alma", "Conserva el item al morir", 1),
                melee("POISON_EDGE", "Filo Venenoso", "Envenena enemigos al golpear", 3),
                melee("NETHER_SLAYER", "Cazador del Nether", "Aumenta el dano contra criaturas del Nether", 3),
                melee("XP_BOOST", "Cosechador de Almas", "Aumenta la experiencia obtenida", 3),
                melee("HEAD_HUNTER", "Cazador de Cabezas", "Puede soltar cabezas de mobs", 3),
                melee("SPAWN_EGG", "Capturador", "Puede soltar spawn eggs", 3),
                pickaxe("TUNNEL", "Tunel", "Mina un tunel centrado y mas profundo por nivel", 5),
                pickaxe("AUTO_SMELT", "Auto-Fundicion", "Funde menas automaticamente al minar", 1)
        );
    }

    public String keyValue() {
        return id.toLowerCase();
    }

    private static VanillaEnchantSpec melee(String id, String name, String description, int maxLevel) {
        return new VanillaEnchantSpec(id, name, description, maxLevel, MaterialLists.MELEE_WEAPONS, EquipmentSlotGroup.MAINHAND);
    }

    private static VanillaEnchantSpec bow(String id, String name, String description, int maxLevel) {
        return new VanillaEnchantSpec(id, name, description, maxLevel,
                Arrays.asList(Material.BOW, Material.CROSSBOW), EquipmentSlotGroup.MAINHAND);
    }

    private static VanillaEnchantSpec pickaxe(String id, String name, String description, int maxLevel) {
        return new VanillaEnchantSpec(id, name, description, maxLevel, MaterialLists.PICKAXES, EquipmentSlotGroup.MAINHAND);
    }

    private static VanillaEnchantSpec armor(String id, String name, String description, int maxLevel) {
        return new VanillaEnchantSpec(id, name, description, maxLevel, MaterialLists.ARMOR, EquipmentSlotGroup.ARMOR);
    }

    private static VanillaEnchantSpec durable(String id, String name, String description, int maxLevel) {
        return new VanillaEnchantSpec(id, name, description, maxLevel, MaterialLists.ALL_DURABLE(), EquipmentSlotGroup.ANY);
    }
}
