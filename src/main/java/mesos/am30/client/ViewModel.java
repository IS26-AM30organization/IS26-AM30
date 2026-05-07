package mesos.am30.client;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.Move;

import java.util.List;
import java.util.ArrayList;

/**
 * Representation Client-side of the Model.
 * <br>This Class represents the Model Client-side: it is a simplified version formed by different collections of objects representing the Game current State.
 * <br>It is used both for move validity checks by the VirtualView Class, both by the user interface for drawing the Game current State.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class ViewModel {
    private List<Player> players;
    private List<Tile> tiles;
    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;
    private Player currentUser = null;
    private Move currentMove = null;

    /**
     * Constructor for ViewModel.
     */
    public ViewModel() {
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.upperBuildings = new ArrayList<>();
        this.lowerBuildings = new ArrayList<>();
    }

    /**
     * Getter for the attribute "players".
     *
     * @return List of all Players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Setter for the attribute "players".
     * <br><strong>Pre:</strong> players != null
     * <br><strong>Post:</strong> this.players == players
     *
     * @param players Updated players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Getter for the attribute "tiles".
     *
     * @return List of all Tiles
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Setter for the attribute "tiles".
     * <br><strong>Pre:</strong> tiles != null
     * <br><strong>Post:</strong> this.tiles == tiles
     *
     * @param tiles Updated tiles
     */
    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * Getter for the attribute "upperRow".
     *
     * @return List of all Cards in the upper row
     */
    public List<Card> getUpperRow() {
        return upperRow;
    }

    /**
     * Setter for the attribute "upperRow".
     * <br><strong>Pre:</strong> upperRow != null
     * <br><strong>Post:</strong> this.upperRow == upperRow
     *
     * @param upperRow Updated upper row
     */
    public void setUpperRow(List<Card> upperRow) {
        this.upperRow = upperRow;
    }

    /**
     * Getter for the attribute "upperBuildings".
     *
     * @return List of all Cards in the upper buildings row
     */
    public List<BuildingCard> getUpperBuildings() {
        return upperBuildings;
    }

    /**
     * Setter for the attribute "upperBuildings".
     * <br><strong>Pre:</strong> upperBuildings != null
     * <br><strong>Post:</strong> this.upperBuildings == upperBuildings
     *
     * @param upperBuildings Updated upper buildings row
     */
    public void setUpperBuildings(List<BuildingCard> upperBuildings) {
        this.upperBuildings = upperBuildings;
    }

    /**
     * Getter for the attribute "lowerRow".
     *
     * @return List of all Cards in the lower row
     */
    public List<Card> getLowerRow() {
        return lowerRow;
    }

    /**
     * Setter for the attribute "lowerRow".
     * <br><strong>Pre:</strong> lowerRow != null
     * <br><strong>Post:</strong> this.lowerRow == lowerRow
     *
     * @param lowerRow Updated lower row
     */
    public void setLowerRow(List<Card> lowerRow) {
        this.lowerRow = lowerRow;
    }

    /**
     * Getter for the attribute "lowerBuildings".
     *
     * @return List of all Cards in the lower buildings row
     */
    public List<BuildingCard> getLowerBuildings() {
        return lowerBuildings;
    }

    /**
     * Setter for the attribute "lowerBuildings".
     * <br><strong>Pre:</strong> lowerBuildings != null
     * <br><strong>Post:</strong> this.lowerBuildings == lowerBuildings
     *
     * @param lowerBuildings Updated lower buildings row
     */
    public void setLowerBuildings(List<BuildingCard> lowerBuildings) {
        this.lowerBuildings = lowerBuildings;
    }

    /**
     * Getter for the attribute "currentUser".
     *
     * @return Player that has to move
     */
    public Player getCurrentUser() {
        return currentUser;
    }

    /**
     * Setter for the attribute "currentUser".
     * <br><strong>Pre:</strong> nickname != null
     * <br><strong>Post:</strong> this.currentUser == (* Player p having p.nickname == nickname *)
     *
     * @param nickname Nickname of the Player that has to move
     */
    public void setCurrentUser(String nickname) {
        this.currentUser = players.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .toList().getFirst();
    }

    /**
     * Getter for the attribute "currentMove".
     *
     * @return Move to perform
     */
    public Move getCurrentMove() {
        return currentMove;
    }

    /**
     * Setter for the attribute "currentMove".
     * <br><strong>Pre:</strong> currentMove != null
     * <br><strong>Post:</strong> this.currentMove == currentMove
     *
     * @param currentMove Move to perform
     */
    public void setCurrentMove(Move currentMove) {
        this.currentMove = currentMove;
    }

    /**
     * Default Setter.
     * <br>This method sets to default value (null) both currentUser and currentMove, assuring that the Turn is over.
     * <br><strong>Post:</strong> this.currentUser == null && this.currentMove == null
     */
    public void setDefault() {
        this.currentUser = null;
        this.currentMove = null;
    }
}
