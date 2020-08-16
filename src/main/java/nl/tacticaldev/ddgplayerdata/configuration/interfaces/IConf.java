package nl.tacticaldev.ddgplayerdata.configuration.interfaces;

public interface IConf {

    void reloadConfig();

    String getName();

    String filePath();

}
