package cz.minicraft.minicraftlobbytreasure.database;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface TreasureStorage {
    void connect() throws SQLException;
    void disconnect() throws SQLException;
    void createTables() throws SQLException;
    boolean hasFoundTreasure(UUID playerUuid, int treasureId, String serverId);
    void markTreasureAsFound(UUID playerUuid, int treasureId, String serverId);
    List<DatabaseManager.FoundTreasure> getFoundTreasures(UUID playerUuid, String serverId);
}
