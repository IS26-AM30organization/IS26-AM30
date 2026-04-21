package mesos.am30.server;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.CharacterCard;
import mesos.am30.GameModel.Tile;

import java.io.IOException;

public interface IF_GameController {
    void chooseTile(String nickname, Tile tile) throws IOException;
    void chooseCharacter(String nickname, CharacterCard characterCard) throws IOException;
    void chooseBuilding(String nickname, BuildingCard buildingCard) throws IOException;
}
