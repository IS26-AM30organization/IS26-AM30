package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.Move;

import java.io.IOException;
import java.util.List;

/**
 * Model's Interface for ModelViewController pattern.
 * <br>Interface for the methods that the Controller calls directly on the Model in order to update the game status.
 */
public interface IF_GameModel {

    /**
     * Getter for the "Tiles".
     *
     * @return List of all the used Tiles.
     */
    List<Tile> getTiles();

    /**
     * Getter for the "upperRow".
     *
     * @return Upper row of Character/Event Cards.
     */
    List<Card> getUpperRow();

    /**
     * Getter for the "upperBuildings".
     *
     * @return Upper row of Building Cards.
     */
    List<BuildingCard> getUpperBuildings();

    /**
     * Getter for the "lowerRow".
     *
     * @return Lower row of Character/Event Cards.
     */
    List<Card> getLowerRow();

    /**
     * Getter for the "lowerBuildings".
     *
     * @return Lower row of Building Cards.
     */
    List<BuildingCard> getLowerBuildings();

    /**
     * Getter for the "playersOrder".
     *
     * @return List of Players in the correct move order.
     */
    List<Player> getPlayersOrder();

    /**
     * Getter for the "currentPlayer".
     *
     * @return Player who has to move.
     */
    Player getCurrentPlayer();

    /**
     * Getter for the "currentMove".
     *
     * @return Next Move to perform.
     */
    Move getCurrentMove();

    /**
     * Move - Pick Tile.
     * <br/>This method updates the Model due to the move "Pick Tile" done from a Player.
     * <br/>If the Tile is already occupied, nothing happens.
     * <br/><strong>Pre:</strong> player != null && tile != null
     *
     * @param player    Player who has made the move.
     * @param tile      Tile picked by the Player.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void pickTile(Player player, Tile tile) throws IOException;

    /**
     * Move - Pick Card (Character Card).
     * <br/>This method updates the Model due to the move "Pick Card" done from a Player, in particular this method works with Character Cards.
     * <br/>If the Card is not displayed on the Board, nothing happens.
     * <br/><strong>Pre:</strong> player != null && card != null
     *
     * @param player    Player who has made the move.
     * @param card      Card picked by the Player.
     *
     * @return True if the Move ended successfully, false otherwise.
     * @throws IOException The connection cannot be established correctly.
     */
    boolean pickCard(Player player, CharacterCard card) throws IOException;

    /**
     * Move - Pick Card (Building Card).
     * <br/>This method updates the Model due to the move "Pick Card" done from a Player, in particular this method works with Building Cards.
     * <br/>If the Card is not displayed on the Board, nothing happens.
     * <br/><strong>Pre:</strong> player != null && card != null
     *
     * @param player    Player who has made the move.
     * @param card      Card picked by the Player.
     *
     * @return True if the Move ended successfully, false otherwise.
     * @throws IOException The connection cannot be established correctly.
     */
    boolean pickCard(Player player, BuildingCard card) throws IOException;

    /**
     * Load the Game.
     * <br/>This method works as a pre-game loader, by loading the various components of the Game (Tiles & Cards) adn preparing the various decks and rows.
     *
     * @throws IOException The resources cannot be load correctly.
     */
    void prepare() throws IOException;

    /**
     * Setup and start the Game.
     * <br/>This method defines the first Game status, by drawing the first rows and starting the first Era of the Game.
     */
    void start();

    /**
     * Start a new Round.
     * <br/>This method starts a new Round, by updating the Players' order and all the rows.
     * <br/>It also checks if a new Era has started.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void nextRound() throws IOException;
}
