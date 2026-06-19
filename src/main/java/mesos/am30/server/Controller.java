package mesos.am30.server;

import mesos.am30.common.interfaces.IF_GameController;
import mesos.am30.db.GameResultsDAO;
import mesos.am30.gameModel.*;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.IF_GameModel;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.common.interfaces.IF_GameView;
import mesos.am30.common.enumerations.Move;

import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.sql.SQLException;

/**
 * Controller for the Players' Moves.
 * <br/>This Class works as the Controller for the Players' Moves, defining the Controller in the ModelViewController Pattern.
 */
public class Controller extends UnicastRemoteObject implements IF_GameController {
    private Server server;
    private IF_GameModel board;
    private final Map<Player, IF_GameView> connections;
    private final int numPlayers;

    /**
     * Constructor for the Controller.
     * <br/><strong>Pre:</strong> 2 &lt;= numPlayers &lt;= 5
     * <br/><strong>Post:</strong> this.numPlayers = numPlayers
     *
     * @param numPlayers Number of Players for the given Game.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public Controller(int numPlayers) throws IOException {
        this.numPlayers = numPlayers;
        this.connections = new HashMap<>(numPlayers);
        this.server = Server.getInstance();
    }

    // Test setter for the attribute "server"
    void setServer(Server server) {
        this.server = server;
    }

    // Test getter for the attribute "numPlayers"
    int getPlayersNumber() {
        return numPlayers;
    }

    /**
     * Getter for the connected Clients (identified by their Player).
     *
     * @return Map of tuples (Player, Client).
     */
    public Map<Player, IF_GameView> getClients() {
        return connections;
    }

    /**
     * Get the number of occupied slots of the Lobby.
     *
     * @return Number of Players connected.
     */
    public synchronized int getOccupiedSlots() {
        return getClients().size();
    }

    // get a Player by its Nickname
    private Player getPlayerByNickname(String nickname) {
        return connections.keySet().stream()
                .filter(p -> nickname.equals(p.getNickname())).toList().getFirst();
    }

    /**
     * Check if the Lobby is full.
     *
     * @return True is all the Clients are connected, false otherwise.
     */
    public synchronized boolean isFull() {
        return connections.size() == numPlayers;
    }

    /**
     * Connect a Client to the Lobby.
     *
     * @param view      Client to connect.
     * @param nickname  Nickname of the Client.
     *
     * @return True if the Client fills the lobby, false otherwise.
     * @throws IOException The connection cannot be established correctly.
     */
    public boolean connect(IF_GameView view, String nickname) throws IOException {
        connections.put(new Player(nickname), view);
        view.setController(this);
        return connections.size() == numPlayers;
    }

    /**
     * Start the Game.
     * <br/>This method set up the Model and starts the Game, notifying the first Move.
     */
    synchronized public void startGame() {
        board = new Board(
                connections.keySet().stream().toList(),
                connections.values().stream().toList()
        );

        try {
            board.prepare();
        } catch (IOException e) {
            System.err.println("[Error]: error on start-up" + e.getMessage());
            for (IF_GameView view : connections.values()) server.handleDisconnection(view);
        }
        board.start();

        Player currentPlayer = board.getCurrentPlayer();
        try {
            sendMove(currentPlayer, Move.PICK_TILE);
        } catch (IOException e) {
            System.err.println("[Error]: error on start-up" + e.getMessage());
            for (IF_GameView view : connections.values()) server.handleDisconnection(view);
        }
    }

