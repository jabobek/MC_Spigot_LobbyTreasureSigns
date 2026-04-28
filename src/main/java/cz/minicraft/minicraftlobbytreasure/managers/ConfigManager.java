package cz.minicraft.minicraftlobbytreasure.managers;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {

    private final MinicraftLobbyTreasure plugin;
    private FileConfiguration config;
    private File configFile;
    private FileConfiguration lang;
    private File langFile;

    public ConfigManager(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        // Load config.yml
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Load lang.yml
        langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getLang() {
        return lang;
    }

    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }

    public String getServerId() {
        return config.getString("database.server_id", "default");
    }

    public String getMysqlHost() {
        return config.getString("database.mysql.host", "localhost");
    }

    public int getMysqlPort() {
        return config.getInt("database.mysql.port", 3306);
    }

    public String getMysqlDatabase() {
        return config.getString("database.mysql.database", "minicraft_lobby_treasure");
    }

    public String getMysqlUsername() {
        return config.getString("database.mysql.username", "user");
    }

    public String getMysqlPassword() {
        return config.getString("database.mysql.password", "password");
    }

    public String getMysqlTable() {
        return config.getString("database.mysql.table", "found_treasures");
    }

    public String getTimezone() {
        return config.getString("general.timezone", "UTC");
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    public void saveLang() {
        try {
            lang.save(langFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save lang.yml!");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        lang = YamlConfiguration.loadConfiguration(langFile);
    }
}
