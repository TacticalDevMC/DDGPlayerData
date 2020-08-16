package nl.tacticaldev.ddgplayerdata.commands;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class GameModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        DataHandler dataHandler = DDGPlayerData.getDataHandler();
        ISettings settings = DDGPlayerData.getInstance().getSettings();

        if (args.length == 0) {
            PlayerData playerData = dataHandler.getUserByPlayer(player);

            playerData.gameMode().thenAccept(s -> {
                player.sendMessage(Utils.replaceColor(settings.getPrefix() + "&3You're gameMode is &b&l" + s + "&3!"));
            });
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Utils.replaceColor("&cThis player is offline!"));
            return false;
        }

        PlayerData playerData = dataHandler.getUserByPlayer(target);

        playerData.gameMode().thenAccept(s -> {
            player.sendMessage(Utils.replaceColor(settings.getPrefix() + "&3" + target.getName() + "'s gameMode is &b&l" + s + "&3!"));
        });

        return false;
    }
}
