package com.github.M2B5.ibrUtils;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class BoatKiller implements Listener {
    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Arrays.stream(event.getChunk().getEntities())
                .filter(Boat.class::isInstance)
                .filter(entity -> entity.getPassengers().stream().noneMatch(Player.class::isInstance))
                .forEach(entity -> {
                    entity.getPassengers().forEach(Entity::remove);
                    entity.remove();
                });
    }
}
