package com.github.M2B5.ibrUtils.rods;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.M2B5.ibrUtils.IbrUtils;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import me.makkuusen.timing.system.api.TimingSystemAPI;

public class LightningRodListener implements Listener {

    private final Map<UUID, ItemDisplay> playerRods = new HashMap<>();
    private int tickCounter = 0;
    private static final int DEBUG_INTERVAL = 100; // Log every 100 ticks (5 seconds)

    @EventHandler
    public void onTickStart(ServerTickStartEvent e) {
        tickCounter++;
        //boolean shouldDebug = tickCounter % DEBUG_INTERVAL == 0;
        boolean shouldDebug = false; // Uncomment above and comment this line to enable debug logging every 5 seconds

        if (!LightningRodManager.isLightningRodsEnabled()) {
            if (shouldDebug) {
                Bukkit.getLogger().info("[RodDebug] Global rods disabled, removing all rods");
            }
            removeAllRods();
            return;
        }

        if (shouldDebug) {
            Bukkit.getLogger().info("[RodDebug] === Rod Visibility Debug ===");
            Bukkit.getLogger().info("[RodDebug] Global enabled: " + LightningRodManager.isLightningRodsEnabled());
            Bukkit.getLogger().info("[RodDebug] Active rods: " + playerRods.size());
        }

        for (Player rodOwner : Bukkit.getOnlinePlayers()) {
            // Remove rod if player is not in a vehicle
            if (rodOwner.getVehicle() == null || rodOwner.getVehicle().getPassengers().isEmpty()) {
                if (shouldDebug && playerRods.containsKey(rodOwner.getUniqueId())) {
                    Bukkit.getLogger().info("[RodDebug] " + rodOwner.getName() + ": Not in vehicle, removing rod");
                }
                removeRodForPlayer(rodOwner.getUniqueId());
                continue;
            }

            // Remove rod if player is not in a heat
            if (!isPlayerInHeat(rodOwner)) {
                if (shouldDebug && playerRods.containsKey(rodOwner.getUniqueId())) {
                    Bukkit.getLogger().info("[RodDebug] " + rodOwner.getName() + ": Not in heat, removing rod");
                }
                removeRodForPlayer(rodOwner.getUniqueId());
                continue;
            }

            // Remove rod if player is not the first passenger
            if (!((Entity) rodOwner.getVehicle().getPassengers().getFirst()).getUniqueId().equals(rodOwner.getUniqueId())) {
                if (shouldDebug) {
                    Bukkit.getLogger().info("[RodDebug] " + rodOwner.getName() + ": Not first passenger, removing rod");
                }
                removeRodForPlayer(rodOwner.getUniqueId());
                continue;
            }

            ItemDisplay rod = playerRods.get(rodOwner.getUniqueId());
            Location targetLocation = rodOwner.getLocation();
            targetLocation.setYaw(0F);
            targetLocation.setPitch(0F);

            if (rod == null || !rod.isValid()) {
                rod = createRodForPlayer(rodOwner, targetLocation);
                playerRods.put(rodOwner.getUniqueId(), rod);
                if (shouldDebug) {
                    Bukkit.getLogger().info("[RodDebug] " + rodOwner.getName() + ": Created new rod");
                }
            } else {
                rod.teleport(targetLocation);
            }

            updateRodVisibility(rod, rodOwner, shouldDebug);
        }
    }

    private static final Transformation FLIPPED_ROD = new Transformation(
        new Vector3f(0, 0.375F, 0),
        new Quaternionf(new AxisAngle4f((float) Math.PI, 1, 0, 0)),
        new Vector3f(0.75F, 0.75F, 0.75F),
        new Quaternionf()
    );

    private ItemDisplay createRodForPlayer(Player rodOwner, Location location) {
        return location.getWorld().spawn(location, ItemDisplay.class, entity -> {
            entity.setInvulnerable(true);
            entity.setSilent(true);
            entity.setGravity(false);
            entity.setItemStack(new ItemStack(Material.LIGHTNING_ROD));
            entity.setTransformation(FLIPPED_ROD);
            entity.getPersistentDataContainer().set(
                Objects.requireNonNull(NamespacedKey.fromString("lightning_rod", (Plugin) IbrUtils.getInstance())),
                PersistentDataType.INTEGER, 1);
            entity.getPersistentDataContainer().set(
                Objects.requireNonNull(NamespacedKey.fromString("rod_owner", (Plugin) IbrUtils.getInstance())),
                PersistentDataType.STRING, rodOwner.getUniqueId().toString());
        });
    }

    private void updateRodVisibility(ItemDisplay rod, Player rodOwner, boolean debug) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            boolean canSeeRods = LightningRodManager.canPlayerSeeRods(viewer.getUniqueId());
            boolean sameHeat = arePlayersInSameHeat(viewer, rodOwner);
            
            if (debug) {
                Bukkit.getLogger().info("[RodDebug] Viewer: " + viewer.getName() + 
                    " | Owner: " + rodOwner.getName() + 
                    " | CanSeeRods: " + canSeeRods + 
                    " | SameHeat: " + sameHeat);
            }
            
            if (!canSeeRods || !sameHeat) {
                viewer.hideEntity((Plugin) IbrUtils.getInstance(), rod);
            } else {
                viewer.showEntity((Plugin) IbrUtils.getInstance(), rod);
                if (debug) {
                    Bukkit.getLogger().info("[RodDebug] -> SHOWING rod to " + viewer.getName());
                }
            }
        }
    }

    private void removeRodForPlayer(UUID playerId) {
        ItemDisplay rod = playerRods.remove(playerId);
        if (rod != null && rod.isValid()) {
            rod.remove();
        }
    }

    private void removeAllRods() {
        for (ItemDisplay rod : playerRods.values()) {
            if (rod != null && rod.isValid()) {
                rod.remove();
            }
        }
        playerRods.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeRodForPlayer(event.getPlayer().getUniqueId());
    }

    private boolean isPlayerInHeat(Player player) {
        var maybeDriver = TimingSystemAPI.getDriverFromRunningHeat(player.getUniqueId());
        return maybeDriver.isPresent();
    }

    private boolean arePlayersInSameHeat(Player viewer, Player rodOwner) {
        var maybeDriverViewer = TimingSystemAPI.getDriverFromRunningHeat(viewer.getUniqueId());
        if (maybeDriverViewer.isEmpty()) {
            return false;
        }
        var maybeDriverRodOwner = TimingSystemAPI.getDriverFromRunningHeat(rodOwner.getUniqueId());
        if (maybeDriverRodOwner.isEmpty()) {
            return false;
        }
        
        int viewerHeatId = maybeDriverViewer.get().getHeat().getId();
        int ownerHeatId = maybeDriverRodOwner.get().getHeat().getId();
        
        return viewerHeatId == ownerHeatId;
    }
}