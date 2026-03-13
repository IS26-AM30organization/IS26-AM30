package mesos.am30;

import mesos.am30.GameModel.*;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Optional;

public interface IF_MVC {
    List<Tile> getTiles();
    Set<Card> getUpperRow();
    Set<BuildingCard> getUpperBuildings();
    Set<Card> getLowerRow();
    Set<BuildingCard> getLowerBuildings();
    Map<Parameter, Card> getPlayerCards(Player player);
    List<Player> getPlayersOrder();
    List<Optional<Integer>> getMove(Tile tile);
    Map<Parameter, Integer> getParameters(Player player);

    void start(int playersNumber);
    void pickTile(Player player, Tile tile);
    void pickCard(Player player, Card card);
    void handleBuildings(Player player);
    void nextRound();
    void endGame();
}
