package mesos.am30.client;

import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

import java.util.List;
import java.util.Map;

/**
 * View UI's Interface.
 * <br/>Interface for the methods that the View calls on the UI in order to display the Game.
 */
public interface IF_GameUI {

    /**
     * Set the View connected to the UI.
     *
     * @param view View connected to the UI.
     */
    void setVView(VirtualView view);

    /**
     * Set the View Model for the Game.
     *
     * @param vBoard View Model for the Game.
     */
    void setVModel(ViewModel vBoard);

    /**
     * Notify the Client about the successful connection to the Server.
     */
    void confirmConnection();

    /**
     * Show the Client the available Lobbies.
     * <br/><strong>Pre:</strong> availableLobbies != null
     *
     * @param availableLobbies Lobbies available to join.
     */
    void showLobbies(Map<String, Integer> availableLobbies);

    /**
     * Ask the Client for its nickname.
     * <br/>This method asks the Client which nickname he wants to use.
     */
    void askNickname();

    /**
     * Notify the Client about the successfully join to the Lobby.
     */
    void confirmLobbyJoined();

    /**
     * Print the next Move for a Player.
     * <br/><strong>Pre:</strong> nickname != null && move != null
     *
     * @param nickname  Nickname of the Player who has to move.
     * @param move      Next move to perform.
     */
    void printMove(String nickname, Move move);

    /**
     * Print an Error made by the Client.
     * <br/><strong>Pre:</strong> errorType != null
     *
     * @param errorType Error occurred due to a wrong action.
     */
    void printError(ErrorType errorType);

    /**
     * Display a View Model.
     * <br/>This method is called in order to refresh the UI after a Game State change.
     *
     * @param viewModel ViewModel to display.
     */
    void refresh(ViewModel viewModel);

    /**
     * Ask the Client for showing the Rankings.
     * <br/>This method asks the Client if it wants to show the Rankings.
     */
    void askShowRankings();

    /**
     * Show the Rankings.
     * <br/>This method is called in order to show the Rankings.
     * <br/><strong>Pre:</strong> playerRank != null && globalRankings != null
     *
     * @param playerRank        Rank of the Player.
     * @param globalRankings    Ranking of all the Players.
     */
    void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings);

    /**
     * Print the Game's end screen.
     */
    void printEnd();
}
