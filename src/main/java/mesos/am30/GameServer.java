package mesos.am30;

import mesos.am30.GameModel.*;

import java.util.*;

public class GameServer implements IF_MVC {
    private static Board board;

    public GameServer() {}

    @Override
    public List<Tile> getTiles() {
        return board.getUsedTiles();
    }

    @Override
    public Set<Card> getUpperRow() {
        return board.getUpperRow();
    }

    @Override
    public Set<BuildingCard> getUpperBuildings() {
        return board.getUpperBuildings();
    }

    @Override
    public Set<Card> getLowerRow() {
        return board.getLowerRow();
    }

    @Override
    public Set<BuildingCard> getLowerBuildings() {
        return board.getLowerBuildings();
    }

    @Override
    public Map<Parameter, Card> getPlayerCards(Player player) {}

    @Override
    public List<Player> getPlayersOrder() {
        return  board.getPlayersOrder();
    }

    @Override
    public List<Optional<Integer>> getMove(Tile tile) {}

    @Override
    public Map<Parameter, Integer> getParameters(Player player) {
        return player.getParameters();
    }

    @Override
    public void start(int playersNumber) {}

    @Override
    public void pickTile(Player player, Tile tile) {}

    @Override
    public void pickCard(Player player, Card card) {}

    @Override
    public void handleBuildings(Player player) {}

    @Override
    public void nextRound() {}

    @Override
    public void endGame() {}

    private void handleEvent(EventCard event, List<Player> players) {}
    private void resetLowerRow() {}
    private void moveDown() {}
    private void resetUpperRow() {}
    private void nextEra() {}
}