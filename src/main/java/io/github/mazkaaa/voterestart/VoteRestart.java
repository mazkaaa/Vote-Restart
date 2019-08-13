package io.github.mazkaaa.voterestart;

import io.github.mazkaaa.voterestart.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteRestart extends JavaPlugin {
    private static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getCommand("voterestart").setExecutor(new VoteCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        plugin = null;
    }

    public void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}
