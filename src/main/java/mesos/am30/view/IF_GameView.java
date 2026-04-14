package mesos.am30.view;

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
     * @throws IOException Socket communication error
     */
    void askPlayersNumber() throws IOException;

    /**
     * Ask the Client for its nickname.
     * <br>This method asks the Client which nickname he wants to use.
     *
     * @throws IOException Socket communication error
     */
    void askNickname() throws IOException;
    void setController(IF_GameController controller);
    void startListening();

    void notifyTurn(String nickname, Move move) throws IOException;
    void notifyError(ErrorType errorType) throws IOException;
    void update(ViewParameter toUpdate, List<Object> parameters) throws IOException;
}
