package cz.minicraft.minicraftlobbytreasure.database;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.ConfigManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final MinicraftLobbyTreasure plugin;
    private TreasureStorage storage;
    private final String serverId;

    public record FoundTreasure(int treasureId, String serverId, java.util.Date foundAt) {}

    public DatabaseManager(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
        ConfigManager configManager = plugin.getConfigManager();
        this.serverId = configManager.getServerId();
        String dbType = configManager.getDatabaseType().toLowerCase();

        if (dbType.equals("mysql")) {
            this.storage = new MySQLStorage(plugin, serverId);
        } else { // default to sqlite
            this.storage = new SQLiteStorage(plugin, serverId);
        }
    }

    public void connect() {
        try {
            storage.connect();
            storage.createTables();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to the database or create tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            storage.disconnect();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not disconnect from the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasFoundTreasure(UUID playerUuid, int treasureId) {
        return storage.hasFoundTreasure(playerUuid, treasureId, serverId);
    }

    public void markTreasureAsFound(UUID playerUuid, int treasureId) {
        storage.markTreasureAsFound(playerUuid, treasureId, serverId);
    }

    public List<FoundTreasure> getFoundTreasures(UUID playerUuid) {
        return storage.getFoundTreasures(playerUuid, serverId);
    }
}
