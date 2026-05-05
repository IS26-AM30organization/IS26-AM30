package mesos.am30.client;

import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

import java.util.Map;

public interface IF_GameUI {
    void askNickname();

    /**
     * Shows the available lobbies the User can join
     * @param availableLobbies map of lobby codes to their current number of players
     */
    void showLobbies(Map<String, Integer> availableLobbies);

    /**
     * Notifies the User that the connection to the Server has been established
     */
    void confirmConnection();

    /**
     * Notifies the User that they have successfully joined the lobby
     */
    void confirmLobbyJoined();

    void printMove(String nickname, Move move);
    void printError(ErrorType errorType);
    void refresh(ViewModel viewModel);
    void printEnd();
}
