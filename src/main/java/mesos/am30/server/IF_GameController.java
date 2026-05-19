package mesos.am30.server;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Controller's Interface for ModelViewController pattern.
 * <br>Interface for the methods that the View calls directly on the Controller in order to communicate its Moves.
 */
public interface IF_GameController extends Remote {

    /**
     * Client's notification about Move CHOOSE_TILE.
     * <br/>This method is called by the Clients in order to notify the Controller about its Move CHOOSE_TILE.
     *
     * @param nickname  Nickname of the Player who has moved.
     * @param tile      Tile chosen by the Player.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void chooseTile(String nickname, Tile tile) throws IOException;

    /**
     * Client's notification about Move CHOOSE_CHARACTER.
     * <br/>This method is called by the Clients in order to notify the Controller about its Move CHOOSE_CHARACTER.
     *
     * @param nickname      Nickname of the Player who has moved.
     * @param characterCard Character Card chosen by the Player.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void chooseCharacter(String nickname, CharacterCard characterCard) throws IOException;

    /**
     * Client's notification about Move CHOOSE_BUILDING.
     * <br/>This method is called by the Clients in order to notify the Controller about its Move CHOOSE_BUILDING.
     *
     * @param nickname      Nickname of the Player who has moved.
     * @param buildingCard  Building Card chosen by the Player.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void chooseBuilding(String nickname, BuildingCard buildingCard) throws IOException;

    /**
     * Client's notification about showing the Rankings.
     * <br/>This method is called by the Clients in order to notify the Controller about their willingness of seeing the Global Rankings.
     *
     * @param nickname  Nickname of the Player who has notified the Controller.
     * @param response  True if the Player wants to see the Rankings, false otherwise.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void showRankings(String nickname, boolean response) throws IOException;
}
