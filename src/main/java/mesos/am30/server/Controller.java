package mesos.am30.server;

import mesos.am30.GameModel.*;
import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class Controller implements IF_GameController {
    private final int playersNumber;
    private final Map<Player, IF_GameView> clients;

    public Controller(int playersNumber) {
        this.playersNumber = playersNumber;
        this.clients = new HashMap<>(playersNumber);
    }

    // Test getter for the attribute playersNumber
    int getPlayersNumber() {
        return playersNumber;
    }

    // Test getter for the attribute clients
    Map<Player, IF_GameView> getClients() {
        return clients;
    }

    public boolean isFull() {
        return clients.size() == playersNumber;
    }

    public boolean connect(IF_GameView view, String nickname) throws IOException {
        clients.put(new Player(nickname), view);
        view.setController(this);
        return clients.size() == playersNumber;
    }

    public void startGame() {
        //
    }

    @Override
    public void chooseTile(String nickname, Tile tile) {
        //
    }

    @Override
    public void chooseCharacter(String nickname, CharacterCard characterCard) {
        //
    }

    @Override
    public void chooseBuilding(String nickname, BuildingCard buildingCard) {
        //
    }
}
