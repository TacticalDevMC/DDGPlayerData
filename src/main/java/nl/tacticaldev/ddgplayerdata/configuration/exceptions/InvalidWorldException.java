package nl.tacticaldev.ddgplayerdata.configuration.exceptions;

public class InvalidWorldException extends Exception {

    private final String world;

    public InvalidWorldException(final String world) {
        super("Invalid world!");
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }
}