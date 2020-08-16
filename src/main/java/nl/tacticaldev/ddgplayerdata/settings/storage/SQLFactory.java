package nl.tacticaldev.ddgplayerdata.settings.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.zaxxer.hikari.HikariDataSource;
import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.configuration.Config;
import nl.tacticaldev.ddgplayerdata.utils.NumberUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class SQLFactory {

    private static HikariDataSource hikari;

    private static Config config = DDGPlayerData.getInstance().getSettings().getConfig();

    public static HikariDataSource getHikari() {
        if (hikari == null) {
            hikari = new HikariDataSource();
        }

        return hikari;
    }

    public String getPrefix() {
        return config.getString("mysql.table_prefix");
    }

    public SQLFactory() {
        ConfigurationSection section = config.getConfigurationSection("mysql");

        String host = section.getString("host");
        String user = section.getString("user");
        String password = section.getString("password");
        String databaseName = section.getString("databaseName");
        int port = section.getInt("port");

        checkForEmpty(host, user, password, databaseName);

        getHikari().setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        getHikari().addDataSourceProperty("serverName", host);
        getHikari().addDataSourceProperty("user", user);
        getHikari().addDataSourceProperty("password", password);
        getHikari().addDataSourceProperty("databaseName", databaseName);
        getHikari().addDataSourceProperty("port", port);

        getHikari().setPoolName("DDGPlayerData pool");
    }

    private void checkForEmpty(String... key) {
        Arrays.asList(key).forEach(value -> {
            if (value.equals("")) {
                DDGPlayerData.getInstance().getLogger().severe(value + " must been valid.");
            }
        });
    }

    public void closeConnection() {
        if (hikari != null) {
            hikari.close();
        }
    }

    public Connection getConnection() {
        try {
            return getHikari().getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + getPrefix() + "players " +
            "(uuid varchar(255), " +
            "name varchar(255), " +
            "gameMode varchar(255), " +
            "fly varchar(255), " +
            "onlineTime varchar(255), " +
            "lastLocation varchar(255), " +
            "PRIMARY KEY(uuid));";

    public CompletableFuture<SQLFactory> createPlayersTable() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                try (PreparedStatement insert = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                    insert.execute();
                    insert.close();
                    connection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return this;
        });
    }

}