    // check if the Player can Move
    boolean isPlayerTurn(Player requestingPlayer) {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer == null) return false;
        return requestingPlayer.equals(currentPlayer);
    }

    /**
     * @see IF_GameController Controller implementation of the chooseTile method.
     */
    @Override
    synchronized public void chooseTile(String nickname, Tile requestingTile) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);

        if (!isPlayerTurn(requestingPlayer) || !requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        try {
            Tile chosenTile = board.getTiles().stream().filter(t -> t.equals(requestingTile)).toList().getFirst();
            board.pickTile(requestingPlayer, chosenTile);
        } catch (NoSuchElementException e) {
            handleError(requestingPlayer, ErrorType.WRONG_TILE);
        }
    }

    /**
     * @see IF_GameController Controller implementation of the chooseCharacter method.
     */
    @Override
    synchronized public void chooseCharacter(String nickname, CharacterCard card) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);

        if (!isPlayerTurn(requestingPlayer) || requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        if (tryPickedCharacter(requestingPlayer, card)) {
            if (board.pickCard(requestingPlayer, card)) {
                board.nextRound();
            }
        }
    }

    // check if a CharacterCard can be picked
    private boolean tryPickedCharacter(Player requestingPlayer, CharacterCard card) throws IOException {
        if (requestingPlayer.hasEnoughUpMoves() && board.getUpperRow().contains(card)) {
            requestingPlayer.decreaseRemainingUpMoves();
            return true;
        }
        else if (requestingPlayer.hasEnoughDownMoves() && board.getLowerRow().contains(card)) {
            requestingPlayer.decreaseRemainingDownMoves();
            return true;
        }
        handleError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    /**
     * @see IF_GameController Controller implementation of the chooseBuilding method.
     */
    @Override
    synchronized public void chooseBuilding(String nickname, BuildingCard card) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);
        
        if (!isPlayerTurn(requestingPlayer) || requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        if (tryPickedBuilding(requestingPlayer, card)) {
            if (board.pickCard(requestingPlayer, card))
                board.nextRound();
        }
    }

    // check if a BuildingCard can be picked
    private boolean tryPickedBuilding(Player requestingPlayer, BuildingCard card) throws IOException {
        if (requestingPlayer.hasEnoughUpMoves() && board.getUpperBuildings().contains(card)) {
            if (!card.canBeBought(requestingPlayer)) {
                handleError(requestingPlayer, ErrorType.NOT_ENOUGH_FOOD);
                return false;
            } else {
                requestingPlayer.decreaseRemainingUpMoves();
                return true;
            }
        }
        else if (requestingPlayer.hasEnoughDownMoves() && board.getLowerBuildings().contains(card)) {
            if (!card.canBeBought(requestingPlayer)) {
                handleError(requestingPlayer, ErrorType.NOT_ENOUGH_FOOD);
                return false;
            } else {
                requestingPlayer.decreaseRemainingDownMoves();
                return true;
            }
        }
        handleError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    /**
     * @see IF_GameController Controller implementation of the showRankings method.
     */
    @Override
    public void showRankings(String nickname, boolean response) throws IOException {
        IF_GameView connection = connections.get(getPlayerByNickname(nickname));
        if (response) {
            try {
                connection.showRankings(
                        GameResultsDAO.queryPlayerRank(numPlayers, nickname),
                        GameResultsDAO.queryGlobalRanking(numPlayers)
                );
            } catch (SQLException exception) {
                connection.notifyError(ErrorType.DB_ERROR);
            }
        }

        // disconnect the Player
        Server.getInstance().disconnectPlayerGracefully(connection);
        try {
            connection.end();
        } catch (Exception ignored) { /* ignored */ }
    }

    //OTHER METHODS:

    // notify a Client about its Move
    void sendMove(Player player, Move move) throws IOException {
        IF_GameView connection = connections.get(player);
        if (connection != null) connection.notifyTurn(player.getNickname(), move);
    }

    // send the current Move
    private void reSendCurrentMove() throws IOException {
        sendMove(board.getCurrentPlayer(), board.getCurrentMove());
    }

    // notify a Client about an Error
    private void handleError(Player player, ErrorType errorType) throws IOException {
        IF_GameView connection = connections.get(player);
        if (connection != null) {
            connection.notifyError(errorType);
            reSendCurrentMove();
        }
    }
}
