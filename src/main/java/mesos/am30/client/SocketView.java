package mesos.am30.client;

import mesos.am30.common.*;
import mesos.am30.server.IF_GameController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Socket communication handler View-side.
 * <br>This Class works as the communication logic for the VirtualView Class, handling all the View commands and sending them to the Controller.
 * <br>It implements the communication protocol via Socket.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class SocketView extends VirtualView {
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    private volatile boolean connectionOpen;

    /**
     * Constructor for SocketView.
     *
     * @see VirtualView Everything ensured by VirtualView constructor is ensured here too.
     */
    public SocketView(IF_GameUI userInterface) {
        super(userInterface);
        connectionOpen = true;
    }

    // Test setter for the attribute socket
    void setSocket(Socket socket) {
        this.socket = socket;
    }

    // Test setter for the attribute outputStream
    void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // Test setter for the attribute inputStream
    void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Open the connection to the Server.
     * <br>This method manages the Socket connection between this View and the Server.
     * <br><strong>Pre:</strong> path != null
     * <br><strong>Post:</strong> socket = Socket(path, port) && outputStream = socket.getOutputStream && inputStream = socket.getInputStream &&
     *                  this.nickname = (* unique nickname for each player in the lobby, chosen by the View *)
     *
     * @see VirtualView Deeper description of this method in the VirtualView abstract Class.
     */
    @Override
    public void findServer(String path, int port) throws IOException {
        try {
            // connect to the Server
            socket = new Socket(path, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // listen for Server messages
            startListeningThread();
        } catch (IOException exception) {
            notifyError(ErrorType.WRONG_IP);
            end();
        }
    }

    // start listening for messages from SocketProxy and execute them
    void startListeningThread() {
        // start listening Thread (SocketProxy -> SocketView)
        new Thread(() -> {
            try {
                while (connectionOpen) {
                    try {
                        Message message = (Message) inputStream.readObject();
                        switch (message.getType()) {
                            // give the number of Players for the lobby
                            case FIRST_PLAYER -> askPlayersNumber();
                            // give the Client nickname
                            case NICKNAME -> askNickname();
                            // notification of turn action
                            case NOTIFY -> {
                                ClienTurnMessage turnMessage = (ClienTurnMessage) message;
                                notifyTurn(turnMessage.getNickname(), turnMessage.getMove());
                            }
                            // notification of not valid move
                            case ERROR -> {
                                ErrorMessage errorMessage = (ErrorMessage) message;
                                notifyError(errorMessage.getError());
                            }
                            // notification of View update
                            case UPDATE -> {
                                ModelUpdateMessage updateMessage = (ModelUpdateMessage) message;
                                update(updateMessage.getToUpdate(), updateMessage.getParameters());
                            }
                            // end of the Game
                            case END -> end();
                            // heartbeat
                            case PING -> ping();
                        }
                    } catch (ClassNotFoundException ignored) { /* not valid message */ }
                }
            } catch (IOException exception) {
                try { socket.close(); } catch (IOException ignored) { /* connection closed Server-Side */ }
                try {
                    if (!connectionOpen) return;
                    notifyError(ErrorType.CONNECTION_CRASHED);
                    exception.printStackTrace();
                    end();
                } catch (IOException ignored) { /* userInterface error */ }
            }
        }).start();
    }

    // never called on SocketView (used Server-side via SocketProxy)
    @Override
    public void setController(IF_GameController controller) { /* never called on SocketView */ }

    // invoke Controller methods
    @Override
    protected synchronized void toController(Choice choice, Object parameter) throws IOException {
        outputStream.writeObject(new ClientChoiceMessage(MessageType.CHOOSE, choice, nickname, parameter));
        outputStream.flush();
    }

    /**
     * @see IF_GameView Implementation Client-Side via Socket of the end method
     */
    @Override
    public synchronized void end() throws IOException {
        connectionOpen = false;
        userInterface.printEnd();
        if (!socket.isClosed()) socket.close();
    }
}
