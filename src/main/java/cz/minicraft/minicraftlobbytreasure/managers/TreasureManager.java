package cz.minicraft.minicraftlobbytreasure.managers;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreasureManager {

    private final MinicraftLobbyTreasure plugin;
    private final List<Treasure> treasures = new ArrayList<>();
    private File treasuresFile;
    private FileConfiguration treasuresConfig;

    public TreasureManager(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    public void loadTreasures() {
        treasuresFile = new File(plugin.getDataFolder(), "treasures.yml");
        if (!treasuresFile.exists()) {
            try {
                treasuresFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create treasures.yml!");
                e.printStackTrace();
            }
        }
        treasuresConfig = YamlConfiguration.loadConfiguration(treasuresFile);
        treasures.clear(); // Clear before loading
        ConfigurationSection treasuresSection = treasuresConfig.getConfigurationSection("treasures");
        if (treasuresSection != null) {
            for (String key : treasuresSection.getKeys(false)) {
                int id = Integer.parseInt(key);
                Location location = treasuresSection.getLocation(key + ".location");
                TreasureType type = TreasureType.valueOf(treasuresSection.getString(key + ".type"));
                treasures.add(new Treasure(id, location, type));
            }
        }
    }

    public void saveTreasures() {
        try {
            treasuresConfig.save(treasuresFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save treasures.yml!");
            e.printStackTrace();
        }
    }

    public Treasure createTreasure(int id, Location location, TreasureType type) {
        Treasure treasure = new Treasure(id, location, type);
        treasures.add(treasure);

        String path = "treasures." + id;
        treasuresConfig.set(path + ".location", location);
        treasuresConfig.set(path + ".type", type.name());
        saveTreasures();
        return treasure;
    }

    public void deleteTreasure(Location location) {
        getTreasureByLocation(location).ifPresent(treasure -> {
            treasures.remove(treasure);
            treasuresConfig.set("treasures." + treasure.id(), null);
            saveTreasures();
        });
    }

    public Optional<Treasure> getTreasureById(int id) {
        return treasures.stream().filter(t -> t.id() == id).findFirst();
    }

    public Optional<Treasure> getTreasureByLocation(Location location) {
        return treasures.stream().filter(t -> t.location().equals(location)).findFirst();
    }

    public List<Treasure> getAllTreasures() {
        return new ArrayList<>(treasures);
    }

    public int getNextId() {
        if (treasures.isEmpty()) {
            return 1;
        }
        List<Integer> sortedIds = treasures.stream().map(Treasure::id).sorted().collect(java.util.stream.Collectors.toList());
        int expectedId = 1;
        for (int id : sortedIds) {
            if (id != expectedId) {
                return expectedId;
            }
            expectedId++;
        }
        return expectedId;
    }
}
