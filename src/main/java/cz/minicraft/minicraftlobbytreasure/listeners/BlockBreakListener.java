package cz.minicraft.minicraftlobbytreasure.listeners;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import cz.minicraft.minicraftlobbytreasure.utils.MessageUtils;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;

public class BlockBreakListener implements Listener {

    private final MinicraftLobbyTreasure plugin;

    public BlockBreakListener(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign)) {
            return;
        }

        Player player = event.getPlayer();
        Optional<Treasure> treasureOpt = plugin.getTreasureManager().getTreasureByLocation(event.getBlock().getLocation());

        if (treasureOpt.isPresent()) {
            if (player.hasPermission("mctreasure.admin")) {
                event.setCancelled(true);
                plugin.getDeletionManager().requestDeletion(player, event.getBlock().getLocation());
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.confirm_deletion"));
            } else {
                event.setCancelled(true);
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.no_permission"));
            }
        }
    }
}
