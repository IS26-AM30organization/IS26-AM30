package mesos.am30.server;

import mesos.am30.GameModel.*;
import mesos.am30.common.ErrorType;
import mesos.am30.view.IF_GameView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    private IF_GameModel board;
    private Map<IF_GameView, Player> connections;
    private int numPlayers;

    public Controller(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    synchronized protected void startTest(IF_GameModel board) throws IOException {
        this.board = board;
        this.board.prepare();
        this.board.start();
    }

    boolean connect(IF_GameView gameView, String nickname) throws IOException {
        connections.put(gameView, new Player(nickname));
        if (connections.size()==numPlayers) return true;
        else return false;
    }

    synchronized public void startGame() throws IOException {
        board = new Board(
                connections.values().stream().toList(),
                connections.keySet().stream().toList()
        );
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
            if (board.pickCard(requestingPlayer, card))
                if (board.nextRound())
                    return; //HERE LOGIC TO END GAME
        }
    }

    synchronized public void pickCard(Player requestingPlayer, BuildingCard card) throws IOException {
        if (!Utility.isPickPhase(board)) return;

        IF_GameView connection = board.getPlayerView(requestingPlayer);

        Player currentPlayer = board.getCurrentPlayer();
        if (!isPlayerTurn(requestingPlayer, currentPlayer)) return;

        if (tryPickedCard(currentPlayer, card)) {
            if (currentPlayer.getParameters().get(Parameter.FOOD)+currentPlayer.getParameters().get(Parameter.BUILDER)>card.getFoodCost())
                connection.notifyError(ErrorType.NOT_ENOUGH_FOOD);
            if (board.pickCard(requestingPlayer, card))
                if (board.nextRound())
                    return; //HERE LOGIC TO END GAME
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

    private boolean tryPickedCard(Player requestingPlayer, BuildingCard card) throws IOException {
        if (requestingPlayer.hasEnoughUpMoves() && cardIsInRow(board.getUpperBuildings(), card)) {
            requestingPlayer.decreaseRemainingUpMoves();
            return true;
        }
        else if (requestingPlayer.hasEnoughDownMoves() && cardIsInRow(board.getLowerBuildings(), card)) {
            requestingPlayer.decreaseRemainingDownMoves();
            return true;
        }
        sendError(requestingPlayer, ErrorType.WRONG_CARD);
        return false;
    }

    private boolean cardIsInRow(List<Card> cards, CharacterCard card) {
        return cards.contains(card);
    }

    private boolean cardIsInRow(List<BuildingCard> cards, BuildingCard card) {
        return cards.contains(card);
    }

    private void sendError(Player player, ErrorType errorType) throws IOException {
        IF_GameView connection = board.getPlayerView(player);
        if (connection != null) connection.notifyError(errorType);
    }

}
