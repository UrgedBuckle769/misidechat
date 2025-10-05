package fun.yamds.maplegroveMisidechat;

import fun.yamds.maplegroveMisidechat.listener.ChunkListener;
import fun.yamds.maplegroveMisidechat.listener.chatListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MaplegroveMisidechat extends JavaPlugin {
    public final Logger logger =  Bukkit.getLogger();
    private static MaplegroveMisidechat instance;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        config = MaplegroveMisidechat.getInstance().getConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new chatListener(), this);
        pluginManager.registerEvents(new ChunkListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MaplegroveMisidechat getInstance() {
        return instance;
    }
}
