package com.customenchants.utils;

import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Utilidades para efectos de partículas.
 */
public class ParticleUtils {

    public static void spawnHeartParticles(Location loc) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(Particle.HEART, loc, 5, 0.3, 0.3, 0.3, 0.1);
    }

    public static void spawnBloodParticles(Location loc) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(Particle.CRIT, loc, 10, 0.3, 0.5, 0.3, 0.2);
    }

    public static void spawnLightningParticles(Location loc) {
        if (loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 20, 0.5, 1.0, 0.5, 0.1);
    }
}
