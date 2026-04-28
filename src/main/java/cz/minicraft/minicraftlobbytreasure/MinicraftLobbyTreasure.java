package cz.minicraft.minicraftlobbytreasure;

import cz.minicraft.minicraftlobbytreasure.commands.TreasureCommand;
import cz.minicraft.minicraftlobbytreasure.database.DatabaseManager;
import cz.minicraft.minicraftlobbytreasure.listeners.PlayerInteractListener;
import cz.minicraft.minicraftlobbytreasure.listeners.SignChangeListener;
import cz.minicraft.minicraftlobbytreasure.managers.ConfigManager;
import cz.minicraft.minicraftlobbytreasure.managers.TreasureManager;
import cz.minicraft.minicraftlobbytreasure.listeners.BlockBreakListener;
import cz.minicraft.minicraftlobbytreasure.managers.DeletionManager;
import cz.minicraft.minicraftlobbytreasure.listeners.GUIListener;
import cz.minicraft.minicraftlobbytreasure.placeholder.TreasureExpansion;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinicraftLobbyTreasure extends JavaPlugin {

    private static MinicraftLobbyTreasure instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private TreasureManager treasureManager;
    private DeletionManager deletionManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        treasureManager = new TreasureManager(this);
        treasureManager.loadTreasures();

        deletionManager = new DeletionManager();

        getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getCommand("mctreasure").setExecutor(new TreasureCommand(this));

        // Register PlaceholderAPI expansion
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TreasureExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        } else {
            getLogger().warning("PlaceholderAPI not found, PlaceholderAPI expansion will not be available.");
        }

        getLogger().info("MinicraftLobbyTreasure has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("MinicraftLobbyTreasure has been disabled!");
    }

    public static MinicraftLobbyTreasure getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TreasureManager getTreasureManager() {
        return treasureManager;
    }

    public DeletionManager getDeletionManager() {
        return deletionManager;
    }
}
