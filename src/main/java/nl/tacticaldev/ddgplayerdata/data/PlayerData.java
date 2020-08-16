package nl.tacticaldev.ddgplayerdata.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

@Setter
@Getter
public class PlayerData {

    private Player player;
    private UUID uuid;
    private String name, gameMode, lastLocation;
    private boolean fly;
    private String onlineTime;

    public PlayerData(UUID uuid, String name, String gameMode, String lastLocation, boolean fly, String onlineTime) {
        this.uuid = uuid;
        this.name = name;
        this.gameMode = gameMode;
        this.lastLocation = lastLocation;
        this.fly = fly;
        this.onlineTime = onlineTime;
    }

    public CompletableFuture<String> getTime() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, String> map = new HashMap<>();

            map.put(uuid.toString(), getOnlineTime());

            return map.get(uuid.toString());
        });
    }

    public CompletableFuture<String> getLocation() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, String> map = new HashMap<>();

            map.put(uuid.toString(), getLastLocation());
            return map.get(uuid.toString());
        });
    }

    public CompletableFuture<Boolean> fly() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, Boolean> map = new HashMap<>();

            map.put(uuid.toString(), isFly());
            return map.get(uuid.toString());
        });
    }

    public CompletableFuture<String> gameMode() {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<String, String> map = new HashMap<>();

            map.put(uuid.toString(), getGameMode());
            return map.get(uuid.toString());
        });
    }
}
