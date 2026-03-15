package mesos.am30.GameModel;

import java.util.Optional;

public class Tile {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;

    public Tile(Player currentPlayer, Integer upArrows, Integer downArrows, Integer food) {
        this.currentPlayer = currentPlayer;
        this.upArrows = upArrows;
        this.downArrows = downArrows;
        this.food = food;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Integer getUpArrows() {
        return upArrows;
    }

    public Integer getDownArrows() {
        return downArrows;
    }

    public Integer getFood() {
        return food;
    }
}
