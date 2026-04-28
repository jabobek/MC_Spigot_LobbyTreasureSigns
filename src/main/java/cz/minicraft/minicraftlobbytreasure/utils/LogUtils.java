package cz.minicraft.minicraftlobbytreasure.utils;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LogUtils {

    public static void logFoundTreasure(Player player, Treasure treasure, Location location) {
        MinicraftLobbyTreasure plugin = MinicraftLobbyTreasure.getInstance();
        String logFilePath = plugin.getConfigManager().getConfig().getString("log_file_path", "logs/found_treasures.log");
        File logFile = new File(plugin.getDataFolder(), logFilePath);

        try {
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getConfigManager().getTimezone()));


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                String timestamp = dateFormat.format(new Date());
                String logEntry = String.format("[%s] Player %s (%s) found treasure #%d (%s) at %s, %s, %s (World: %s)%n",
                        timestamp,
                        player.getName(),
                        player.getUniqueId(),
                        treasure.id(),
                        treasure.type().name(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ(),
                        location.getWorld().getName());
                writer.write(logEntry);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write to found treasures log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
