package mesos.am30.common.interfaces;

import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.common.enumerations.Move;
import mesos.am30.common.enumerations.ViewParameter;

import java.io.IOException;
import java.rmi.Remote;

import java.util.List;
import java.util.Map;

/**
 * Views's Interface for ModelViewController pattern.
 * <br/>Interface for the methods that the Server calls directly on the View in order to notify a request/Error/update.
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
     * <br/><strong>Pre:</strong> availableLobbies != null
     *
     * @param availableLobbies Lobbies available to join.
     *
     * @throws IOException The connection cannot be established.
     */
    void showLobbies(Map<String, Integer> availableLobbies) throws IOException;

    /**
     * Ask the Client for its nickname.
     * <br/>This method asks the Client which nickname he wants to use.
     * <br/><strong>Pre:</strong> lobbyCode != null
     *
     * @param lobbyCode Code of the lobby.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void askNickname(String lobbyCode) throws IOException;

    /**
     * Notify the Client about the successfully join to the Lobby.
     *
     * @throws IOException The connection cannot be established.
     */
    void confirmLobbyJoined() throws IOException;

    /**
     * Set the controller.
     * <br/>This method sets the Controller with which the View is communicating following the ModelViewController Pattern.
     * <br/><strong>Pre:</strong> controller != null
     *
     * @param controller Controller calling the method.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void setController(IF_GameController controller) throws IOException;

    /**
     * Notify the View about the Turn change.
     * <br/>This method is called in order to notify the View about the turn change, telling who has to move and what he has to do.
     * <br/><strong>Pre:</strong> nickname != null &amp;&amp; move != null
     *
     * @param nickname  Nickname of the Player who has to move.
     * @param move      Next move to perform.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void notifyTurn(String nickname, Move move) throws IOException;

    /**
     * Notify the View about an Error.
     * <br/>This method is called in order to notify the View about an Error it has made.
     * <br/><strong>Pre:</strong> errorType != null
     *
     * @param errorType Error occurred due to a wrong action.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void notifyError(ErrorType errorType) throws IOException;

    /**
     * Update the View.
     * <br/>This method is called in order to update the view after a Game State change.
     * <br/><strong>Pre:</strong> toUpdate != null &amp;&amp; parameters != null
     *
     * @param toUpdate      Parameter of the View to update.
     * @param parameters    New updated value for the given parameter.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void update(ViewParameter toUpdate, List<Object> parameters) throws IOException;

    /**
     * Ask the Client for showing the Rankings.
     * <br/>This method asks the Client if it wants to show the Rankings.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void askShowRankings() throws IOException;

    /**
     * Show the Rankings.
     * <br/>This method is called in order to show the Rankings.
     * <br/><strong>Pre:</strong> playerRank != null &amp;&amp; globalRankings != null
     *
     * @param playerRank        Rank of the Player.
     * @param globalRankings    Ranking of all the Players.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) throws IOException;

    /**
     * End of the Game.
     * <br/>This method is called in order to end the Game Client-side.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    void end() throws IOException;

    /**
     * Heartbeat for the View.
     * <br/>This method is called in order to verify if the View is still connected.
     *
     * @throws IOException The connection is no more established.
     */
    void ping() throws IOException;
}
