package mesos.am30.server;

import mesos.am30.client.IF_GameView;

import java.io.IOException;
import java.rmi.Remote;

/**
 * Server's Interface for ModelViewController pattern.
 * <br>Interface for the methods that the Views call directly in order to establish the connection with the Controller.
 */
public interface IF_Server extends Remote {

    /**
     * Handle a Client connection.
     * <br>This method is called by the Clients in order to inform the Server of their connection.
     * <br>If a Client is the first connected to the lobby, the Server asks first for the number of players, otherwise it
     * asks directly for its nickname.
     * <br><strong>Pre:</strong> view != null
     *
     * @param view The Client instance of the IF_GameView
     * @throws IOException The connection cannot be established correctly
     */
    void handleConnection(IF_GameView view) throws IOException;

    /**
     * Check the nickname of a Client.
     * <br>This method is invocated from each Client connecting to the lobby, and works as the Server-side counterpart to
     * the method view.askNickname().
     * <br> If the nickname is valid (no duplicates), the Server connects the Client to the lobby.
     * <br><strong>Pre:</strong> view != null && nickname != null
     * <br><strong>Post:</strong> !\old(lobby.getClients().keySet().contains(nickname)) ==> lobby.getClients().keySet().contains(nickname)
     *
     * @param view The Client instance of the IF_GameView
     * @param nickname Nickname of the Client
     * @throws IOException The connection cannot be established correctly
     */
    void setNickname(IF_GameView view, String nickname) throws IOException;

    /**
     * Creates a lobby for a give number of Players.
     * <br>This method is invocated from the first Client connecting to the lobby, and works as the Server-side counterpart to
     * the method view.askPlayersNumber().
     * <br> If the number is valid, the Server creates the lobby, then asks the Client for its nickname.
     * <br><strong>Pre:</strong> view != null
     * <br><strong>Post:</strong> 2 <= playersNumber <= 5 && lobby == new Controller(playersNumber)
     *
     * @param view The Client instance of the IF_GameView
     * @param playersNumber Number of Players for the lobby
     * @throws IOException The connection cannot be established correctly
     */
    void setPlayersNumber(IF_GameView view, int playersNumber) throws IOException;

    /**
     * Heartbeat for the Server.
     * <br>This method is called in order to verify if the Server is still connected.
     *
     * @throws IOException The connection is no more established
     */
    void ping () throws IOException;
}
