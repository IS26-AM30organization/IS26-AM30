package mesos.am30.GameModel;

import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.util.List;

public interface IF_GameModel {
    List<Tile> getTiles();
    List<Card> getUpperRow();
    List<BuildingCard> getUpperBuildings();
    List<Card> getLowerRow();
    List<BuildingCard> getLowerBuildings();
    List<Player> getPlayersOrder();

    /**
     * Saves player in chosen tile and removes player from playersOrder, if the tile wasn't alreadt picked.
     * If the tile was already picked, nothing happens.
     */
    void pickTile(Player player, Tile tile);

    /**
     * If the card is on the table, removes the card from the table and adds the card to the player tribe or buildings.
     * If the card isn't on the table, nothing happens.
     */
    void pickCard(Player player, CharacterCard card);

    /**
     * If the card is on the table, removes the card from the table and adds the card to the player tribe or buildings.
     * If the card isn't on the table, nothing happens.
     */
    void pickCard(Player player, BuildingCard card);

    /**
     * it loads the decks and choose the buildings
     */
    void prepare() throws IOException;

    /**
     * it draws the rows for the first round
     */
    void start();

    /**
     * it handles EVERYTHING to change round
     * @return true if nextEra
     */
    boolean nextRound();

    /**
     * it returns currentPlayer for Controller checks
     */
    Player getCurrentPlayer();

    /**
     * forces the model to update currentPlayer to the following one
     */
    void endPlayerTurn();

    /**
     * to obtain requestedPlayer's virtualView to send errorMessages directly from the Controller
     * @return if !=null used to send errorMessages
     */
    IF_GameView getPlayerView(Player requestingPlayer);
}
