package mesos.am30.GameModel;

import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.client.IF_GameView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    private Board board;
    private List<Player> playersOrder; //direct pointer to board's playersOrder
    private List<IF_GameView> views;
    private List<Player> players;
    private Move currentMove;

    public GameManager(Board board, List<Player> players, List<IF_GameView> views) {
        this.board = board;
        this.playersOrder = board.getPlayersOrder();
        this.views = views;
        this.players = players;
        this.currentMove = Move.PICK_TILE;
    }

    protected boolean iPickedCard(Player player) throws IOException {

        updateEveryone(ViewParameter.PLAYERS, players);

        if (playersOrder.getFirst().hasNoMoves()){
            playersOrder.remove(player);
            playersOrder.add(player);
        }

        player = playersOrder.getFirst();

        while(!player.hasNoMoves() && !anyChoosableCard(player)) {
                playersOrder.remove(player);

                if(player.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_UP_TILE)) {
                    player.setMoves(1,0);
                    player.removeBuff(SpecialBuff.ADDITIONAL_UP_TILE);
                    playersOrder.add(player);
                    List<Player> tempPlayers = playersOrder;
                    for (Player p : tempPlayers){
                        if (p.hasNoMoves()){
                            playersOrder.remove(p);
                            playersOrder.add(p);
                        }
                    }
                }
                playersOrder.add(player);
                player = playersOrder.getFirst();
        }

        if(playersOrder.getFirst().hasNoMoves())
            return true;
        else
            notifyEveryone(playersOrder.getFirst(), whereDoIPickCards(player));
        return false;
    }

    protected void iPickedTile(Player player) throws IOException {
        playersOrder.remove(player);
        updateEveryone(ViewParameter.TILES, board.getTiles());

        if (playersOrder.isEmpty()){
            board.scanTiles();
            notifyEveryone(playersOrder.getFirst(), whereDoIPickCards(player));
        } else {
            notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
        }
    }

    protected void iChangedTurn() throws IOException {
        updateEveryone(ViewParameter.TILES, board.getTiles());
        updateEveryone(ViewParameter.PLAYERS, players);
        updateEveryone(ViewParameter.UPPER_ROW, board.getUpperRow());
        updateEveryone(ViewParameter.LOWER_ROW, board.getLowerRow());
        updateEveryone(ViewParameter.LOWER_BUILDINGS, board.getLowerBuildings());
        updateEveryone(ViewParameter.UPPER_BUILDINGS, board.getUpperBuildings());
        notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
    }

    protected boolean anyChoosableCard(Player player){
        if (player.hasEnoughUpMoves() &&
                anyCharacterLeft(board.getUpperRow())) {
            return true;
        }
        if (player.hasEnoughDownMoves() &&
                anyCharacterLeft(board.getLowerRow())) {
            return true;
        }

        player.setUpMoves(0);
        player.setDownMoves(0);
        return false;
    }

    protected boolean anyCharacterLeft(List<Card> cards) {
        for (Card card : cards)
            if (card.isPickable()) return true;
        return false;
    }

    protected Move whereDoIPickCards(Player player) {
        Move move = null;
        if (playersOrder.getFirst().hasNoMoves()){
            move = Move.PICK_TILE;
        } else if (
                playersOrder.getFirst().hasEnoughUpMoves() &&
                        playersOrder.getFirst().hasEnoughDownMoves()
        ){
            move = Move.PICK_ANY_CARD;
        } else if (playersOrder.getFirst().hasEnoughUpMoves()) {
            move = Move.PICK_FROM_UP;
        } else if (playersOrder.getFirst().hasEnoughDownMoves()) {
            move = Move.PICK_FROM_DOWN;
        }
        return move;
    }

    protected void notifyEveryone(Player player, Move move) throws IOException {
        System.out.println("[TURN LOG] currentPlayer is: " + player.getNickname() + " | Move: " + move);
        setCurrentMove(move);

        for (IF_GameView view : views) {
            view.notifyTurn(player.getNickname(), move);
        }
    }

    protected void updateEveryone(ViewParameter where, List<?> what) throws IOException {
        List<Object> parameters = new ArrayList<>(what);

        for (IF_GameView view : views) {
            view.update(where, parameters);
        }
    }

    private void setCurrentMove(Move currentMove) {
        this.currentMove = currentMove;
    }

    public Move getCurrentMove() {
        return currentMove;
    }
}




