package nl.tacticaldev.ddgplayerdata.settings.interfaces;

import nl.tacticaldev.ddgplayerdata.configuration.Config;
import nl.tacticaldev.ddgplayerdata.configuration.interfaces.IConf;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public interface ISettings extends IConf {

    Config getConfig();

    String getPrefix();

    boolean useMysql();

    boolean isMongo();

    boolean isMysql();

    boolean isAutoSaving();

}
