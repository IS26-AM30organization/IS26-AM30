package mesos.am30.GameModel;

public abstract class Card {
    private final int era;
    private final int playersMinimum;

    public int getPlayersMinimum() {
        return playersMinimum;
    }

    public Card(int era, int playersMinimum) {
        this.playersMinimum = playersMinimum;
        this.era = era;
    }

    public int getEra() {
        return this.era;
    }
}
