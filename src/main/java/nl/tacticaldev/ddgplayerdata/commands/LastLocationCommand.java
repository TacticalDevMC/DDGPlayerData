package nl.tacticaldev.ddgplayerdata.commands;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.DataPlayer;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

import static nl.tacticaldev.ddgplayerdata.utils.Utils.replaceColor;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class LastLocationCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (args.length == 0) {
            PlayerData playerData = DDGPlayerData.getDataHandler().getUserByPlayer(player);

            player.sendMessage(replaceColor("&7----------------------"));
            playerData.getLocation().thenAccept(s -> {
                if (s.equals("Geen")) {
                    player.sendMessage(replaceColor("&3LastLocation: &cNo Location Found"));
                } else {
                    Location loc = new Utils().DbStringToLocation(s);
                    player.sendMessage(replaceColor((String.format("&3LastLocation: &6&lX: &6%s&7, &6&lY: &6%s&7, &6&lZ: &6%s&7&3.", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))));
                }
            });
            player.sendMessage(replaceColor("&7----------------------"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(replaceColor("&cThis player is offline!"));
            return false;
        }

        PlayerData playerData = DDGPlayerData.getDataHandler().getUserByPlayer(target);

        player.sendMessage(replaceColor("&7------- &6Player: " + target.getName() + "&7 ---------------"));
        playerData.getLocation().thenAccept(s -> {
            if (s.equals("Geen")) {
                player.sendMessage(replaceColor("&3LastLocation: &cNo Location Found"));
            } else {
                Location loc = new Utils().DbStringToLocation(s);
                player.sendMessage(replaceColor((String.format("&3LastLocation: &6&lX: &6%s&7, &6&lY: &6%s&7, &6&lZ: &6%s&7&3.", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))));
            }
        });
        player.sendMessage(replaceColor("&7----------------------"));

        return false;
    }
}
