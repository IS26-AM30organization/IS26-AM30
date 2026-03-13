package mesos.am30.GameModel;

import java.util.Optional;

public class Tile {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;

    public Tile(int playersMinimum, Integer upArrows, Integer downArrows, Integer food) {}

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Optional<Integer> getUpArrows() {}

    public Optional<Integer> getDownArrows() {}

    public Optional<Integer> getFood() {}

}
