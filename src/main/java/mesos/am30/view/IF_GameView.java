package mesos.am30.view;

import mesos.am30.GameModel.Player;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.server.IF_GameController;

import java.io.IOException;
import java.util.List;

public interface IF_GameView {

    /**
     * Ask the Client the number of players.
     * <br>This method asks the first Client connecting to a lobby how many players will participate.
     *
     * @return Number of players expecting to participate and connect to the lobby
     * @throws IOException Socket communication error
     * @throws ClassNotFoundException Message communication error
     */
    int askPlayersNumber() throws IOException, ClassNotFoundException;

    /**
     * Ask the Client for its nickname.
     * <br>This method asks the Client which nickname he wants to use.
     *
     * @return Nickname of the Client
     * @throws IOException Socket communication error
     * @throws ClassNotFoundException Message communication error
     */
    String askNickname() throws IOException, ClassNotFoundException;
    void setController(IF_GameController controller);
    void startListening();

    void notifyTurn(Player player, Move move) throws IOException;
    void notifyError(ErrorType errorType) throws IOException;
    void update(ViewParameter toUpdate, List<?> parameters) throws IOException;
}
