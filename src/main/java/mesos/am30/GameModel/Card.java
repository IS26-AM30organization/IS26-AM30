package mesos.am30.GameModel;

import java.io.Serializable;

public abstract class Card implements Serializable {
    private final int era;

    public Card(int era) {
        this.era = era;
    }

    public int getEra() {
        return this.era;
    }
}
