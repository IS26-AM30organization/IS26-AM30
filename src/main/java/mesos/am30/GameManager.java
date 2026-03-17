package mesos.am30;

import mesos.am30.GameModel.*;

import java.util.*;

public class GameManager implements IF_MVC {
    private static Board board;

    public GameManager() {}

    @Override
    public List<Tile> getTiles() {
        return board.getUsedTiles();
    }

    @Override
    public Set<Card> getUpperRow() { return new HashSet<>(board.getUpperRow()); }

    @Override
    public Set<BuildingCard> getUpperBuildings() {
        return new HashSet<>(board.getUpperBuildings());
    }

    @Override
    public Set<Card> getLowerRow() {
        return new HashSet<>(board.getLowerRow());
    }

    @Override
    public Set<BuildingCard> getLowerBuildings() {
        return new HashSet<>(board.getLowerBuildings());
    }

    //si interfaccia direttamente con player, non con board:
    @Override
    public Map<Parameter, List<CharacterCard>> getPlayerCards(Player player) { return player.getTribe(); }

    //si interfaccia direttamente con player, non con board:
    @Override
    public Set<BuildingCard> getPlayerBuildings(Player player){ return player.getBuildings(); }

    @Override
    public List<Player> getPlayersOrder() {
        return  board.getPlayersOrder();
    }

    @Override
    public List<Optional<Integer>> getMove(Tile tile) { return null; }

    @Override
    public Map<Parameter, Integer> getParameters(Player player) {
        return player.getParameters();
    }

    @Override
    public void start(int playersNumber) {}

    @Override
    public void pickTile(Player player, Tile tile) { board.pickTile(player, tile); }

    @Override
    public void pickCard(Player player, Card card) { board.pickCard(player, card); }

    //si interfaccia direttamente con player, non con board:
    @Override
    public void handleBuildings(Player player) {
        for(BuildingCard b : player.getBuildings()) {
            if (b.getEventType()==EventType.ROUND) {
                b.getEvent().handleEvent(player);
            }
        }
    }

    @Override
    public void nextRound() { board.nextRound(); }

    @Override
    public void endGame() {
        //DA IMPLEMENTARE
    }

    private void handleEvent(EventCard event, List<Player> players) {
        for (Player player : players) {
            event.getEvent().handleEvent(player);
        }
    }
    private void discardLowerRow() { board.discardLowerRow(); }
    private void moveDown() { board.moveDown(); }
    //private void resetUpperRow() {} RIMOSSA
    private void nextEra() {  board.nextEra(); }
}