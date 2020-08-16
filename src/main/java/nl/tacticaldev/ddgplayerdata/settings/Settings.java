package nl.tacticaldev.ddgplayerdata.settings;

import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.configuration.Config;
import nl.tacticaldev.ddgplayerdata.settings.interfaces.ISettings;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class Settings implements ISettings {

    private DDGPlayerData ddgPlayerData;
    private Config config;

    public Settings(DDGPlayerData plugin) {
        this.ddgPlayerData = plugin;
        this.config = new Config("config");
        reloadConfig();
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public String getPrefix() {
        return ddgPlayerData.color(config.getString("prefix"));
    }

    @Override
    public boolean useMysql() {
        return config.getBoolean("using-mysql");
    }

    @Override
    public boolean isMongo() {
        return !useMysql();
    }

    @Override
    public boolean isMysql() {
        return useMysql();
    }

    @Override
    public boolean isAutoSaving() {
        return config.getBoolean("auto-saving");
    }

    @Override
    public void reloadConfig() {
        config.reload();
    }

    @Override
    public String getName() {
        return config.getFile().getName();
    }

    @Override
    public String filePath() {
        return config.getFile().getAbsolutePath();
    }
}
