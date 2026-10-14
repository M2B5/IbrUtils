package com.github.M2B5.ibrUtils.hotbarItems;

import com.github.M2B5.ibrUtils.IbrUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class HotbarItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (use(event.getPlayer(), event.getItem())) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (use(event.getPlayer(), event.getItemDrop().getItemStack())) event.setCancelled(true);
    }

    private boolean use(Player player, ItemStack item) {
        String command = HotbarItemsManager.getCommand(item);
        if (command == null) return false;
        if (!command.isEmpty()) player.performCommand(command);
        Bukkit.getScheduler().runTask(IbrUtils.getInstance(), () -> HotbarItemsManager.refreshLonelinessToggles(player));
        return true;
    }
}
