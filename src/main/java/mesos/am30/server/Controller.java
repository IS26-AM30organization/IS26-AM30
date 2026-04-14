package mesos.am30.server;

import mesos.am30.GameModel.*;
import mesos.am30.common.ErrorType;
import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    final IF_GameModel board;

    public Controller(IF_GameModel board) {
        this.board = board;
    }

    synchronized public void startGame() throws IOException {
        board.prepare();
        board.start();
    }

    synchronized public void pickTile(Player requestingPlayer, Tile chosenTile) throws IOException {
        if (!Utility.isTilePhase(board)) return;
        if (!isPlayerTurn(requestingPlayer, board.getCurrentPlayer())) return;
        board.pickTile(requestingPlayer, chosenTile);
    }

    synchronized public void pickCard(Player requestingPlayer, CharacterCard card) throws IOException {
        if (!Utility.isPickPhase(board)) return;

        Player currentPlayer = board.getCurrentPlayer();
        if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;

        if (tryPickedCard(currentPlayer, card)) {
            board.pickCard(requestingPlayer, card);

            if (currentPlayer.hasNoMoves()) board.endPlayerTurn();
        }
    }

    //OTHER METHODS:

    private boolean isPlayerTurn(Player requestingPlayer, Player currentPlayer) throws IOException {
        IF_GameView connection = board.getPlayerView(requestingPlayer);

        if(currentPlayer == null) return false;
        if(requestingPlayer.equals(currentPlayer)) return true;
        if(connection == null) return false;
        connection.notifyError(ErrorType.NOT_YOUR_TURN);
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
        sendError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    private boolean cardIsInRow(List<Card> cards, CharacterCard card) {
        return cards.contains(card);
    }

    private void sendError(Player player, ErrorType errorType) throws IOException {
        IF_GameView connection = board.getPlayerView(player);
        if (connection != null) connection.notifyError(errorType);
    }

}
