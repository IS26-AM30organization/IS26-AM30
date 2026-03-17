package mesos.am30.GameModel;

import java.util.Optional;

public class Tile {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;
    private final int playersMinimum;

    public Tile(int playersMinimum, Integer upArrows, Integer downArrows, Integer food) {
        this.playersMinimum = playersMinimum;
        this.upArrows = upArrows;
        this.downArrows = downArrows;
        this.food = food;
    }

    public Optional<Player> getCurrentPlayer() {
        return Optional.ofNullable(this.currentPlayer);
    }

    public int getPlayersMinimum() { return playersMinimum; }

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
