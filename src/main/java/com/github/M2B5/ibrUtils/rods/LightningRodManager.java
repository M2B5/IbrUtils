package com.github.M2B5.ibrUtils.rods;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningRodManager {

    @Getter
    private static boolean lightningRodsEnabled = true;
    
    private static final Map<UUID, Boolean> playerToggles = new HashMap<>();

    public static void setLightningRodsEnabled(boolean lightningRodsEnabled) {
        LightningRodManager.lightningRodsEnabled = lightningRodsEnabled;
    }
    
    public static boolean isPlayerToggleEnabled(UUID playerId) {
        return playerToggles.getOrDefault(playerId, true);
    }
    
    public static void setPlayerToggle(UUID playerId, boolean enabled) {
        playerToggles.put(playerId, enabled);
    }
    
    public static boolean canPlayerSeeRods(UUID playerId) {
        return lightningRodsEnabled && isPlayerToggleEnabled(playerId);
    }
}
