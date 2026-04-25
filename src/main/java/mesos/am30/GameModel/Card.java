package mesos.am30.GameModel;

import java.io.Serializable;

public abstract class Card implements Serializable {
    private final int era;
    final int id;

    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.getId();
    }

    public int getId(){
        int num = id;
        return num;
    }

    public int getEra() {
        return this.era;
    }

    public boolean isPickacble() {
        return false;
    }

    protected void drawUp(Board board){}

    protected void drawDown(Board board){}

    protected void discard (Board board){}

    protected void reorder (Board board){}
}
