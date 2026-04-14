package mesos.am30.server;

import mesos.am30.GameModel.BuildingCard;
import mesos.am30.GameModel.CharacterCard;
import mesos.am30.GameModel.Tile;

public interface IF_GameController {
    void chooseTile(String nickname, Tile tile);
    void chooseCharacter(String nickname, CharacterCard characterCard);
    void chooseBuilding(String nickname, BuildingCard buildingCard);
}
