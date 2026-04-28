package cz.minicraft.minicraftlobbytreasure.listeners;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import cz.minicraft.minicraftlobbytreasure.managers.TreasureManager;
import cz.minicraft.minicraftlobbytreasure.managers.TreasureType;
import cz.minicraft.minicraftlobbytreasure.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

    private final MinicraftLobbyTreasure plugin;

    public SignChangeListener(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String line0 = event.getLine(0);
        if (line0 != null && (line0.equalsIgnoreCase("[treasure]") || line0.equalsIgnoreCase("[poklad]"))) {
            if (!player.hasPermission("mctreasure.admin")) {
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("sign.no_permission"));
                return;
            }

            int typeId;
            String line2Input = event.getLine(1);
            if (line2Input == null || line2Input.isEmpty()) {
                typeId = plugin.getConfigManager().getConfig().getInt("general.default_difficulty", 1);
            } else {
                try {
                    typeId = Integer.parseInt(line2Input);
                } catch (NumberFormatException e) {
                    typeId = plugin.getConfigManager().getConfig().getInt("general.default_difficulty", 1);
                }
            }

            TreasureType type = TreasureType.fromInt(typeId);
            if (type == null) {
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("sign.invalid_type"));
                return;
            }

            TreasureManager treasureManager = plugin.getTreasureManager();
            int treasureId;

            String line3Content = event.getLine(2);
            if (line3Content != null && !line3Content.isEmpty()) {
                try {
                    treasureId = Integer.parseInt(line3Content);
                    if (treasureManager.getTreasureById(treasureId).isPresent()) {
                        MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("sign.id_already_exists").replace("%id%", String.valueOf(treasureId)));
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Not a valid number, so we will auto-assign an ID
                    treasureId = treasureManager.getNextId();
                }
            } else {
                treasureId = treasureManager.getNextId();
            }

            Treasure treasure = treasureManager.createTreasure(treasureId, event.getBlock().getLocation(), type);

            String typeName = type.name().toLowerCase();
            String path = "sign." + typeName;

            String line1 = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString(path + ".line1", "").replace("%number%", String.valueOf(treasure.id())));
            String line2 = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString(path + ".line2", "").replace("%number%", String.valueOf(treasure.id())));
            String line3 = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString(path + ".line3", "").replace("%number%", String.valueOf(treasure.id())));
            String line4 = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getConfig().getString(path + ".line4", "").replace("%number%", String.valueOf(treasure.id())));

            event.setLine(0, line1);
            event.setLine(1, line2);
            event.setLine(2, line3);
            event.setLine(3, line4);

            MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("sign.created").replace("%number%", String.valueOf(treasure.id())));
        }
    }
}
