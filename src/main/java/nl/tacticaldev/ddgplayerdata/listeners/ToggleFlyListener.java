package nl.tacticaldev.ddgplayerdata.listeners;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class ToggleFlyListener implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        DataHandler dataHandler = DDGPlayerData.getDataHandler();
        PlayerData playerData = dataHandler.getUserByPlayer(player);
        ISettings settings = DDGPlayerData.getInstance().getSettings();

        playerData.setFly(event.isFlying());
        dataHandler.save(player);

        playerData.fly().thenAccept((b) -> {
            if (b) {
                player.sendMessage(Utils.replaceColor(settings.getPrefix() + "&aFly &6&lenabled&a!"));
            } else {
                player.sendMessage(Utils.replaceColor(settings.getPrefix() + "&aFly &6&ldisabled&a!"));
            }
        });
    }
}
