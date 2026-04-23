package mesos.am30.server;

import mesos.am30.GameModel.Player;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.RemoteException;
import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * Static Server for the game "Mesos".
 * <br>This class works as a static implementation of the Server for the game "Mesos".
 * <br>It handles new Clients connecting both via Socket and RMI, binding them to the Controller for the specific lobby.
 * <br>This class extends the class UnicastRemoteObject and implements the interface Remote, in order to work both via RMI and Socket.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class Server extends UnicastRemoteObject implements IF_Server {
    private static Server instance = null;
    private static Controller lobby = null;
    private static List<IF_GameView> connectedViews;

    // constructor of the instance
    Server() throws RemoteException {
        connectedViews = new ArrayList<>();
    }

    /**
     * Static Getter for the Server.
     * <br>This static method returns the static instance of the Server.
     * <br>This allows everything to work in the intended way, even if more instances of the program run simultaneously.
     *
     * @return Static instance of the Server
     * @throws RemoteException The Server cannot be instantiated correctly
     */
    public synchronized static Server getInstance() throws RemoteException {
        if (instance == null) instance = new Server();
        return instance;
    }

    // Test getter for the attribute lobby
    static Controller getLobby() {
        return lobby;
    }

    // Test setter for the attribute lobby
    static void setLobby(Controller lobby) {
        Server.lobby = lobby;
    }

    // Test getter for the attribute connectedViews
    static List<IF_GameView> getConnectedViews() {
        return connectedViews;
    }

    /**
     * Main entry point for the "Mesos" Server.
     * <br>This method is the main entry point for the "Mesos" Server; it creates (or connects to) the Server instance, then
     * opens both the RMI and Socket channel of communications as Threads.
     *
     * @throws IOException The connection cannot be established correctly
     */
    static void main() throws IOException {
        Server server = Server.getInstance();

        // open RMI connection (RMI - Thread)
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("server", server);
            System.out.println("Server RMI Registry open at port 1099");
        } catch (AlreadyBoundException exception) {
            // already running Server
            System.err.println("[Server RMI Registry Error]: " + exception.getMessage());
            return;
        }

        // open Socket connection (Thread)
        new Thread( () -> {
            try (ServerSocket socket = new ServerSocket(12345)) {
                System.out.println("Server Socket open at port 12345");
                while (true) {
                    Socket client = socket.accept();

                    // connect to a Lobby
                    new Thread(() -> {
                        try {
                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                            out.flush();
                            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                            server.handleConnection(new SocketProxy(client, out, in));
                        } catch (IOException exception) {
                            try { client.close(); } catch (IOException ignored) { /* Client connection failed */ }
                        }
                    }).start();
                }
            } catch (IOException exception) {
                System.err.println("[Server Socket Error]: " + exception.getMessage());
            }
        }).start();
    }

    /**
     * @see IF_Server Implementation of the handleConnection method
     */
    @Override
    public synchronized void handleConnection(IF_GameView view) throws IOException {
        connectedViews.add(view);
        startHeartbeat(view);
        if (lobby == null) view.askPlayersNumber();
        else view.askNickname();
    }

    // start a Heartbeat thread
    private void startHeartbeat(IF_GameView view) {
        new Thread(() -> {
            try {
                while (connectedViews.contains(view)) {
                    Thread.sleep(1000);
                    view.ping();
                }
            } catch (IOException | InterruptedException crashed) {
                handleDisconnection(view);
            }
        }).start();
    }

    /**
     * @see IF_Server Implementation of the handleConnection method
     */
    @Override
    public synchronized void setPlayersNumber(IF_GameView view, int playersNumber) throws IOException {
        if (lobby != null) view.notifyError(ErrorType.ALREADY_EXISTING_LOBBY);
        else if (playersNumber < 2 || playersNumber > 5) view.notifyError(ErrorType.WRONG_PLAYERS_NUMBER);
        else {
            lobby = new Controller(playersNumber);
            view.askNickname();
        }
    }

    /**
     * @see IF_Server Implementation of the ping method
     */
    @Override
    public void ping() throws IOException {

    }

    /**
     * @see IF_Server Implementation of the handleConnection method
     */
    @Override
    public synchronized void setNickname(IF_GameView view, String nickname) throws IOException {
        if (lobby == null) view.notifyError(ErrorType.NOT_EXISTING_LOBBY);
        else if (lobby.isFull()) view.notifyError(ErrorType.FULL_LOBBY);
        else {
            // check nickname
            List<String> existingNicknames = lobby.getClients().keySet().stream()
                    .map(Player::getNickname)
                    .toList();
            if (existingNicknames.contains(nickname)) view.notifyError(ErrorType.WRONG_NICKNAME);
            else if (lobby.connect(view, nickname)) new Thread(lobby::startGame).start();
        }
    }

    /**
     * Handle a Client Disconnection.
     * <br>This method notifies all the other clients when a disconnection happens that the Game has come to an end.
     * <br><strong>Pre:</strong> disconnected != null
     *
     * @param disconnected The Client instance of the IF_GameView who disconnected
     */
    public synchronized void handleDisconnection(IF_GameView disconnected) {
        if (!connectedViews.contains(disconnected)) return;
        connectedViews.remove(disconnected);
        connectedViews.forEach(view -> {
            try {
                view.notifyError(ErrorType.END_FOR_DISCONNECTION);
                view.end();
            } catch (IOException ignored) { /* Client connection failed */ }
        });
        connectedViews = new ArrayList<>();
        lobby = null;
    }
}
