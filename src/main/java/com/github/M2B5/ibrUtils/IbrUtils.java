package com.github.M2B5.ibrUtils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class IbrUtils extends JavaPlugin {

    @Getter
    private static IbrUtils instance;

    @Override
    public void onEnable() {
        instance = this;
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
    }

    private void registerCommands() {
        this.getCommand("togglerods").setExecutor(new ToggleRodsCommand());
    }
}
