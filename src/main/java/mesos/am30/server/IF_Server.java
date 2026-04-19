package mesos.am30.server;

import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.rmi.Remote;

public interface IF_Server extends Remote {
    void handleConnection(IF_GameView view) throws IOException;

    void setNickname(IF_GameView view, String nickname) throws IOException;

    void setPlayersNumber(IF_GameView view, int playersNumber) throws IOException;

}
