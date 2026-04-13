package mesos.am30.server;

import mesos.am30.GameModel.*;
import mesos.am30.common.ErrorType;
import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Controller {
    final IF_GameModel board;
    private ReadWriteLock pickLock;
    private ConcurrentHashMap<Player, IF_GameView> clientConnections;
    private Player currentPlayer;
    private int currPlayerUpMoves;
    private int currPlayerDownMoves;

    public Controller(IF_GameModel board, List<Player> players, ConcurrentHashMap<Player, IF_GameView> clientConnections) {
        this.board = board;
        this.clientConnections = clientConnections;
        this.pickLock = new ReentrantReadWriteLock();
        this.currentPlayer = null;
        this.currPlayerUpMoves = 0;
        this.currPlayerDownMoves = 0;
    }

    public void pickTile(Player requestingPlayer, Tile chosenTile) throws IOException {
        //allow all clients to check if they are the current player with no delay
        pickLock.readLock().lock();
        try {
            if (currentPlayer == null) return;
            if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;
        } finally {
            pickLock.readLock().unlock(); //required finally block to handle returns.
        }
        //if player X has clicked twice, the following handles it
        pickLock.writeLock().lock();
        try {
            if (currentPlayer == null) return;
            if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;
            board.pickTile(requestingPlayer, chosenTile);
        } finally {
            pickLock.writeLock().unlock();
        }
    }

    public void pickCard(Player requestingPlayer, CharacterCard card) throws IOException {
        IF_GameView connection = clientConnections.get(requestingPlayer);

        pickLock.readLock().lock();
        try {
            if (currentPlayer == null) return;
            if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;
        } finally {
            pickLock.readLock().unlock();
        }
        //if player X has clicked twice, the following handles it
        pickLock.writeLock().lock();
        try {
            if (currentPlayer == null) return;
            if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;

            if (canPlayerPick(board.getUpperRow(), card, currPlayerUpMoves)) {
                board.pickCard(requestingPlayer, card);
                currPlayerUpMoves--;
            } else if (canPlayerPick(board.getLowerRow(), card, currPlayerDownMoves)) {
                board.pickCard(requestingPlayer, card);
                currPlayerDownMoves--;
            } else {
                if (connection != null) {
                    connection.notifyError(ErrorType.WRONG_CARD);
                }
            }
        } finally {
            pickLock.writeLock().unlock();
        }

    }

    private boolean canPlayerPick(List<Card> cards, CharacterCard card, int moves) {
        if (cards.contains(card) && moves > 0) return true;
        return false;
    }

    private boolean isPlayerTurn(Player requestingPlayer, Player currentPlayer) throws IOException {
        IF_GameView connection = clientConnections.get(requestingPlayer);
        if (!requestingPlayer.equals(currentPlayer)) {
            if (connection != null) {
                connection.notifyError(ErrorType.NOT_YOUR_TURN);
            }
            return false;
        }
        return true;
    }

    public void nextPlayer(Player player, int upMoves, int downMoves) {
        pickLock.writeLock().lock();
        try {
            this.currentPlayer = player;
            this.currPlayerUpMoves = upMoves;
            this.currPlayerDownMoves = downMoves;
        } finally {
            pickLock.writeLock().unlock();
        }
    }
}
