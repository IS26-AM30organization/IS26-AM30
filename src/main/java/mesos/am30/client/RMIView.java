package mesos.am30.client;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.common.Choice;
import mesos.am30.common.ErrorType;
import mesos.am30.gameModel.card.Tile;

import mesos.am30.server.IF_GameController;
import mesos.am30.server.IF_Server;
import mesos.am30.server.IORunnable;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIView extends VirtualView {
    private IF_Server remoteServer;
    private IF_GameController controller;
    private Registry registry;

    public RMIView(IF_GameUI userInterface) throws RemoteException {
        super(userInterface);
        UnicastRemoteObject.exportObject(this, 0);
        this.registry = null;
    }

    void setRemoteServer(IF_Server remoteServer) {
        this.remoteServer = remoteServer;
    }

    /**
     * Open the connection to the Server
     * @param path URL of the server
     * @param port Port opened by the Server
     * @throws IOException The connection cannot be established correctly
     */
    @Override
    public void findServer(String path, int port) throws IOException {
        try {
            registry = LocateRegistry.getRegistry(path, port);
            remoteServer = (IF_Server) registry.lookup("server");
            remoteServer.handleConnection(this);
            startHeartbeat(remoteServer);
        }
        catch (IOException | NotBoundException e){
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
                    server.ping();
                }
            } catch (IOException | InterruptedException crashed) {
                try {
                    notifyError(ErrorType.CONNECTION_CRASHED);
                    end();
                } catch (IOException ignored) {

                }
            }
        }).start();
    }

    // Client writes to controller
    @Override
    protected void toController(Choice choice, Object parameter) throws IOException {
        switch (choice) {
            case PLAYERS_NUMBER -> asynchronousServerCall(() -> remoteServer.setPlayersNumber(this, (int) parameter));

            case NICKNAME -> asynchronousServerCall(() -> remoteServer.setNickname(this, (String) parameter));

            case CHOOSE_TILE -> asynchronousServerCall(() -> controller.chooseTile(nickname,(Tile) parameter));

            case CHOOSE_BUILDING -> asynchronousServerCall(() -> controller.chooseBuilding(nickname, (BuildingCard) parameter));

            case CHOOSE_CHARACTER -> asynchronousServerCall(() -> controller.chooseCharacter(nickname, (CharacterCard) parameter));
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

    @Override
    public void setController(IF_GameController controller) throws IOException {
        try {
            this.controller = (IF_GameController) registry.lookup("lobby");
        } catch (IOException | NotBoundException e){
            notifyError(ErrorType.WRONG_IP);
            end();
        }
    }

    @Override
    public void end() throws IOException {
        userInterface.printEnd();
        UnicastRemoteObject.unexportObject(this, true);
    }
}
