package cz.minicraft.minicraftlobbytreasure.placeholder;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TreasureExpansion extends PlaceholderExpansion {

    private final MinicraftLobbyTreasure plugin;

    public TreasureExpansion(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mctreasure";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("found_count")) {
            // Get the count of found treasures for the player
            // Using the new getFoundTreasures method from DatabaseManager which accepts UUID
            int count = plugin.getDatabaseManager().getFoundTreasures(player.getUniqueId()).size();
            return String.valueOf(count);
        }

        if (params.equalsIgnoreCase("total_count")) {
            int total = plugin.getTreasureManager().getAllTreasures().size();
            return String.valueOf(total);
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
