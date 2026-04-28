package cz.minicraft.minicraftlobbytreasure.commands;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import cz.minicraft.minicraftlobbytreasure.guis.TreasureGUI;
import cz.minicraft.minicraftlobbytreasure.managers.Treasure;
import cz.minicraft.minicraftlobbytreasure.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class TreasureCommand implements CommandExecutor {

    private final MinicraftLobbyTreasure plugin;

    public TreasureCommand(MinicraftLobbyTreasure plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfigManager().getLang().getString("command.player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            if (player.hasPermission("mctreasure.admin")) {
                for (String line : plugin.getConfigManager().getLang().getStringList("command.help_admin")) {
                    MessageUtils.sendMessageWithoutPrefix(player, line);
                }
            } else {
                for (String line : plugin.getConfigManager().getLang().getStringList("command.help_player")) {
                    MessageUtils.sendMessageWithoutPrefix(player, line);
                }
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "gui":
                if (args.length > 1) { // Admin wants to view another player's GUI
                    if (!player.hasPermission("mctreasure.admin")) {
                        MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.no_permission"));
                        return true;
                    }
                    String targetPlayerName = args[1];
                    // Attempt to get online player first
                    Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
                    UUID targetUuid = null;
                    String targetName = "";
                    if (targetPlayer != null) {
                        targetUuid = targetPlayer.getUniqueId();
                        targetName = targetPlayer.getName();
                    } else { // Try to get offline player
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetPlayerName);
                        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
                            targetUuid = offlinePlayer.getUniqueId();
                            targetName = offlinePlayer.getName();
                        }
                    }

                    if (targetUuid != null) {
                        new TreasureGUI(plugin, player, targetUuid).open();
                    } else {
                        MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.player_not_found").replace("%player%", targetPlayerName));
                    }

                } else { // Player wants to view their own GUI
                    new TreasureGUI(plugin, player, player.getUniqueId()).open();
                }
                break;
            case "tp":
                if (!player.hasPermission("mctreasure.admin")) {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.teleport_no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    for (String line : plugin.getConfigManager().getLang().getStringList("command.help_admin")) {
                        MessageUtils.sendMessageWithoutPrefix(player, line);
                    }
                    return true;
                }
                int treasureId;
                try {
                    treasureId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.treasure_not_found"));
                    return true;
                }

                Optional<Treasure> treasureOpt = plugin.getTreasureManager().getTreasureById(treasureId);
                if (treasureOpt.isPresent()) {
                    player.teleport(treasureOpt.get().location());
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.teleported").replace("%number%", String.valueOf(treasureId)));
                } else {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.treasure_not_found"));
                }
                break;
            case "confirm":
                if (!player.hasPermission("mctreasure.admin")) {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.no_permission"));
                    return true;
                }
                Location loc = plugin.getDeletionManager().getPendingDeletion(player);
                if (loc != null) {
                    plugin.getTreasureManager().deleteTreasure(loc);
                    loc.getBlock().setType(Material.AIR);
                    plugin.getDeletionManager().confirmDeletion(player);
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.deletion_successful"));
                } else {
                    MessageUtils.sendMessage(player, plugin.getConfigManager().getLang().getString("command.no_pending_deletion"));
                }
                break;
            default:
                if (player.hasPermission("mctreasure.admin")) {
                    for (String line : plugin.getConfigManager().getLang().getStringList("command.help_admin")) {
                        MessageUtils.sendMessageWithoutPrefix(player, line);
                    }
                } else {
                    for (String line : plugin.getConfigManager().getLang().getStringList("command.help_player")) {
                        MessageUtils.sendMessageWithoutPrefix(player, line);
                    }
                }
                break;
        }

        return true;
    }
}
