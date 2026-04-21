package mesos.am30.view;

import mesos.am30.GameModel.*;
import mesos.am30.common.Choice;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

/**
 * View implementation Client-Side.
 * <br>This Class represents the View: it works with different communication protocols, depending on the implementation.
 * <br>It works as a first line of control against illegal moves, before sending them to the Controller.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public abstract class VirtualView implements IF_GameView {
    protected final IF_GameUI userInterface;
    protected final ViewModel model;
    protected String nickname = "stillToConnect";   // avoid null value messages

    /**
     * Constructor for virtualView.
     * <br><strong>Pre:</strong> userInterface != null
     * <br><strong>Post:</strong> this.userInterface == userInterface && this.model = new ViewModel()
     *
     * @param userInterface User Interface to use
     */
    public VirtualView(IF_GameUI userInterface) {
        this.userInterface = userInterface;
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

    /**
     * Open the connection to the Server.
     * <br>This method establishes the communication between this View and the Server, first connecting to it, then notify that it wants to connect
     * via the Server method server.handleConnection(view).
     * <br><strong>Pre:</strong> path != null
     *
     * @param path URL of the Server
     * @param port Port opened by the Server
     */
    public abstract void findServer(String path, int port) throws IOException;

    // invoke Controller methods (polymorphic)
    protected abstract void toController(Choice choice, Object parameter) throws IOException;

    /**
     * Check if the move is valid (pick a Tile).
     * <br>This method is called by the Client when picking a Tile; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Tile
     * @throws IOException The connection cannot be established correctly
     */
    public synchronized void checkTile(Tile choice) throws IOException {
        // check if right turn
        if (model.getCurrentUser().getNickname().equals(nickname) && model.getCurrentMove() == Move.PICK_TILE) {
            List<Tile> tiles = model.getTiles();
            // check if valid Tile
            if (tiles.contains(choice) && choice.getCurrentPlayer().isEmpty()) {
                model.setDefault();
                toController(Choice.CHOOSE_TILE, choice);
            } else notifyError(ErrorType.WRONG_TILE);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    /**
     * Check if the move is valid (pick a Character Card).
     * <br>This method is called by the Client when picking a Character Card; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Character Card
     * @throws IOException The connection cannot be established correctly
     */
    public synchronized void checkCharacterCard(CharacterCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_CARD || currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
        )) {
            // check if valid Character
            List<Card> row = getCharacterCards(currentMove);
            if (row.contains(choice)) {
                model.setDefault();
                toController(Choice.CHOOSE_CHARACTER, choice);
            } else notifyError(ErrorType.WRONG_CARD);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    // get the correct Character Cards
    private List<Card> getCharacterCards(Move currentMove) {
        List<Card> row = null;
        switch (currentMove) {
            // picked Character from upper/lower row
            case PICK_CARD -> {
                List<Card> upperRow = model.getUpperRow().stream()
                        .filter(card -> card instanceof CharacterCard)
                        .toList();
                List<Card> lowerRow = model.getLowerRow().stream()
                        .filter(card -> card instanceof CharacterCard)
                        .toList();
                row = new ArrayList<>(upperRow);
                row.addAll(lowerRow);
            }
            // picked Character from upper row
            case PICK_FROM_UP -> row = model.getUpperRow().stream()
                    .filter(card -> card instanceof CharacterCard)
                    .toList();
            // picked Character from lower row
            case PICK_FROM_DOWN -> row = model.getLowerRow().stream()
                    .filter(card -> card instanceof CharacterCard)
                    .toList();
        }
        return row;
    }

    /**
     * Check if the move is valid (pick a Building Card).
     * <br>This method is called by the Client when picking a Building Card; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Building Card
     * @throws IOException The connection cannot be established correctly
     */
    public synchronized void checkBuildingCard(BuildingCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_CARD || currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
        )) {
            // check if valid Building
            List<BuildingCard> row = getBuildingCards(currentMove);
            if (row.contains(choice)) {
                if (model.getCurrentUser().getParameters().get(Parameter.FOOD) >= choice.getFoodCost()) {
                    model.setDefault();
                    toController(Choice.CHOOSE_BUILDING, choice);
                } else notifyError(ErrorType.NOT_ENOUGH_FOOD);
            } else notifyError(ErrorType.WRONG_CARD);
        } else notifyError(ErrorType.NOT_YOUR_TURN);
    }

    // get the correct Building Cards
    private List<BuildingCard> getBuildingCards(Move currentMove) {
        List<BuildingCard> row = null;
        switch (currentMove) {
            // picked Building from upper/lower row
            case PICK_CARD -> {
                List<BuildingCard> upperBuildings = model.getUpperBuildings();
                List<BuildingCard> lowerBuildings = model.getLowerBuildings();
                row = new ArrayList<>(upperBuildings);
                row.addAll(lowerBuildings);
            }
            // picked Building from upper row
            case PICK_FROM_UP -> row = model.getUpperBuildings();
            // picked Building from lower row
            case PICK_FROM_DOWN -> row = model.getLowerBuildings();
        }
        return row;
    }

    /**
     * @see IF_GameView Implementation Client-side asking the View
     */
    @Override
    public synchronized void askPlayersNumber() throws IOException {
        int playersNumber = userInterface.askPlayersNumber();
        toController(Choice.PLAYERS_NUMBER, playersNumber);
    }

    /**
     * @see IF_GameView Implementation Client-side asking the View
     */
    @Override
    public synchronized void askNickname() throws IOException {
        nickname = userInterface.askNickname();
        toController(Choice.NICKNAME, nickname);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the notifyTurn method
     */
    @Override
    public synchronized void notifyTurn(String nickname, Move move) throws IOException {
        model.setCurrentUser(nickname);
        model.setCurrentMove(move);
        userInterface.printMove(nickname, move);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the notifyError method
     */
    @Override
    public synchronized void notifyError(ErrorType errorType) throws IOException {
        userInterface.printError(errorType);
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
        userInterface.refresh(model);
    }
}
