package mesos.am30.server;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;

import java.io.IOException;
import java.rmi.Remote;

public interface IF_GameController extends Remote {
    void chooseTile(String nickname, Tile tile) throws IOException;
    void chooseCharacter(String nickname, CharacterCard characterCard) throws IOException;
    void chooseBuilding(String nickname, BuildingCard buildingCard) throws IOException;
}
