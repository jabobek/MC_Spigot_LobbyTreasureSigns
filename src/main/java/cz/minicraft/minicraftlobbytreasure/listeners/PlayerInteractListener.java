package cz.minicraft.minicraftlobbytreasure.listeners;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import cz.minicraft.minicraftlobbytreasure.utils.LogUtils;
import cz.minicraft.minicraftlobbytreasure.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final MinicraftLobbyTreasure plugin;

    public PlayerInteractListener(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Player player = event.getPlayer();
        Optional<Treasure> treasureOpt = plugin.getTreasureManager().getTreasureByLocation(event.getClickedBlock().getLocation());

        if (treasureOpt.isPresent()) {
            Treasure treasure = treasureOpt.get();
            if (plugin.getDatabaseManager().hasFoundTreasure(player.getUniqueId(), treasure.id())) {
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("treasure.already_found"));
            } else {
                plugin.getDatabaseManager().markTreasureAsFound(player.getUniqueId(), treasure.id());

                String messagePath = "treasure.found." + treasure.type().name().toLowerCase();
                MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString(messagePath));

                // Log the found treasure
                LogUtils.logFoundTreasure(player, treasure, treasure.location());

                // Spawn particle effect
                player.getWorld().spawnParticle(Particle.FALLING_DUST, treasure.location().clone().add(0.5, 0.5, 0.5), 5, 0.5, 0.5, 0.5, 0, Material.GOLD_BLOCK.createBlockData());


                List<String> commands = plugin.getConfigManager().getConfig().getStringList("rewards." + treasure.type().name().toLowerCase());
                for (String command : commands) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                }
            }
        }
    }
}
