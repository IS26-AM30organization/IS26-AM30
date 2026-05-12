package mesos.am30.server;

import mesos.am30.gameModel.Player;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Static Server for the game "Mesos".
 * <br>This class works as a static implementation of the Server for the game "Mesos".
 * <br>It handles new Clients connecting both via Socket and RMI, binding them to the Controller for the specific lobby.
 * <br>This class extends the class UnicastRemoteObject and implements the interface Remote, in order to work both via RMI and Socket.
 */
public class Server extends UnicastRemoteObject implements IF_Server {
    private static Server instance = null;
    private static ThreadPoolExecutor executor;
    private static Registry registry;
    private static final Map<String, Controller> lobbies = new ConcurrentHashMap<>();
    private static final Map<String, List<IF_GameView>> lobbyViews = new ConcurrentHashMap<>();
    private static final List<IF_GameView> pendingViews = new ArrayList<>();
    private static Random random = new Random();

    // constructor of the instance
    Server() throws RemoteException {
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    // Package-private getters/setters used by test
    static Map<String, Controller> getLobbies() { return lobbies; }
    static Map<String, List<IF_GameView>> getLobbyViews() { return lobbyViews; }
    static List<IF_GameView> getPendingViews() { return pendingViews; }
    static void setRegistry(Registry r) { registry = r; }
    static void setRandom(Random r) { random = r; }

    /**
     * Static Getter for the Server.
     * <br>This static method returns the static instance of the Server.
     * <br>This allows everything to work in the intended way, even if more instances of the program run simultaneously.
     *
     * @return Static instance of the Server.
     * @throws RemoteException The Server cannot be instantiated correctly.
     */
    public synchronized static Server getInstance() throws RemoteException {
        if (instance == null) instance = new Server();
        return instance;
    }

    // It generates a 6 decimal digit code
    private String generateLobbyCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1_000_000));
        } while (lobbies.containsKey(code));
        return code;
    }

    // It finds the lobby code containing the view (null if pending)
    private String findLobbyCodeOf(IF_GameView view) {
        return lobbyViews.entrySet().stream()
                .filter(e -> e.getValue().contains(view))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    // handle an asynchronous view method call
    private void asynchronousViewCall(IORunnable method) throws IOException {
        executor.execute(() -> {
            try {
                method.run();
            } catch (IOException ignored) { /* handled by heartbeat */ }
        });
    }

    /**
     * Main entry point for the "Mesos" Server.
     * <br>This method is the main entry point for the "Mesos" Server; it creates (or connects to) the Server instance, then
     * opens both the RMI and Socket channel of communications as Threads.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    static void main() throws IOException {
        Server server = Server.getInstance();
        if (startRmiServer(server, 1099)) startSocketServer(server, 12345);
    }

    // package-private for testing
    static boolean startRmiServer(Server server, int port) throws IOException {
        try {
            registry = LocateRegistry.createRegistry(port);
            registry.bind("server", server);
            System.out.println("Server RMI Registry open at port " + port);
            return true;
        } catch (AlreadyBoundException exception) {
            // already running Server
            System.err.println("[Server RMI Registry Error]: " + exception.getMessage());
            return false;
        }
    }

    // package-private for testing
    static void startSocketServer(Server server, int port) {
        new Thread( () -> {
            try (ServerSocket socket = new ServerSocket(port)) {
                System.out.println("Server Socket open at port " + port);
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
     * @see IF_Server Implementation of the handleConnection method.
     */
    @Override
    public synchronized void handleConnection(IF_GameView view) throws IOException {
        pendingViews.add(view);
        startHeartbeat(view);
        asynchronousViewCall(view::confirmConnection);
    }

    // start a Heartbeat thread
    private void startHeartbeat(IF_GameView view) {
        new Thread(() -> {
            try {
                while (pendingViews.contains(view) || findLobbyCodeOf(view) != null) {
                    Thread.sleep(1000);
                    view.ping();
                }
            } catch (IOException | InterruptedException crashed) {
                handleDisconnection(view);
            }
        }).start();
    }

    /**
     * @see IF_Server Implementation of the createLobby method.
     */
    @Override
    public synchronized void createLobby(IF_GameView view, int playersNumber, String lobbyCode) throws IOException {
        if (lobbyCode.isBlank()) {
            lobbyCode = generateLobbyCode();
        } else if (!lobbyCode.matches("\\d{6}")) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.INVALID_LOBBY_CODE));
            return;
        }
        if (lobbies.containsKey(lobbyCode)) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.ALREADY_EXISTING_LOBBY));
            return;
        }
        if (playersNumber < 2 || playersNumber > 5) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.WRONG_PLAYERS_NUMBER));
            return;
        }

        // create the new lobby
        Controller newLobby = new Controller(playersNumber);
        lobbies.put(lobbyCode, newLobby);
        registry.rebind(lobbyCode, newLobby);
        lobbyViews.put(lobbyCode, new ArrayList<>());

        // ask for the Nickname
        String finalLobbyCode = lobbyCode;
        asynchronousViewCall(() -> view.askNickname(finalLobbyCode));
    }

    /**
     * @see IF_Server Implementation of the showAvailableLobbies method.
     */
    @Override
    public synchronized void showAvailableLobbies(IF_GameView view) throws IOException {
        Map<String, Integer> available = new HashMap<>();
        lobbies.forEach((code, controller) -> {
            if (!controller.isFull() && controller.getOccupiedSlots() > 0) available.put(code, controller.getOccupiedSlots());
        });
        asynchronousViewCall(() -> view.showLobbies(available));
    }

    /**
     * @see IF_Server Implementation of the joinLobby method.
     */
    @Override
    public synchronized void joinLobby(IF_GameView view, String lobbyCode) throws IOException {
        if (!lobbies.containsKey(lobbyCode)) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.NOT_EXISTING_LOBBY));
            return;
        }
        Controller target = lobbies.get(lobbyCode);
        if (target.isFull()) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.FULL_LOBBY));
            return;
        }
        asynchronousViewCall(() -> view.askNickname(lobbyCode));
    }

    /**
     * @see IF_Server Implementation of the setNickname method.
     */
    @Override
    public synchronized void setNickname(IF_GameView view, String nickname, String code) throws IOException {
        Controller target = (code == null) ? null : lobbies.get(code);
        if (target == null) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.NOT_EXISTING_LOBBY));
            return;
        }

        // check Nickname
        List<String> existingNicknames = target.getClients().keySet().stream()
                .map(Player::getNickname)
                .toList();
        if (existingNicknames.contains(nickname)) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.WRONG_NICKNAME));
            return;
        }

        if (target.isFull()) {
            asynchronousViewCall(() -> view.notifyError(ErrorType.FULL_LOBBY));
            return;
        }

        // Last player to full the lobby, game starts
        if (target.connect(view, nickname)) new Thread(target::startGame).start();
        // Other players are needed to start the game. Just send the Client a confirmation that he has joined the lobby.
        else asynchronousViewCall(view::confirmLobbyJoined);
        pendingViews.remove(view);
        lobbyViews.get(code).add(view);
    }

    /**
     * @see IF_Server Implementation of the ping method.
     */
    @Override
    public void ping() throws IOException {}

    /**
     * Handle a Client Disconnection.
     * <br>This method notifies all the other clients when a disconnection happens that the Game has come to an end.
     * <br><strong>Pre:</strong> disconnected != null
     *
     * @param disconnected The Client instance of the IF_GameView who disconnected.
     */
    public synchronized void handleDisconnection(IF_GameView disconnected) {
        if (pendingViews.remove(disconnected)) return;

        // end Game for all Views in the Lobby
        String code = findLobbyCodeOf(disconnected);
        if (code == null) return;
        List<IF_GameView> views = lobbyViews.get(code);
        views.remove(disconnected);

        // Notify the end of the game to all the others Client
        views.forEach(view -> {
            try {
                asynchronousViewCall(() -> view.notifyError(ErrorType.END_FOR_DISCONNECTION));
                asynchronousViewCall(view::end);
            } catch (IOException ignored) { /* Client connection failed */ }
        });

        // Remove the lobby
        lobbies.remove(code);
        lobbyViews.remove(code);
    }
}
