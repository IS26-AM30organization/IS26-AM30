package mesos.am30.GameModel;

import java.io.Serializable;
import java.util.Optional;

public class Tile implements Serializable {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;

    public Tile(Integer upArrows, Integer downArrows, Integer food) {
        this.upArrows = upArrows;
        this.downArrows = downArrows;
        this.food = food;
    }

    public Optional<Player> getCurrentPlayer() {
        return Optional.ofNullable(this.currentPlayer);
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void clearCurrentPlayer() {this.currentPlayer = null;}

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
