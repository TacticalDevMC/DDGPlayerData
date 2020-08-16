package nl.tacticaldev.ddgplayerdata;

import lombok.Getter;
import nl.tacticaldev.ddgplayerdata.commands.*;
import nl.tacticaldev.ddgplayerdata.configuration.interfaces.IConf;
import nl.tacticaldev.ddgplayerdata.data.DataPlayer;
import nl.tacticaldev.ddgplayerdata.data.PlayerData;
import nl.tacticaldev.ddgplayerdata.interfaces.DataHandler;
import nl.tacticaldev.ddgplayerdata.listeners.GameModeListener;
import nl.tacticaldev.ddgplayerdata.listeners.PlayerConnectionListener;
import nl.tacticaldev.ddgplayerdata.listeners.ToggleFlyListener;
import nl.tacticaldev.ddgplayerdata.settings.Settings;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;
import nl.tacticaldev.ddgplayerdata.settings.storage.MongoFactory;
import nl.tacticaldev.ddgplayerdata.settings.storage.SQLFactory;
import nl.tacticaldev.ddgplayerdata.utils.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public final class DDGPlayerData extends JavaPlugin {

    @Getter
    private static DDGPlayerData instance;

    private static Logger LOGGER = Logger.getLogger("[DDGPlayerData]");

    private List<IConf> confList;

    @Getter
    private ISettings settings;
    @Getter
    private static MongoFactory mongoFactory;
    @Getter
    private static SQLFactory sqlFactory;
    @Getter
    private static DataHandler dataHandler;

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    public void log(String prefix, Exception ex) {
        System.out.println("[DDGPlayerData] " + prefix);
        LOGGER.log(Level.WARNING, "Discovered DDGPlayerData Exception!");
        LOGGER.log(Level.WARNING, "---------------------------");
        LOGGER.log(Level.WARNING, "Exception: " + ex.toString());
        for (StackTraceElement s : ex.getStackTrace()) {
            LOGGER.log(Level.WARNING, s.getClassName() + " [" + s.getLineNumber() + "/" + s.getMethodName()
                    + "] [" + s.getFileName() + "]");
        }
        LOGGER.log(Level.WARNING, "---------------------------");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        long start = System.currentTimeMillis();

        // Settings
        this.confList = new ArrayList<>();
        this.settings = new Settings(this);
        confList.add(settings);

        // Factory
        if (settings.isMongo()) {
            try {
                mongoFactory = new MongoFactory(settings.getConfig().getString("mongo.uri"));
                getLogger().severe("MongoFactory loaded and logged in!");
            } catch (Exception e) {
                getLogger().severe("MongoFactory failed to logged in!");
                log("MongoFactory-Fail", e);
            }
        } else if (settings.isMysql()) {
            try {
                sqlFactory = new SQLFactory();
                sqlFactory.createPlayersTable().get(1L, TimeUnit.SECONDS);
                dataHandler = new DataPlayer();
                getLogger().severe("SQLFactory loaded and logged in!");
            } catch (Exception e) {
                getLogger().severe("SQLFactory failed to logged in!");
                log("SQLFactory-Fail", e);
            }
        }

        // Commands
        getCommand("online").setExecutor(new OnlineTimeCommand());
        getCommand("lastlocation").setExecutor(new LastLocationCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("convert").setExecutor(new ConvertCommand());
        getCommand("gm").setExecutor(new GameModeCommand());

        // Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ToggleFlyListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameModeListener(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            dataHandler.load(player);
        }

        if (settings.isAutoSaving()) {
            getLogger().severe("AutoSaving enabled..");

            Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                @Override
                public void run() {
                    getLogger().severe("Saving all..");
                    dataHandler.saveAll();
                    getLogger().severe("Saving all complete!");
                }
            }, 10 * 20 * 60, 10);
        }

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    long ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

                    PlayerData playerData = dataHandler.getUserByPlayer(player);

                    playerData.setOnlineTime(DateUtil.formatTimeMillis(ticks * 50L));
                    dataHandler.save(player);
                }
            }
        }, 10, 60);

        getLogger().severe("Plugin enabled! Took " + (start - System.currentTimeMillis()) + "ms!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        long start = System.currentTimeMillis();

        if (settings.isMongo()) {
            mongoFactory.closeConnection();
            getLogger().severe("MongoFactory unloaded and logged out!");
        } else if (settings.isMysql()) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (dataHandler.getUserByPlayer(all) != null) dataHandler.save(all);
            }
            sqlFactory.closeConnection();
            getLogger().severe("SQLFactory unloaded and logged out!");
        }

        getLogger().severe("Plugin disabled! Took " + (start - System.currentTimeMillis()) + "ms!");
    }

    public void addReloadListener(IConf listener) {
        listener.reloadConfig();
    }

    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
