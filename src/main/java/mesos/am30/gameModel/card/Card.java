package mesos.am30.gameModel.card;

import mesos.am30.gameModel.board.Board;

import java.io.Serializable;
import java.util.Objects;

public abstract class Card implements Serializable {
    private final int era;
    final int id;

    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return era == card.era && id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(era, id);
    }

    public int getId(){
        int num = id;
        return num;
    }


    public int getEra() {
        return this.era;
    }

    public boolean isPickable() {
        return false;
    }

    public void drawUp(Board board){}

    public void drawDown(Board board){}

    public void discard (Board board){}

    public void reorder (Board board){}

    public void displayCard() {}

    public void createRow(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {}
}
