package com.github.M2B5.ibrUtils.rods;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleRodsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("ibrutils.togglerods")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.").color(TextColor.color(0xFF5555)));
            return true;
        }

        // Global toggle (admin only)
        if (args.length > 0 && args[0].equalsIgnoreCase("global")) {
            if (!sender.hasPermission("ibrutils.togglerods.global")) {
                sender.sendMessage(Component.text("You do not have permission to toggle global rod visibility.").color(TextColor.color(0xFF5555)));
                return true;
            }
            
            LightningRodManager.setLightningRodsEnabled(!LightningRodManager.isLightningRodsEnabled());
            sender.sendMessage(Component.text("Global lightning rods are now " + (LightningRodManager.isLightningRodsEnabled() ? "enabled" : "disabled") + ".").color(TextColor.color(0x55FF55)));
            return true;
        }

        // Personal toggle
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can toggle their personal rod visibility.").color(TextColor.color(0xFF5555)));
            return true;
        }

        boolean currentToggle = LightningRodManager.isPlayerToggleEnabled(player.getUniqueId());
        LightningRodManager.setPlayerToggle(player.getUniqueId(), !currentToggle);
        player.sendMessage(Component.text("Your lightning rod visibility is now " + (!currentToggle ? "enabled" : "disabled") + ".").color(TextColor.color(0x55FF55)));
        return true;
    }
}
