package nl.tacticaldev.ddgplayerdata.commands;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.configuration.Config;
import nl.tacticaldev.ddgplayerdata.data.DataPlayer;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static nl.tacticaldev.ddgplayerdata.utils.Utils.replaceColor;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class ConvertCommand implements CommandExecutor {

    private final Config config;

    public ConvertCommand() {
        config = new Config("convert");
        config.reload();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        ISettings settings = DDGPlayerData.getInstance().getSettings();
        DataHandler dataHandler = DDGPlayerData.getDataHandler();

        if (args.length == 0) {
            PlayerData playerData = dataHandler.getUserByPlayer(player);

            config.setProperty("players.", player.getUniqueId().toString());
            config.setProperty("players." + player.getUniqueId().toString() + ".name", player.getName());
            config.setProperty("players." + player.getUniqueId().toString() + ".gamemode", playerData.getGameMode());
            config.setProperty("players." + player.getUniqueId().toString() + ".fly", playerData.fly());
            config.setProperty("players." + player.getUniqueId().toString() + ".onlineTime", playerData.getTime());
            config.setProperty("players." + player.getUniqueId().toString() + ".lastLocation", playerData.getLocation());
            config.save();

            player.sendMessage(Utils.replaceColor("&aSuccesfully converted from the Database to the convert.yml file."));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        PlayerData playerData = dataHandler.getUserByPlayer(target);

        if (target == null) {
            player.sendMessage(replaceColor("&cThis player is offline!"));
            return false;
        }

        config.setProperty("players." + target.getUniqueId().toString() + ".name", player.getName());
        config.setProperty("players." + target.getUniqueId().toString() + ".gamemode", playerData.getGameMode());
        config.setProperty("players." + target.getUniqueId().toString() + ".fly", playerData.fly());
        config.setProperty("players." + target.getUniqueId().toString() + ".onlineTime", playerData.getTime());
        config.setProperty("players." + target.getUniqueId().toString() + ".lastLocation", playerData.getLocation());
        config.save();

        player.sendMessage(Utils.replaceColor("&aSuccesfully converted from the Database to the convert.yml file."));

        return false;
    }
}
