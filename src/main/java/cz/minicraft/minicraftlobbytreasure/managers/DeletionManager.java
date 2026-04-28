package cz.minicraft.minicraftlobbytreasure.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeletionManager {

    private final Map<UUID, Location> pendingDeletions = new HashMap<>();

    public void requestDeletion(Player player, Location location) {
        pendingDeletions.put(player.getUniqueId(), location);
    }

    public Location getPendingDeletion(Player player) {
        return pendingDeletions.get(player.getUniqueId());
    }

    public void confirmDeletion(Player player) {
        pendingDeletions.remove(player.getUniqueId());
    }
}
