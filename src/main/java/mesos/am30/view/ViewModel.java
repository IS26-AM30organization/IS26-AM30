package mesos.am30.view;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.Card;
import mesos.am30.GameModel.Player;
import mesos.am30.GameModel.Tile;
import mesos.am30.common.Move;

import java.util.ArrayList;
import java.util.List;

public class ViewModel {
    private List<Player> players;
    private List<Tile> tiles;
    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;
    private Player currentUser = null;
    private Move currentMove = null;

    public ViewModel() {
        this.players = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.upperBuildings = new ArrayList<>();
        this.lowerBuildings = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public List<Card> getUpperRow() {
        return upperRow;
    }

    public void setUpperRow(List<Card> upperRow) {
        this.upperRow = upperRow;
    }

    public List<BuildingCard> getUpperBuildings() {
        return upperBuildings;
    }

    public void setUpperBuildings(List<BuildingCard> upperBuildings) {
        this.upperBuildings = upperBuildings;
    }

    public List<Card> getLowerRow() {
        return lowerRow;
    }

    public void setLowerRow(List<Card> lowerRow) {
        this.lowerRow = lowerRow;
    }

    public List<BuildingCard> getLowerBuildings() {
        return lowerBuildings;
    }

    public void setLowerBuildings(List<BuildingCard> lowerBuildings) {
        this.lowerBuildings = lowerBuildings;
    }

    public Player getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String nickname) {
        this.currentUser = players.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .toList().getFirst();
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Move currentMove) {
        this.currentMove = currentMove;
    }
}
