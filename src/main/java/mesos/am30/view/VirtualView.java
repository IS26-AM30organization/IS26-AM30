package mesos.am30.view;

import mesos.am30.GameModel.*;
import mesos.am30.common.Choice;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class VirtualView implements IF_GameView {
    protected final ViewModel model;
    protected String nickname = "stillToConnect";   // avoid null value messages

    public VirtualView() {
        model = new ViewModel();
    }

    // Test getter for the attribute model
    ViewModel getModel() {
        return model;
    }

    // Test getter for the attribute nickname
    String getNickname() {
        return nickname;
    }

    // Test setter for the attribute nickname
    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public abstract void findServer(String path, int port) throws IOException;
    protected abstract void toController(Choice choice, Object parameter) throws IOException;

    public synchronized void checkTile(Tile choice) throws IOException {
        // check if right turn
        if (model.getCurrentUser().getNickname().equals(nickname) && model.getCurrentMove() == Move.PICK_TILE) {
            List<Tile> tiles = model.getTiles();
            // check if valid Tile
            if (tiles.contains(choice) && choice.getCurrentPlayer().isEmpty()) {
                model.setCurrentUser(null);
                model.setCurrentMove(null);
                toController(Choice.CHOOSE_TILE, choice);
            } else notifyError(ErrorType.WRONG_TILE);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    public synchronized void checkCharacterCard(CharacterCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
        )) {
            List<Card> row = null;
            switch (currentMove) {
                // picked character from upper row
                case PICK_FROM_UP -> row = model.getUpperRow().stream()
                        .filter(card -> card instanceof CharacterCard)
                        .toList();
                // picked character from lower row
                case PICK_FROM_DOWN -> row = model.getLowerRow().stream()
                        .filter(card -> card instanceof CharacterCard)
                        .toList();
            }
            // check if valid Character
            if (row.contains(choice)) {
                model.setCurrentUser(null);
                model.setCurrentMove(null);
                toController(Choice.CHOOSE_CHARACTER, choice);
            } else notifyError(ErrorType.WRONG_CARD);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    public synchronized void checkBuildingCard(BuildingCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
        )) {
            List<BuildingCard> row = null;
            switch (currentMove) {
                // picked character from upper row
                case PICK_FROM_UP -> row = model.getUpperBuildings();
                // picked character from lower row
                case PICK_FROM_DOWN -> row = model.getLowerBuildings();
            }
            // check if valid Building
            if (row.contains(choice)) {
                if (model.getCurrentUser().getParameters().get(Parameter.FOOD) >= choice.getFoodCost()) {
                    model.setCurrentUser(null);
                    model.setCurrentMove(null);
                    toController(Choice.CHOOSE_BUILDING, choice);
                } else notifyError(ErrorType.NOT_ENOUGH_FOOD);
            } else notifyError(ErrorType.WRONG_CARD);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    /**
     * @see IF_GameView Implementation Client-side asking the View
     */
    @Override
    public void askPlayersNumber() throws IOException {
        int playersNumber = 0; // ----- ui.askPlayersNumber();
        toController(Choice.PLAYERS_NUMBER, playersNumber);
    }

    /**
     * @see IF_GameView Implementation Client-side asking the View
     */
    @Override
    public void askNickname() throws IOException {
        nickname = ""; // ----- ui.askNickname
        toController(Choice.NICKNAME, nickname);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the notifyTurn method
     */
    @Override
    public synchronized void notifyTurn(String nickname, Move move) throws IOException {
        model.setCurrentUser(nickname);
        model.setCurrentMove(move);
        // UI ------ notify Move
    }

    /**
     * @see IF_GameView Implementation Client-Side of the notifyError method
     */
    @Override
    public void notifyError(ErrorType errorType) throws IOException {
        // UI ------ invalid move
    }

    /**
     * @see IF_GameView Implementation Client-Side of the update method
     */
    @Override
    public synchronized void update(ViewParameter toUpdate, List<Object> parameters) throws IOException {
        switch (toUpdate) {
            // update the Players
            case PLAYERS -> {
                List<Player> players = new ArrayList<>(parameters.size());
                for (Object p: parameters) players.add((Player) p);
                model.setPlayers(players);
            }
            // update the Tiles
            case TILES -> {
                List<Tile> tiles = new ArrayList<>(parameters.size());
                for (Object p : parameters) tiles.add((Tile) p);
                model.setTiles(tiles);
            }
            // update the Upper Row
            case UPPER_ROW -> {
                List<Card> upperRow = new ArrayList<>(parameters.size());
                for (Object p: parameters) upperRow.add((Card) p);
                model.setUpperRow(upperRow);
            }
            // update the Upper Buildings
            case UPPER_BUILDINGS -> {
                List<BuildingCard> upperBuildings = new ArrayList<>(parameters.size());
                for (Object p : parameters) upperBuildings.add((BuildingCard) p);
                model.setUpperBuildings(upperBuildings);
            }
            // update the Lower Row
            case LOWER_ROW  -> {
                List<Card> lowerRow = new ArrayList<>(parameters.size());
                for (Object p: parameters) lowerRow.add((Card) p);
                model.setLowerRow(lowerRow);
            }
            // update the Lower Buildings
            case LOWER_BUILDINGS -> {
                List<BuildingCard> lowerBuildings = new ArrayList<>(parameters.size());
                for (Object p : parameters) lowerBuildings.add((BuildingCard) p);
                model.setLowerBuildings(lowerBuildings);
            }
        }
        // ------------- UI update
    }
}
