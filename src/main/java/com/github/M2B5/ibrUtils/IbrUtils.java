package com.github.M2B5.ibrUtils;

import com.github.M2B5.ibrUtils.hotbarItems.HotbarItemListener;
import com.github.M2B5.ibrUtils.hotbarItems.HotbarItemsCommand;
import com.github.M2B5.ibrUtils.rods.LightningRodListener;
import com.github.M2B5.ibrUtils.rods.ToggleRodsCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class IbrUtils extends JavaPlugin {

    @Getter
    private static IbrUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BoatKiller(), this);
        getServer().getPluginManager().registerEvents(new LightningRodListener(), this);
        getServer().getPluginManager().registerEvents(new HotbarItemListener(), this);
    }

    private void registerCommands() {
        this.getCommand("togglerods").setExecutor(new ToggleRodsCommand());
        this.getCommand("hotbaritems").setExecutor(new HotbarItemsCommand());
    }
}
