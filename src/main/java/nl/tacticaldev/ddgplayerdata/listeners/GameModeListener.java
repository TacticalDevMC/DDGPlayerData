package nl.tacticaldev.ddgplayerdata.listeners;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class GameModeListener implements Listener {

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        DataHandler dataHandler = DDGPlayerData.getDataHandler();
        PlayerData playerData = dataHandler.getUserByPlayer(player);
        ISettings settings = DDGPlayerData.getInstance().getSettings();

        playerData.setGameMode(event.getNewGameMode().toString());
        dataHandler.save(player);

        playerData.gameMode().thenAccept((s) -> {
            s = event.getNewGameMode().toString();
            player.sendMessage(Utils.replaceColor(settings.getPrefix() + "&aGamemode changed to &6&l" + s + "&a!"));
        });
    }
}
