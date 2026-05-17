package mesos.am30.server;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.board.Board;
import mesos.am30.gameModel.IF_GameModel;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.ErrorType;
import mesos.am30.client.IF_GameView;
import mesos.am30.common.Move;

import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


public class Controller extends UnicastRemoteObject implements IF_GameController {
    private IF_GameModel board;
    private Map<Player, IF_GameView> connections;
    private int numPlayers;

    public Controller(int numPlayers) throws IOException {
        this.numPlayers = numPlayers;
        this.connections = new HashMap<>(numPlayers);
    }

    synchronized protected void startTest(IF_GameModel board) throws IOException {
        this.board = board;
        this.board.prepare();
        this.board.start();
    }

    public boolean isFull() {
        return connections.size() == numPlayers;
    }

    public boolean connect(IF_GameView view, String nickname) throws IOException {
        connections.put(new Player(nickname), view);
        view.setController(this);
        return connections.size() == numPlayers;
    }

    synchronized public void startGame() {
        board = new Board(
                connections.keySet().stream().toList(),
                connections.values().stream().toList()
        );

        try {
            board.prepare();
        } catch (IOException e) {
            System.err.println("[Error]: error on start-up" + e.getMessage());
        }
        board.start();

        Player currentPlayer = board.getCurrentPlayer();
        try {
            sendMove(currentPlayer, Move.PICK_TILE);
        } catch (IOException e) {
            System.err.println("[Error]: error on start-up" + e.getMessage());
        }
    }
    synchronized public void chooseTile(String nickname, Tile requestingTile) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);
        Player currentPlayer = board.getCurrentPlayer();

        if (!isPlayerTurn(requestingPlayer, currentPlayer) || !requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        Tile chosenTile = null;

        try {
            chosenTile = board.getTiles().stream().filter(t -> t.equals(requestingTile)).toList().getFirst();
        } catch (NoSuchElementException e) {
            handleError(requestingPlayer, ErrorType.WRONG_TILE);
            return;
        }

        board.pickTile(requestingPlayer, chosenTile);
    }

    synchronized public void chooseCharacter(String nickname, CharacterCard card) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);

        Player currentPlayer = board.getCurrentPlayer();
        if (!isPlayerTurn(requestingPlayer, currentPlayer) || requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        if (tryPickedCard(currentPlayer, card)) {
            if (board.pickCard(requestingPlayer, card)) {
                if (board.nextRound()) {
                    sendEnd();
                }
            }
        }
    }

    synchronized public void chooseBuilding(String nickname, BuildingCard card) throws IOException {
        Player requestingPlayer = getPlayerByNickname(nickname);
        Player currentPlayer = board.getCurrentPlayer();
        
        if (!isPlayerTurn(requestingPlayer, currentPlayer) || requestingPlayer.hasNoMoves()) {
            handleError(requestingPlayer, ErrorType.NOT_YOUR_TURN);
            return;
        }

        if (tryPickedCard(currentPlayer, card)) {
            if (!card.canBeBought(currentPlayer)) {
                handleError(requestingPlayer, ErrorType.NOT_ENOUGH_FOOD);
                return;
            }
            if (board.pickCard(requestingPlayer, card))
                if (board.nextRound())
                    sendEnd();
        }
    }

    //OTHER METHODS:

    private boolean isPlayerTurn(Player requestingPlayer, Player currentPlayer) throws IOException {
        if(currentPlayer == null) return false;
        if(requestingPlayer.equals(currentPlayer)) return true;
        return false;
    }

    private boolean tryPickedCard(Player requestingPlayer, CharacterCard card) throws IOException {
        if (requestingPlayer.hasEnoughUpMoves() && cardIsInRow(board.getUpperRow(), card)) {
            requestingPlayer.decreaseRemainingUpMoves();
            return true;
        }
        else if (requestingPlayer.hasEnoughDownMoves() && cardIsInRow(board.getLowerRow(), card)) {
            requestingPlayer.decreaseRemainingDownMoves();
            return true;
        }
        handleError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    private boolean tryPickedCard(Player requestingPlayer, BuildingCard card) throws IOException {
        if (requestingPlayer.hasEnoughUpMoves() && cardIsInRow(board.getUpperBuildings(), card)) {
            requestingPlayer.decreaseRemainingUpMoves();
            return true;
        }
        else if (requestingPlayer.hasEnoughDownMoves() && cardIsInRow(board.getLowerBuildings(), card)) {
            requestingPlayer.decreaseRemainingDownMoves();
            return true;
        }
        handleError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    private boolean cardIsInRow(List<Card> cards, CharacterCard card) {
        return cards.contains(card);
    }

    private boolean cardIsInRow(List<BuildingCard> cards, BuildingCard card) {
        return cards.contains(card);
    }

    private void handleError(Player player, ErrorType errorType) throws IOException {
        IF_GameView connection = connections.get(player);
        if (connection != null) {
            connection.notifyError(errorType);
            reSendCurrentMove();
        }
    }

    private void sendMove(Player player, Move move) throws IOException {
        IF_GameView connection = connections.get(player);
        if (connection != null) connection.notifyTurn(player.getNickname(), move);
    }

    private void sendEnd() throws IOException {
        IF_GameView connection = null;
        for (Player p : connections.keySet()) {
            connection = connections.get(p);
            if (connection != null) connection.end();
        }
        System.out.println("[TURN LOG] game is ended");
    }

    private Player getPlayerByNickname(String nickname) {
        return connections.keySet().stream()
            .filter(p -> nickname.equals(p.getNickname())).toList().getFirst();
    }

    // Test getter for the attribute playersNumber
    int getPlayersNumber() {
        return numPlayers;
    }

    // Test getter for the attribute clients
    Map<Player, IF_GameView> getClients() {
        return connections;
    }

    private void reSendCurrentMove() throws IOException {
        sendMove(board.getCurrentPlayer(), board.getCurrentMove());
    }
}
