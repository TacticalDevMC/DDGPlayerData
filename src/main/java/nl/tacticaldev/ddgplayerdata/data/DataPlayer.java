package nl.tacticaldev.ddgplayerdata.data;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.settings.storage.SQLFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class DataPlayer implements DataHandler {

    private static Map<String, PlayerData> dataHashMap = new HashMap<>();
    private static Collection<PlayerData> players;
    private SQLFactory factory = DDGPlayerData.getSqlFactory();

    public DataPlayer() {
        players = new HashSet<>();
    }

    @Override
    public PlayerData getUserByPlayer(Player value) {
        return getUserByUuid(value.getUniqueId());
    }

    public static PlayerData getUserByUuid(UUID value) {
        for (PlayerData user : players) {
            if (user.getUuid().equals(value)) {
                return user;
            }
        }
        return null;
    }

    public static Collection<PlayerData> getPlayers() {
        return players;
    }

    @Override
    public void save(Player data) {
        PlayerData player = getUserByPlayer(data);
        if (player != null) {
            try (Connection connection = factory.getConnection()) {
                try (PreparedStatement update = connection.prepareStatement("UPDATE " + factory.getPrefix() + "players SET name=?, gameMode=?, fly=?, onlineTime=?, lastLocation=? WHERE uuid=?")) {
                    update.setString(1, player.getName());
                    update.setString(2, player.getGameMode());
                    update.setString(3, String.valueOf(player.isFly()));
                    update.setString(4, player.getOnlineTime());
                    update.setString(5, player.getLastLocation());
                    update.setString(6, data.getUniqueId().toString());

                    dataHashMap.put(data.getUniqueId().toString(), player);
                    players.add(player);

                    update.executeUpdate();
                    update.close();
                    connection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                DDGPlayerData.getInstance().log("DataPlayer-save", throwables);
            }
        }
    }

    @Override
    public CompletableFuture<PlayerData> saveAll() {
        return CompletableFuture.supplyAsync(() -> {
            for (PlayerData player : players) {
                try (Connection connection = factory.getConnection()) {
                    try (PreparedStatement update = connection.prepareStatement("UPDATE " + factory.getPrefix() + "players SET name=?, gameMode=?, fly=?, onlineTime=?, lastLocation=? WHERE uuid=?")) {
                        update.setString(1, player.getName());
                        update.setString(2, player.getGameMode());
                        update.setString(3, String.valueOf(player.isFly()));
                        update.setString(4, player.getOnlineTime());
                        update.setString(5, player.getLastLocation());
                        update.setString(6, player.getUuid().toString());

                        update.executeUpdate();
                        update.close();
                        connection.close();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    DDGPlayerData.getInstance().log("DataPlayer-saveAll", throwables);
                }
            }
            return null;
        });
    }

    public static Map<String, PlayerData> getDataHashMap() {
        return dataHashMap;
    }

    public CompletableFuture<PlayerData> getPlayerData(String uuid) {
        return CompletableFuture.supplyAsync(() -> dataHashMap.get(uuid));
    }

    @Override
    public CompletableFuture<PlayerData> load(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            if (playerExists(player.getUniqueId().toString())) {
                try (Connection connection = factory.getConnection()) {
                    try (PreparedStatement select = connection.prepareStatement("SELECT * FROM " + factory.getPrefix() + "players WHERE uuid=?")) {
                        select.setString(1, player.getUniqueId().toString());
                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                PlayerData playerData = new PlayerData(player.getUniqueId(), player.getName(), rs.getString("gameMode"), rs.getString("lastLocation"), Boolean.parseBoolean(rs.getString("fly")), rs.getString("onlineTime"));

                                dataHashMap.put(player.getUniqueId().toString(), playerData);
                                players.add(dataHashMap.get(player.getUniqueId().toString()));
                                playerData.setPlayer(player);
                            }
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    DDGPlayerData.getInstance().log("DataPlayer-load", throwables);
                }
            } else {
                String INSERT_QUERY = "INSERT INTO " + factory.getPrefix() + "players" +
                        "(uuid, " +
                        "name, " +
                        "gameMode, " +
                        "fly, " +
                        "onlineTime, " +
                        "lastLocation) " +
                        " VALUES " +
                        "('" + player.getUniqueId().toString() + "', " +
                        "'" + player.getName() + "', " +
                        "'" + player.getGameMode() + "', " +
                        "'false', " +
                        "'0', " +
                        "'Geen')";

                try (Connection connection = factory.getConnection()) {
                    try (PreparedStatement insert = connection.prepareStatement(INSERT_QUERY)) {
                        insert.execute();
                        insert.close();
                        connection.close();
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    DDGPlayerData.getInstance().log("DataPlayer-load", throwables);
                }
                load(player);
            }
            return dataHashMap.get(player.getUniqueId().toString());
        });
    }

    @Override
    public boolean playerExists(String uuid) {
        try (Connection connection = factory.getConnection()) {
            try (PreparedStatement select = connection.prepareStatement("SELECT * FROM " + factory.getPrefix() + "players WHERE uuid=?")) {
                select.setString(1, uuid);
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            DDGPlayerData.getInstance().log("DataPlayer-playerExists", throwables);
        }
        return false;
    }
}
