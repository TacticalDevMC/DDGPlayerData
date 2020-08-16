package nl.tacticaldev.ddgplayerdata.commands;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.DataPlayer;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class OnlineTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        DataHandler dataHandler = DDGPlayerData.getDataHandler();

        if (args.length == 0) {
            PlayerData playerData = dataHandler.getUserByPlayer(player);

            player.sendMessage(Utils.replaceColor("&7----------------------"));
            playerData.getTime().thenAccept(s -> {
                if (s.equals("0")) {
                    player.sendMessage(Utils.replaceColor("&3Online time: &c0"));
                } else {
                    player.sendMessage(Utils.replaceColor("&3Online time: &6" + s));
                }
            });
            player.sendMessage(Utils.replaceColor("&7----------------------"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Utils.replaceColor("&cThis player is offline!"));
            return false;
        }

        PlayerData playerData = dataHandler.getUserByPlayer(target);

        player.sendMessage(Utils.replaceColor("&7------- &6Player: " + target.getName() + "&7 ---------------"));
        player.sendMessage(Utils.replaceColor("&3Online time: &6" + playerData.getTime()));
        player.sendMessage(Utils.replaceColor("&7----------------------"));
        return false;
    }
}
