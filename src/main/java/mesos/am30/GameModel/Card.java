package mesos.am30.GameModel;

public abstract class Card {
    private final int era;

    public Card(int era) {
        this.era = era;
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
