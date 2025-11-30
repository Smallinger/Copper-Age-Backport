package com.github.smallinger.copperagebackport.client.endflash;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to access EndFlashState from ClientLevel.
 * Use EndFlashAccessor.get(clientLevel) to get the EndFlashState.
 */
public interface EndFlashAccessor {
    
    @Nullable
    EndFlashState copperagebackport$getEndFlashState();
    
    /**
     * Utility method to get EndFlashState from a ClientLevel
     */
    @Nullable
    static EndFlashState get(ClientLevel level) {
        return ((EndFlashAccessor) level).copperagebackport$getEndFlashState();
    }
}
