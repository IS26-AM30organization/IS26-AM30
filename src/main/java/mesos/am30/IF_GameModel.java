package mesos.am30;

import mesos.am30.GameModel.*;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Optional;

public interface IF_GameModel {
    List<Tile> getTiles();
    List<Card> getUpperRow();
    List<BuildingCard> getUpperBuildings();
    List<Card> getLowerRow();
    List<BuildingCard> getLowerBuildings();
    //Map<Parameter, List<CharacterCard>> getPlayerCards(Player player);
    //Set<BuildingCard> getPlayerBuildings(Player player);
    List<Player> getPlayersOrder();
    //List<Optional<Integer>> getMove(Tile tile);
    //Map<Parameter, Integer> getParameters(Player player);

    /*
    /**
     * Returns null
     */
    //void start(int playersNumber);


    /**
     * Saves player in chosen tile and removes player from playersOrder, if the tile wasn't alreadt picked.
     * If the tile was already picked, nothing happens.
     */
    void pickTile(Player player, Tile tile);

    /**
     * If the card is on the table, removes the card from the table and adds the card to the player tribe or buildings.
     * If the card isn't on the table, nothing happens.
     */
    void pickCard(Player player, Card card);

    /*
    /**
     * Handles all recurring events in the player buildings.
     */
    //void handleBuildings(Player player);

    boolean nextRound();
    //oid endGame();
}
