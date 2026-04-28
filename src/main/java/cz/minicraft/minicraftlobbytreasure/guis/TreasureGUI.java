package cz.minicraft.minicraftlobbytreasure.guis;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.database.DatabaseManager;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

public class TreasureGUI implements InventoryHolder {

    private final MinicraftLobbyTreasure plugin;
    private final Player viewer; // The player who opened the GUI
    private final UUID targetUuid; // The UUID of the player whose treasures are being viewed
    private int currentPage = 1;
    private Inventory inventory;

    public TreasureGUI(MinicraftLobbyTreasure plugin, Player viewer, UUID targetUuid) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.targetUuid = targetUuid;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void open() {
        open(1);
    }

    public void open(int page) {
        this.currentPage = page;
        List<Treasure> allTreasures = plugin.getTreasureManager().getAllTreasures();
        // Sort treasures by ID in ascending order
        allTreasures.sort(java.util.Comparator.comparingInt(Treasure::id));

        Map<Integer, DatabaseManager.FoundTreasure> foundTreasuresMap = plugin.getDatabaseManager().getFoundTreasures(targetUuid).stream()
                .collect(Collectors.toMap(DatabaseManager.FoundTreasure::treasureId, ft -> ft));

        String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString("gui.title"));
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetUuid);
        String targetName = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        
        if (title.contains("%player%")) {
            title = title.replace("%player%", targetName);
        } else if (!viewer.getUniqueId().equals(targetUuid)) {
            title = title + ChatColor.DARK_GRAY + " (" + targetName + ")";
        }

        // Pagination settings
        int itemsPerPage = 45; // 5 rows
        int totalPages = (int) Math.ceil(allTreasures.size() / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        int inventorySize = 54; // Always 6 rows for pagination
        String fullTitle = title + (totalPages > 1 ? ChatColor.DARK_GRAY + " (" + page + "/" + totalPages + ")" : "");
        if (fullTitle.length() > 32) { // Just in case title is too long for some versions
             // fullTitle = fullTitle.substring(0, 32); 
        }
        
        this.inventory = Bukkit.createInventory(this, inventorySize, fullTitle);

        // Add treasures for the current page
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allTreasures.size());

        for (int i = startIndex; i < endIndex; i++) {
            Treasure treasure = allTreasures.get(i);
            boolean isFound = foundTreasuresMap.containsKey(treasure.id());
            DatabaseManager.FoundTreasure foundTreasure = foundTreasuresMap.get(treasure.id());

            String itemName = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString("gui.item_name").replace("%number%", String.valueOf(treasure.id())));
            List<String> lore = new ArrayList<>();
            String lorePath = isFound ? "gui.item_lore.found" : "gui.item_lore.not_found";

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone(plugin.getConfigManager().getTimezone()));

            for (String line : plugin.getConfigManager().getConfig().getStringList(lorePath)) {
                String processedLine = line.replace("%type%", treasure.type().name());
                if (isFound && foundTreasure != null) {
                    processedLine = processedLine.replace("%date%", dateFormat.format(foundTreasure.foundAt()));
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', processedLine));
            }

            ItemStack item = createItem(treasure, isFound, itemName, lore);
            this.inventory.addItem(item);
        }

        // Add navigation items on the bottom row (slots 45-53)
        if (totalPages > 1 || page == 1) {
            // Close button (always slot 49 or 45/53 depending on page)
            if (page == 1) {
                this.inventory.setItem(49, createNavigationItem("pagination.close"));
            } else {
                this.inventory.setItem(48, createNavigationItem("pagination.close"));
            }

            // Previous page
            if (page > 1) {
                this.inventory.setItem(45, createNavigationItem("pagination.previous_page"));
            }

            // Next page
            if (page < totalPages) {
                this.inventory.setItem(53, createNavigationItem("pagination.next_page"));
            }

            // Page info
            ItemStack info = createNavigationItem("pagination.info");
            ItemMeta infoMeta = info.getItemMeta();
            if (infoMeta != null) {
                String infoName = infoMeta.getDisplayName()
                        .replace("%current%", String.valueOf(page))
                        .replace("%total%", String.valueOf(totalPages));
                infoMeta.setDisplayName(infoName);
                info.setItemMeta(infoMeta);
            }
            this.inventory.setItem(page == 1 ? 48 : 49, info);
        } else if (totalPages == 1) {
             this.inventory.setItem(49, createNavigationItem("pagination.close"));
        }

        viewer.openInventory(this.inventory);
    }

    private ItemStack createItem(Treasure treasure, boolean isFound, String name, List<String> lore) {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.materials." + treasure.type().name().toLowerCase() + "." + (isFound ? "found" : "not_found"));
        ItemStack item;
        try {
            item = new ItemStack(Material.valueOf(materialName != null ? materialName.toUpperCase() : "CHEST"));
        } catch (IllegalArgumentException e) {
            item = new ItemStack(Material.CHEST);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNavigationItem(String path) {
        String materialName = plugin.getConfigManager().getConfig().getString("gui." + path + ".material");
        String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString("gui." + path + ".name", " "));
        ItemStack item;
        try {
            item = new ItemStack(Material.valueOf(materialName != null ? materialName.toUpperCase() : "STONE"));
        } catch (IllegalArgumentException e) {
            item = new ItemStack(Material.STONE);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
