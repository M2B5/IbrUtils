package com.github.M2B5.ibrUtils.hotbarItems;

import com.github.M2B5.ibrUtils.IbrUtils;
import me.makkuusen.timing.system.api.TimingSystemAPI;
import me.makkuusen.timing.system.tplayer.TPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HotbarItemsManager {

    public static final String LONELINESS_TOGGLE = "lonelinessToggle";
    private static final NamespacedKey COMMAND_KEY = new NamespacedKey(IbrUtils.getInstance(), "hotbar_command");

    public static void giveItems(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            player.getInventory().setItem(slot, buildItem(player, slot));
        }
    }

    public static void refreshLonelinessToggles(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            ConfigurationSection section = getSection(slot);
            if (section != null && LONELINESS_TOGGLE.equalsIgnoreCase(section.getString("item"))
                    && getCommand(player.getInventory().getItem(slot)) != null) {
                player.getInventory().setItem(slot, buildItem(player, slot));
            }
        }
    }

    public static String getCommand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(COMMAND_KEY, PersistentDataType.STRING);
    }

    private static ConfigurationSection getSection(int slot) {
        return IbrUtils.getInstance().getConfig().getConfigurationSection("hotbaritems." + slot);
    }

    private static ItemStack buildItem(Player player, int slot) {
        ConfigurationSection section = getSection(slot);
        if (section == null) return null;

        String itemName = section.getString("item", "");
        Material material = LONELINESS_TOGGLE.equalsIgnoreCase(itemName)
                ? (isLonely(player) ? Material.LIME_DYE : Material.RED_DYE)
                : Material.matchMaterial(itemName);
        if (material == null) {
            IbrUtils.getInstance().getLogger().warning("Unknown hotbar item material '" + itemName + "' in slot " + slot);
            return null;
        }

        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            TextColor colour = TextColor.fromHexString("#" + section.getString("namecolour", "FFFFFF"));
            meta.displayName(Component.text(section.getString("name", ""))
                    .color(colour == null ? TextColor.color(0xFFFFFF) : colour)
                    .decoration(TextDecoration.ITALIC, false));
            meta.getPersistentDataContainer().set(COMMAND_KEY, PersistentDataType.STRING, section.getString("command", ""));
        });
        return item;
    }

    private static boolean isLonely(Player player) {
        TPlayer tPlayer = TimingSystemAPI.getTPlayer(player.getUniqueId());
        return tPlayer != null && tPlayer.getSettings().isLonely();
    }
}
