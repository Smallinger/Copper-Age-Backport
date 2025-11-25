package com.github.smallinger.copperagebackport.entity.ai.navigation;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;

/**
 * Erweiterte Navigation für Copper Golem mit besserer Pfadberechnung.
 * Verwendet längere Pfade um Blockieren zu vermeiden.
 */
public class CopperGolemNavigation extends GroundPathNavigation {
    private float requiredPathLength = 16.0F;
    
    public CopperGolemNavigation(Mob mob, Level level) {
        super(mob, level);
    }
    
    /**
     * Setzt die minimale Pfadlänge für die Pathfinding-Berechnung.
     * Höhere Werte = längere, bessere Pfade = weniger Blockieren
     */
    public void setRequiredPathLength(float requiredPathLength) {
        this.requiredPathLength = requiredPathLength;
    }
    
    /**
     * Gibt die minimale Pfadlänge zurück
     */
    public float getRequiredPathLength() {
        return this.requiredPathLength;
    }
}

