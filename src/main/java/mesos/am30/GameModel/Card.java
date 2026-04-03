package mesos.am30.GameModel;

public abstract class Card {
    private final int era;

    public Card(int era) {
        this.era = era;
    }

    public int getEra() {
        return this.era;
    }
}
