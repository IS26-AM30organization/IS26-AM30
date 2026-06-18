package mesos.am30.client.view;

import mesos.am30.client.IF_GameUI;
import mesos.am30.common.interfaces.IF_GameView;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.common.enumerations.Choice;
import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.gameModel.card.Tile;

import mesos.am30.common.interfaces.IF_GameController;
import mesos.am30.common.interfaces.IF_Server;
import mesos.am30.common.interfaces.IORunnable;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI communication handler View-side.
 * <br/>This Class works as the communication logic for the VirtualView Class, handling all the View commands and sending them to the Controller.
 * <br/>It implements the communication protocol via RMI.
 */
public class RMIView extends VirtualView {
    private IF_Server remoteServer;
    private IF_GameController controller;
    private Registry registry;
    private volatile boolean connectionOpen = true;

    /**
     * Constructor for RMIView.
     *
     * @see VirtualView Everything ensured by VirtualView constructor is ensured here too.
     */
    public RMIView(IF_GameUI userInterface) throws RemoteException {
        super(userInterface);
        UnicastRemoteObject.exportObject(this, 0);
        this.registry = null;
    }

    // Test setter for the attribute "remoteServer"
    void setRemoteServer(IF_Server remoteServer) {
        this.remoteServer = remoteServer;
    }

    // Test setter for the attribute "connectionOpen" as false
    void closeConnection() {
        this.connectionOpen = false;
    }

    /**
     * Open the connection to the Server.
     * <br/>This method handles the RMI connection between this View and the Server.
     * <br/><strong>Pre:</strong> path != null
     * <br/><strong>Post:</strong> registry = LocateRegistry.getRegistry(path, port) &amp;&amp; remoteServer = registry.lookup("server")
     *
     * @see VirtualView Deeper description of this method in the VirtualView abstract Class.
     */
    @Override
    public void findServer(String path, int port) throws IOException {
        try {
            registry = LocateRegistry.getRegistry(path, port);
            remoteServer = (IF_Server) registry.lookup("server");
            remoteServer.handleConnection(this);
            startHeartbeat(remoteServer);
        }
        catch (IOException | NotBoundException e) {
            notifyError(ErrorType.WRONG_IP);
            end();
        }
    }

    // start a Heartbeat thread
    void startHeartbeat(IF_Server server) {
        new Thread(() -> {
            try {
                while(true) {
                    Thread.sleep(1000);
                    if (connectionOpen) server.ping();
                    else return;
                }
            } catch (IOException | InterruptedException crashed) {
                try {
                    notifyError(ErrorType.CONNECTION_CRASHED);
                    end();
                } catch (IOException ignored) { /* ignored */ }
            }
        }).start();
    }

    // invoke Server methods
    @Override
    protected void toServer(Choice choice, String lobbyCode, Object parameter) throws IOException {
        switch (choice) {
            case CREATE_LOBBY           -> asynchronousServerCall(() -> remoteServer.createLobby(this, (Integer) parameter, lobbyCode));
            case GET_AVAILABLE_LOBBIES  -> asynchronousServerCall(() -> remoteServer.showAvailableLobbies(this));
            case JOIN_LOBBY             -> asynchronousServerCall(() -> remoteServer.joinLobby(this, lobbyCode));
            case NICKNAME               -> asynchronousServerCall(() -> remoteServer.setNickname(this, (String) parameter, lobbyCode));
        }
    }

    // invoke Controller methods
    @Override
    protected void toController(Choice choice, Object parameter) throws IOException {
        switch (choice) {
            case CHOOSE_TILE        -> asynchronousServerCall(() -> controller.chooseTile(nickname,(Tile) parameter));
            case CHOOSE_BUILDING    -> asynchronousServerCall(() -> controller.chooseBuilding(nickname, (BuildingCard) parameter));
            case CHOOSE_CHARACTER   -> asynchronousServerCall(() -> controller.chooseCharacter(nickname, (CharacterCard) parameter));
            case RANKINGS           -> asynchronousServerCall(() -> controller.showRankings(nickname, (boolean) parameter));
        }
    }

    // handle an asynchronous view method call
    private void asynchronousServerCall(IORunnable method) throws IOException {
        new Thread(() -> {
            try {
                method.run();
            } catch (IOException ignored) { /* handled by heartbeat */ }
        }).start();
    }

    /**
     * @see IF_GameView Implementation Client-Side via RMI of the setController method.
     */
    @Override
    public void setController(IF_GameController controller) throws IOException {
        this.controller = controller;
    }

    /**
     * @see IF_GameView Implementation Client-Side via RMI of the end method.
     */
    @Override
    public void end() throws IOException {
        connectionOpen = false;
        userInterface.printEnd();
        UnicastRemoteObject.unexportObject(this, true);
    }
}
