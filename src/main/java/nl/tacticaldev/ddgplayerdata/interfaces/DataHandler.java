package nl.tacticaldev.ddgplayerdata.interfaces;

import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public interface DataHandler {

    PlayerData getUserByPlayer(Player value);

    void save(Player data);

    CompletableFuture<PlayerData> saveAll();

    CompletableFuture<PlayerData> load(Player player);

    boolean playerExists(String uuid);

}
