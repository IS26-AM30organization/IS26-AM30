package mesos.am30.server;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.*;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * SocketView handler Server-side.
 * <br>This Class works as a Stub for the SocketView Class, allowing polymorphic methods call on the View via the Controller.
 * <br>When calling a method on the View, this Proxy gives the illusion of being able to call directly the SocketView one (such as view.notifyTurn(Move)) like in RMI,
 * where in reality the low-level communication via Socket happens here.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
class SocketProxy implements IF_GameView {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final Server server;
    private IF_GameController controller = null;
    private volatile boolean connectedToController;
    private volatile boolean connectionOpen;

    /**
     * Constructor for a SocketView Proxy.
     * <br><strong>Pre:</strong> socket != null && outputStream != null && inputStream != null &&
     *      socket.getOutputStream().equals(outputStream) &&
     *      socket.getInputStream().equals(inputStream)
     * <br><strong>Post:</strong> this.socket = socket && this.outputStream = outputStream && this.inputStream = inputStream
     *
     * @param socket The socket used for connection with the real SocketView
     * @param outputStream The output stream of the socket
     * @param inputStream The input stream of the socket
     */
    public SocketProxy(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) throws RemoteException {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        server = Server.getInstance();
        connectedToController = false;
        connectionOpen = true;

        // start listening Thread (SocketView -> SocketProxy)
        startListeningThread();
    }

    // start listening for messages from SocketView and deploy them to Server/Controller
    private void startListeningThread() {
        new Thread(() -> {
            try {
                while (connectionOpen) {
                    try {
                        Message message = (Message) inputStream.readObject();
                        if (message.getType() == MessageType.CHOOSE) {
                            ClientChoiceMessage choiceMessage = (ClientChoiceMessage) message;
                            if (connectedToController) {
                                // game phase
                                switch (choiceMessage.getChoice()) {
                                    case CHOOSE_TILE ->
                                            controller.chooseTile(choiceMessage.getIdentifier(), (Tile) choiceMessage.getParameter());
                                    case CHOOSE_CHARACTER ->
                                            controller.chooseCharacter(choiceMessage.getIdentifier(), (CharacterCard) choiceMessage.getParameter());
                                    case CHOOSE_BUILDING ->
                                            controller.chooseBuilding(choiceMessage.getIdentifier(), (BuildingCard) choiceMessage.getParameter());
                                }
                            } else {
                                // connection phase
                                switch (choiceMessage.getChoice()) {
                                    case CREATE_LOBBY ->
                                            server.createLobby(this, (Integer) choiceMessage.getParameter(), choiceMessage.getIdentifier());
                                    case NICKNAME ->
                                            server.setNickname(this, (String) choiceMessage.getParameter(), choiceMessage.getIdentifier());
                                    case JOIN_LOBBY ->
                                            server.joinLobby(this, (String) choiceMessage.getParameter());
                                    case GET_AVAILABLE_LOBBIES ->
                                            server.showAvailableLobbies(this);
                                }
                            }
                        }
                    } catch (ClassNotFoundException ignored) { /* not valid message */ }
                }
            } catch (IOException exception) {
                try { socket.close(); } catch (IOException ignored) { /* Client connection failed */ }
                if (connectionOpen) server.handleDisconnection(this);
            }
        }).start();
    }

    /**
     * @see IF_GameView Implementation Server-side via Socket Proxy of the askNickname method
     */
    @Override
    public void askNickname() throws IOException {
        outputStream.writeObject(new Message(MessageType.NICKNAME));
        outputStream.flush();
    }

    /**
     * Asks the Client which game to join
     *
     * @throws IOException
     */
    @Override
    public void askLobbyCode() throws IOException {
        outputStream.writeObject(new Message(MessageType.ASK_LOBBY_CODE));
        outputStream.flush();
    }

    /**
     * Request the available lobbies to the Server
     *
     * @throws IOException
     */
    @Override
    public void requestAvailableLobbies() throws IOException {

    }

    /**
     * Shows the lobbies you can connect to
     *
     * @param availableLobbies
     * @throws IOException
     */
    @Override
    public void showLobbies(Map<String, Integer> availableLobbies) throws IOException {
        outputStream.writeObject(new ClientChoiceMessage(MessageType.SHOW_LOBBIES, null, null, availableLobbies));
        outputStream.flush();
    }

    /**
     * @see IF_GameView Implementation Server-side via Socket Proxy of the createLobby method
     */
    @Override
    public void createLobby() throws IOException {
        outputStream.writeObject(new Message(MessageType.FIRST_PLAYER));
        outputStream.flush();
    }

    /**
     * Asks the server to join a lobby
     *
     * @throws IOException
     */
    @Override
    public void joinLobby() throws IOException {
        outputStream.writeObject(new Message(MessageType.ASK_JOIN_LOBBY));
        outputStream.flush();
    }

    /**
     * Confirm the instauration of connection to the Client
     *
     * @throws IOException
     */
    @Override
    public void confirmConnection() throws IOException {
        outputStream.writeObject(new Message(MessageType.CONFIRM_CONNECTION));
        outputStream.flush();
    }

    /**
     * Confirm that the Client joined the Lobby
     *
     * @param code the lobby code the Client joined
     * @throws IOException
     */
    @Override
    public void confirmLobbyJoined(String code) throws IOException {
        outputStream.writeObject(new ClientChoiceMessage(MessageType.CONFIRM_LOBBY_JOINED, null, null, code));
        outputStream.flush();
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the setController method
     */
    @Override
    public void setController(IF_GameController controller) {
        this.controller = controller;
        connectedToController = true;
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the notifyTurn method
     */
    @Override
    public void notifyTurn(String nickname, Move move) throws IOException {
        outputStream.writeObject(new ClienTurnMessage(MessageType.NOTIFY, nickname, move));
        outputStream.flush();
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the notifyError method
     */
    @Override
    public void notifyError(ErrorType errorType) throws IOException {
        outputStream.writeObject(new ErrorMessage(MessageType.ERROR, errorType));
        outputStream.flush();
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the update method
     */
    @Override
    public void update(ViewParameter toUpdate, List<Object> parameters) throws IOException {
        outputStream.writeObject(new ModelUpdateMessage(MessageType.UPDATE, toUpdate, parameters));
        outputStream.flush();
        outputStream.reset();
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the end method
     */
    @Override
    public void end() throws IOException {
        outputStream.writeObject(new Message(MessageType.END));
        outputStream.flush();
        connectionOpen = false;
        socket.close();
    }

    /**
     * @see IF_GameView Implementation Server-Side via Socket Proxy of the ping method
     */
    @Override
    public void ping() throws IOException {
        outputStream.writeObject(new Message(MessageType.PING));
        outputStream.flush();
    }
}
