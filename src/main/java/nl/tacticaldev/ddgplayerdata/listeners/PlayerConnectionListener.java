package nl.tacticaldev.ddgplayerdata.listeners;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.data.DataPlayer;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.settings.storage.MongoFactory;
import nl.tacticaldev.ddgplayerdata.utils.DateUtil;
import nl.tacticaldev.ddgplayerdata.utils.Utils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ISettings settings = DDGPlayerData.getInstance().getSettings();

        if (settings.isMongo()) {
            DBCollection collection = MongoFactory.getCollection(settings.getConfig().getString("mongo.collections.playerData"));
            DBObject playerObject = collection.findOne(new BasicDBObject("uuid", player.getUniqueId()));

            if (playerObject == null) {
                BasicDBObject object = new BasicDBObject();
                object.put("uuid", player.getUniqueId());
                object.put("playerName", player.getName());
                object.put("GameMode", player.getName());
                collection.insert(object);
            }
        } else if (settings.isMysql()) {
            DataHandler dataHandler = DDGPlayerData.getDataHandler();
            PlayerData playerData = dataHandler.getUserByPlayer(event.getPlayer());

            try {
                dataHandler.load(player).get(1L, TimeUnit.SECONDS);
                long ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

                playerData.setOnlineTime(DateUtil.formatTimeMillis(ticks * 50L));
                dataHandler.save(player);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataHandler dataHandler = DDGPlayerData.getDataHandler();
        PlayerData playerData = dataHandler.getUserByPlayer(event.getPlayer());

        ISettings settings = DDGPlayerData.getInstance().getSettings();

        if (settings.isMysql()) {
            Utils utils = new Utils();

            playerData.setLastLocation(utils.locationToDbString(event.getPlayer().getLocation()));
            dataHandler.save(event.getPlayer());
        }
    }
}
