package mesos.am30.client;

import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.server.IF_GameController;

import java.io.IOException;
import java.rmi.Remote;

import java.util.List;
import java.util.Map;

/**
 * Views's Interface for ModelViewController pattern.
 * <br>Interface for the methods that the Server calls directly on the View in order to notify a request/Error/update.
 */
public interface IF_GameView extends Remote {

    /**
     * Notify the Client about the successful connection to the Server.
     *
     * @throws IOException The connection cannot be established.
     */
    void confirmConnection() throws IOException;

    /**
     * Show the Client the available Lobbies.
     *
     * @throws IOException The connection cannot be established.
     */
    void showLobbies(Map<String, Integer> availableLobbies) throws IOException;

    /**
     * Ask the Client for its nickname.
     * <br>This method asks the Client which nickname he wants to use.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void askNickname() throws IOException;

    /**
     * Notify the Client about the successfully join to the Lobby.
     *
     * @throws IOException The connection cannot be established.
     */
    void confirmLobbyJoined() throws IOException;

    /**
     * Set the controller.
     * <br>This method sets the Controller with which the View is communicating following the ModelViewController Pattern.
     *
     * @param controller Controller calling the method.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void setController(IF_GameController controller) throws IOException;

    /**
     * Notify the View about the Turn change.
     * <br>This method is called in order to notify the View about the turn change, telling who has to move and what he has to do.
     * <br><strong>Pre:</strong> nickname != null && move != null
     *
     * @param nickname Nickname of the Player who has to mov.
     * @param move Next move to perform.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void notifyTurn(String nickname, Move move) throws IOException;

    /**
     * Notify the View about an Error.
     * <br>This method is called in order to notify the View about an Error it has made.
     * <br><strong>Pre:</strong> errorType != null
     *
     * @param errorType Error occurred due to a wrong action.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void notifyError(ErrorType errorType) throws IOException;

    /**
     * Update the View.
     * <br>This method is called in order to update the view after a Game State change.
     *
     * @param toUpdate Parameter of the View to update.
     * @param parameters New updated value for the given parameter.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void update(ViewParameter toUpdate, List<Object> parameters) throws IOException;

    /**
     * End of the Game.
     * <br>This method is called in order to end the Game Client-side.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void end() throws IOException;

    /**
     * Heartbeat for the View.
     * <br>This method is called in order to verify if the View is still connected.
     *
     * @throws IOException The connection is no more established.
     */
    void ping() throws IOException;
}
