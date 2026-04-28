package cz.minicraft.minicraftlobbytreasure.listeners;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import cz.minicraft.minicraftlobbytreasure.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

import cz.minicraft.minicraftlobbytreasure.guis.TreasureGUI;

public class GUIListener implements Listener {

    private final MinicraftLobbyTreasure plugin;

    public GUIListener(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof TreasureGUI)) {
            return;
        }

        TreasureGUI gui = (TreasureGUI) clickedInventory.getHolder();
        event.setCancelled(true); // Prevent players from taking items

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        int slot = event.getRawSlot();

        // Handle navigation items
        if (slot >= 45 && slot <= 53) {
            if (slot == 45) { // Previous page
                if (gui.getCurrentPage() > 1) {
                    gui.open(gui.getCurrentPage() - 1);
                }
            } else if (slot == 53) { // Next page
                List<Treasure> allTreasures = plugin.getTreasureManager().getAllTreasures();
                int totalPages = (int) Math.ceil(allTreasures.size() / 45.0);
                if (gui.getCurrentPage() < totalPages) {
                    gui.open(gui.getCurrentPage() + 1);
                }
            } else if (slot == 48 || slot == 49) {
                // Check if it's the close button
                String closeName = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString("gui.pagination.close.name", ""));
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null && meta.getDisplayName().equals(closeName)) {
                    player.closeInventory();
                }
            }
            return;
        }

        // Handle treasure items
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = ChatColor.stripColor(meta.getDisplayName());
        try {
            String numberString = displayName.replaceAll("[^0-9]", "");
            if (numberString.isEmpty()) return;
            
            int treasureId = Integer.parseInt(numberString);
            
            if (player.hasPermission("mctreasure.admin")) {
                Optional<Treasure> treasureOpt = plugin.getTreasureManager().getTreasureById(treasureId);
                if (treasureOpt.isPresent()) {
                    player.teleport(treasureOpt.get().location());
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.teleported").replace("%number%", String.valueOf(treasureId)));
                    player.closeInventory();
                } else {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.treasure_not_found"));
                }
            }
        } catch (NumberFormatException e) {
            // Not a treasure item
        }
    }
}
