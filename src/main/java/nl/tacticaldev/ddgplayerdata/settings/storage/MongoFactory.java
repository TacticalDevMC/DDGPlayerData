package nl.tacticaldev.ddgplayerdata.settings.storage;

import com.mongodb.*;
import lombok.SneakyThrows;
import nl.tacticaldev.ddgplayerdata.DDGPlayerData;
import nl.tacticaldev.ddgplayerdata.configuration.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @AUTHOR: TacticalDev
 * Copyright © 2020, Joran Huibers, All rights reserved.
 */

public class MongoFactory {

    private static MongoClient mongo = null;
    private static DB database = null;
    private static DBCollection collection;

    private static Config config = DDGPlayerData.getInstance().getSettings().getConfig();

    public static MongoClient getMongo() {
        if (mongo == null) {
            mongo = new MongoClient(new MongoClientURI(config.getString("mongo.uri")));
        }
        return mongo;
    }

    public MongoFactory(String uri) {
        if (uri.equals("")) {
            DDGPlayerData.getInstance().getLogger().severe("URI must been valid.");
            return;
        }

        mongo = new MongoClient(new MongoClientURI(uri));
    }

    public static DBCollection getCollection(String c) {
        if (getDatabase() != null) {
            collection = getDatabase().getCollection(config.getString("mongo.collections." + c));
        }
        return collection;
    }

    // mongodb+srv://TacticalDev:jorande2de@ddgplayerdata.qysaf.azure.mongodb.net/test

    public static DB getDatabase() {
        if (database == null) {
            database = getMongo().getDB(config.getString("mongo.databaseName"));
        }
        return database;
    }

    public static BasicDBObject getPlayerObject(String playerName) {
        DBCollection collection = getCollection("playerData");
        DBObject playerObject = collection.findOne(new BasicDBObject("playerName", playerName));
        BasicDBObject object = (BasicDBObject) collection.find(playerObject).one();
        return object;
    }

    public static BasicDBObject getPlayerSettingsObject(String playerName) {
        DBCollection collection = getCollection("settings");
        DBObject playerObject = collection.findOne(new BasicDBObject("playerName", playerName));
        BasicDBObject object = (BasicDBObject) collection.find(playerObject).one();
        return object;
    }

    public void closeConnection() {
        if (mongo != null) {
            mongo.close();
        }
    }


}
