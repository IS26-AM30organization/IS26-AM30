package mesos.am30.gameModel.board;

import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.client.IF_GameView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Utility GameManager for the Model.
class GameManager {
    private final Board board;
    private final List<Player> playersOrder; //direct pointer to board's playersOrder
    private final List<IF_GameView> views;
    private final List<Player> players;
    private Move currentMove;

    // constructor for the GameManager
    protected GameManager(Board board, List<Player> players, List<IF_GameView> views) {
        this.board = board;
        this.playersOrder = board.getPlayersOrder();
        this.views = views;
        this.players = players;
        this.currentMove = Move.PICK_TILE;
    }

    // Getter for the attribute "currentMove"
    protected Move getCurrentMove() {
        return currentMove;
    }

    // get the next Move
    protected Move whereDoIPickCards() {
        Move move;
        if (playersOrder.getFirst().hasNoMoves()){
            move = Move.PICK_TILE;
        } else if (
                playersOrder.getFirst().hasEnoughUpMoves() &&
                        playersOrder.getFirst().hasEnoughDownMoves()
        ){
            move = Move.PICK_ANY_CARD;
        } else if (playersOrder.getFirst().hasEnoughUpMoves()) {
            move = Move.PICK_FROM_UP;
        } else {
            move = Move.PICK_FROM_DOWN;
        }
        return move;
    }

    // check if there is any Card to pick
    protected boolean anyChoosableCard(Player player){
        if (player.hasEnoughUpMoves() &&
                anyCharacterLeft(board.getUpperRow())) {
            return true;
        }
        if (player.hasEnoughDownMoves() &&
                anyCharacterLeft(board.getLowerRow())) {
            return true;
        }
        if (player.hasEnoughUpMoves() &&
                board.getUpperBuildings().stream().anyMatch(b -> b.canBeBought(player))) {
            return true;
        }
        if (player.hasEnoughDownMoves() &&
                board.getLowerBuildings().stream().anyMatch(b -> b.canBeBought(player))) {
            return true;
        }

        player.setMoves(0, 0);
        return false;
    }

    // check if is left any CharacterCard
    protected boolean anyCharacterLeft(List<Card> cards) {
        for (Card card : cards)
            if (card.isPickable()) return true;
        return false;
    }

    // handle the Move PICK_TILE
    protected void iPickedTile(Player player) throws IOException {
        playersOrder.remove(player);
        updateEveryone(ViewParameter.TILES, board.getTiles());

        if (playersOrder.isEmpty()){
            board.scanTiles();
            notifyEveryone(playersOrder.getFirst(), whereDoIPickCards());
        } else {
            notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
        }
    }

    // handle the Move PICK_CARD
    protected boolean iPickedCard(Player player) throws IOException {
        // update Players
        updateEveryone(ViewParameter.PLAYERS, players);
        if (playersOrder.getFirst().hasNoMoves()){
            playersOrder.remove(player);

            if(player.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_UP_TILE)) {
                player.setMoves(1,0);
                player.removeBuff(SpecialBuff.ADDITIONAL_UP_TILE);
                insertPlayerAddMove(playersOrder, player);
            } else playersOrder.add(player);
        }
        player = playersOrder.getFirst();

        // check if there are left Moves
        while(!player.hasNoMoves() && !anyChoosableCard(player)) {
            playersOrder.remove(player);
            playersOrder.add(player);
            player = playersOrder.getFirst();
        }
        if(playersOrder.getFirst().hasNoMoves()) return true;
        notifyEveryone(playersOrder.getFirst(), whereDoIPickCards());
        return false;
    }

    // insert the player as last with Moves
    private void insertPlayerAddMove(List<Player> playersOrder, Player playerAdd) {
        for (int i = 0; i < playersOrder.size(); i++) {
            if (playersOrder.get(i).hasNoMoves()) {
                playersOrder.add(i, playerAdd);
                return;
            }
        }
        playersOrder.add(playersOrder.size(), playerAdd);
    }

    // handle the change of Turn
    protected void iChangedTurn() throws IOException {
        updateState();
        notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
    }

    // notify every Player about the next Move
    protected void notifyEveryone(Player player, Move move) throws IOException {
        System.out.println("[TURN LOG] currentPlayer is: " + player.getNickname() + " | Move: " + move);
        this.currentMove = move;

        for (IF_GameView view : views) {
            view.notifyTurn(player.getNickname(), move);
        }
    }

    // update every Player's ViewModel after a Move
    protected void updateEveryone(ViewParameter where, List<?> what) throws IOException {
        List<Object> parameters = new ArrayList<>(what);

        for (IF_GameView view : views) {
            view.update(where, parameters);
        }
    }

    // handle the update of the Model
    protected void updateState() throws IOException{
        updateEveryone(ViewParameter.TILES, board.getTiles());
        updateEveryone(ViewParameter.PLAYERS, players);
        updateEveryone(ViewParameter.UPPER_ROW, board.getUpperRow());
        updateEveryone(ViewParameter.LOWER_ROW, board.getLowerRow());
        updateEveryone(ViewParameter.LOWER_BUILDINGS, board.getLowerBuildings());
        updateEveryone(ViewParameter.UPPER_BUILDINGS, board.getUpperBuildings());
    }

    // handle the End notification
    protected void sendClientEnd() throws IOException {
        updateState();
        for (IF_GameView view : views) {
            view.askShowRankings();
        }
        System.out.println("[TURN LOG] game is ended");
    }
}



