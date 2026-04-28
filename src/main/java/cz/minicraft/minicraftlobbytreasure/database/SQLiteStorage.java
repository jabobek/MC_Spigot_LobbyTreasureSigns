package cz.minicraft.minicraftlobbytreasure.database;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteStorage implements TreasureStorage {

    private final MinicraftLobbyTreasure plugin;
    private Connection connection;
    private final String serverId;
    private String tableName;

    public SQLiteStorage(MinicraftLobbyTreasure plugin, String serverId) {
        this.plugin = plugin;
        this.serverId = serverId;
    }

    @Override
    public void connect() throws SQLException {
        File dbFile = new File(plugin.getDataFolder(), "treasures.db");
        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
        }
        this.tableName = plugin.getConfigManager().getMysqlTable();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            plugin.getLogger().info("SQLite connected.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver not found!");
            throw new SQLException(e);
        }
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            plugin.getLogger().info("SQLite disconnected.");
        }
    }

    @Override
    public void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "treasure_id INT NOT NULL," +
                    "server_id VARCHAR(64) NOT NULL," +
                    "found_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                    "UNIQUE(player_uuid, treasure_id, server_id)" +
                    ");");
        }
    }

    @Override
    public boolean hasFoundTreasure(UUID playerUuid, int treasureId, String serverId) {
        String sql = "SELECT 1 FROM " + tableName + " WHERE player_uuid = ? AND treasure_id = ? AND server_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setInt(2, treasureId);
            ps.setString(3, serverId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking if treasure was found: " + e.getMessage());
            e.printStackTrace();
            return true; // Assume found to prevent reward duplication on error
        }
    }

    @Override
    public void markTreasureAsFound(UUID playerUuid, int treasureId, String serverId) {
        String sql = "INSERT INTO " + tableName + " (player_uuid, treasure_id, server_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setInt(2, treasureId);
            ps.setString(3, serverId);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error marking treasure as found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<DatabaseManager.FoundTreasure> getFoundTreasures(UUID playerUuid, String serverId) {
        List<DatabaseManager.FoundTreasure> foundTreasures = new ArrayList<>();
        String sql = "SELECT treasure_id, server_id, found_at FROM " + tableName + " WHERE player_uuid = ? AND server_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, serverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    foundTreasures.add(new DatabaseManager.FoundTreasure(
                            rs.getInt("treasure_id"),
                            rs.getString("server_id"),
                            rs.getTimestamp("found_at")));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting found treasures: " + e.getMessage());
            e.printStackTrace();
        }
        return foundTreasures;
    }
}
