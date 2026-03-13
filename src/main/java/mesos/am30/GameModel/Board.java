package mesos.am30.GameModel;

import java.util.List;
import java.util.Set;

public class Board {
    private static Set<Card> allCards;
    private final Set<Card> usedCards;
    private static Set<Tile> allTiles;
    private final List<Tile> usedTiles;
    private static Set<EventCard> finalEventCards;
    private final List<List<Card>> decks;
    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;
    private final List<Player> players;
    private final List<Player> playersOrder;

    public Board(List<Player> players) {}

    public Set<Card> getUsedCards() {
        return usedCards;
    }

    public List<Tile> getUsedTiles() {
        return usedTiles;
    }

    public List<List<Card>> getDecks() {
        return decks;
    }

    public List<Card> getUpperRow() {
        return upperRow;
    }

    public List<BuildingCard> getUpperBuildings() {
        return upperBuildings;
    }

    public List<Card> getLowerRow() {
        return lowerRow;
    }

    public List<BuildingCard> getLowerBuildings() {
        return lowerBuildings;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }
}
