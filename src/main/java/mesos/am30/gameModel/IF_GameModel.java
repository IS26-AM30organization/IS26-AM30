package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.Move;

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
     *
     * @return
     */
    void pickTile(Player player, Tile tile) throws IOException;

    /**
     * If the card is on the table, removes the card from the table and adds the card to the player tribe or buildings.
     * If the card isn't on the table, nothing happens.
     */
    boolean pickCard(Player player, CharacterCard card) throws IOException;

    /**
     * If the card is on the table, removes the card from the table and adds the card to the player tribe or buildings.
     * If the card isn't on the table, nothing happens.
     *
     * @return
     */
    boolean pickCard(Player player, BuildingCard card) throws IOException;

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
    boolean nextRound() throws IOException;

    /**
     * it returns currentPlayer for Controller checks
     */
    Player getCurrentPlayer();
    /**
     * it returns currentMove for Controller checks
     */
    Move getCurrentMove();
}
