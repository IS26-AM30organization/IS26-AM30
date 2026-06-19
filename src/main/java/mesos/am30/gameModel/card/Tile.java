package mesos.am30.gameModel.card;

import mesos.am30.gameModel.Player;

import java.io.Serializable;

import java.util.Objects;
import java.util.Optional;

/**
 * Representation of a Tile.
 * <br/>This Class works as the representation for a Tile, independently of its type of moves.
 */
public class Tile implements Serializable {
    private Player currentPlayer;
    private final Integer upArrows;
    private final Integer downArrows;
    private final Integer food;

    /**
     * Constructor of a Tile.
     * <br/><strong>Pre:</strong> (upArrows > 0 || upArrows == null) &amp;&amp; (downArrows > 0 || downArrows == null) &amp;&amp; (food > 0 || food == null)
     * <br/><strong>Post:</strong> this.upArrows = upArrows &amp;&amp; this.downArrows = downArrows &amp;&amp; this.food = food
     *
     * @param upArrows      Number of Cards which can be picked from the upper row.
     * @param downArrows    Number of Cards which can be picked from the lower row.
     * @param food          Food which the Player gains.
     */
    public Tile(Integer upArrows, Integer downArrows, Integer food) {
        this.upArrows = upArrows;
        this.downArrows = downArrows;
        this.food = food;
    }

    /**
     * Getter for the attribute "currentPlayer".
     *
     * @return Player positioned on the Tile currently.
     */
    public Optional<Player> getCurrentPlayer() {
        return Optional.ofNullable(this.currentPlayer);
    }

    /**
     * Setter for the attribute "currentPlayer".
     * <br/><strong>Pre:</strong> currentPlayer != null
     *
     * @param currentPlayer Player who has just positioned itself on the Tile.
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Default setter for the attribute "currentPlayer".
     * <br/>This method works a default setter for the attribute "currentPlayer", setting it to null.
     */
    public void clearCurrentPlayer() {this.currentPlayer = null;}

    /**
     * Getter for the attribute "upArrows".
     *
     * @return Number of Cards which can be picked from the upper row.
     */
    public Integer getUpArrows() {
        return (upArrows == null) ? 0 : upArrows;
    }

    /**
     * Getter for the attribute "downArrows".
     *
     * @return Number of Cards which can be picked from the lower row.
     */
    public Integer getDownArrows() {
        return (downArrows == null) ? 0 : downArrows;
    }

    /**
     * Getter for the attribute "food".
     *
     * @return Food which the Player gains.
     */
    public Integer getFood() {
        return food;
    }

    /**
     * Add Tile's info to the StringBuilders for the Terminal.
     * <br/>This method works by adding the Tile's info to the StringBuilders, in order to display it properly on the Terminal.
     * <br/><strong>Pre:</strong> ln1 != null &amp;&amp; ln2 != null &amp;&amp; ln3 != null
     *
     * @param ln1 Line containing the number of Moves (Pick From Up) of the Tile.
     * @param ln2 Line containing the number of Moves (Pick From Down) of the Tile.
     * @param ln3 Line containing the Player currently positioned on the Tile.
     */
    public void createRow(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3) {
        String r = "";
        if (food != null) r = "\033[31m" + "Up: " + "\033[0m" + food;
        if (upArrows != null) r = "\033[32m" + "Up: " + "\033[0m" + upArrows;
        String i = "";
        if (downArrows != null) i = "\033[31m" + "Down: " + "\033[0m" + downArrows;
        String pp = "";
        if (currentPlayer != null) pp = "\033[33m" + "Player: " + "\033[0m" + currentPlayer.getNickname();

        //Ansi characters are a problem for length size, must be ignored
        String ansiRegex = "\u001B\\[[;\\d]*m";

        int real1 = r.replaceAll(ansiRegex, "").length();
        int real2 = i.replaceAll(ansiRegex, "").length();
        int real3 = pp.replaceAll(ansiRegex, "").length();

        int maxWidth = Math.max(real1, Math.max(real2, real3)) + 3;

        ln1.append(r);
        ln1.repeat(" ", Math.max(0, maxWidth + 2 - real1));

        ln2.append(i);
        ln2.repeat(" ", Math.max(0, maxWidth + 2 - real2));

        ln3.append(pp);
        ln3.repeat(" ", Math.max(0, maxWidth + 2 - real3));
    }

    // Object Methods Override

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(currentPlayer, tile.currentPlayer) && Objects.equals(upArrows, tile.upArrows) && Objects.equals(downArrows, tile.downArrows) && Objects.equals(food, tile.food);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPlayer, upArrows, downArrows, food);
    }

}
