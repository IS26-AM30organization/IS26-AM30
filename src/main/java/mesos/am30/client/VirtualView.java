package mesos.am30.client;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.Tile;
import mesos.am30.common.Choice;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * View implementation Client-Side.
 * <br/>This Class represents the View: it works with different communication protocols, depending on the implementation.
 * <br/>It works as a first line of control against illegal moves, before sending them to the Controller.
 */
public abstract class VirtualView implements IF_GameView {
    protected final IF_GameUI userInterface;
    protected final ViewModel model;
    protected String nickname = "stillToConnect";   // avoid null value messages
    protected String lobbyCode = "";
    protected int playersNumber = 0;

    /**
     * Constructor for virtualView.
     * <br/><strong>Pre:</strong> userInterface != null
     * <br/><strong>Post:</strong> this.userInterface == userInterface && this.model = new ViewModel()
     *
     * @param userInterface User Interface to use.
     */
    public VirtualView(IF_GameUI userInterface) {
        this.userInterface = userInterface;
        model = new ViewModel();
        this.userInterface.setVModel(model);
        this.userInterface.setVView(this);
    }

    // Test getter for the attribute "model"
    ViewModel getModel() {
        return model;
    }

    // Test getter for the attribute "nickname"
    String getNickname() {
        return nickname;
    }

    /** Getter for the attribute "lobbyCode".
     *
     * @return Saved code of the Lobby.
     */
    public String getLobbyCode() {
        return lobbyCode;
    }

    // Test setter for the attribute "nickname"
    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Open the connection to the Server.
     * <br/>This method establishes the communication between this View and the Server, first connecting to it, then notify that it wants to connect
     * via the Server method server.handleConnection(view).
     * <br/><strong>Pre:</strong> path != null
     *
     * @param path URL of the Server.
     * @param port Port opened by the Server.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public abstract void findServer(String path, int port) throws IOException;

    // invoke Server methods (polymorphic)
    protected abstract void toServer(Choice choice, String lobbyCode, Object parameter) throws IOException;

    // invoke Controller methods (polymorphic)
    protected abstract void toController(Choice choice, Object parameter) throws IOException;

    /**
     * @see IF_GameView Implementation Client-Side of the confirmConnection method.
     */
    @Override
    public synchronized void confirmConnection() throws IOException {
        userInterface.confirmConnection();
    }

    /**
     * Send a request to create a Lobby.
     * <br/>This method is called by the Client in order to request the Server the creation of a new Lobby.
     * <br/><strong>Pre:</strong> lobbyCode != null
     *
     * @param playersNumber Number of Players of the Lobby.
     * @param lobbyCode     Code of the Lobby.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public void createLobby(int playersNumber, String lobbyCode) throws IOException {
        this.playersNumber = playersNumber;
        if (lobbyCode == null || lobbyCode.isEmpty()) {
            this.lobbyCode = "";
        }
        else this.lobbyCode = lobbyCodePadded(lobbyCode);

        toServer(Choice.CREATE_LOBBY, this.lobbyCode, playersNumber);
    }

    // handle correctly the code padding
    private String lobbyCodePadded (String lobbyCode) {
        String code = lobbyCode;

        if (code.length() > 6) {
            code = code.substring(0, 6);
        }
        else if (code.length() < 6) {
            code = code + "0".repeat(6 - code.length());
        }

        return code;
    }

    /**
     * Send a request to get the available Lobbies.
     * <br/>This method is called by the Client in order to request the Server to show the available Lobbies to join.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public synchronized void requestAvailableLobbies() throws IOException {
        toServer(Choice.GET_AVAILABLE_LOBBIES, null, null);
    }

    /**
     * @see IF_GameView Implementation Client-side of the showLobbies method.
     */
    @Override
    public synchronized void showLobbies(Map<String, Integer> availableLobbies) throws IOException {
        userInterface.showLobbies(availableLobbies);
    }

    /**
     * Send a request to join a Lobby.
     * <br/>This method is called by the Client in order to request the Server to join a new Lobby.
     * <br/><strong>Pre:</strong> lobbyCode != null
     *
     * @param lobbyCode Code of the Lobby.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public void joinLobby(String lobbyCode) throws IOException{
        this.lobbyCode = lobbyCodePadded(lobbyCode);
        toServer(Choice.JOIN_LOBBY, this.lobbyCode, null);
    }

    /**
     * @see IF_GameView Implementation Client-side of the askNickname method.
     */
    @Override
    public synchronized void askNickname(String lobbyCode) throws IOException {
        this.lobbyCode = lobbyCode;
        userInterface.askNickname();
    }

    /**
     * Send the Nickname for the Lobby.
     * <br/>This method is called by the Client in order to send the Server its nickname and then join the Lobby.
     * <br/><strong>Pre:</strong> nickname != null
     *
     * @param nickname Nickname the Client wants to use.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public synchronized void answerNickname(String nickname) throws IOException {
        this.nickname = nickname;
        toServer(Choice.NICKNAME, lobbyCode, nickname);
    }

    /**
     * @see IF_GameView Implementation Client-side of the confirmLobbyJoined method.
     */
    @Override
    public synchronized void confirmLobbyJoined() throws IOException {
        userInterface.confirmLobbyJoined();
    }



    /**
     * Check if the move is valid (pick a Tile).
     * <br/>This method is called by the Client when picking a Tile; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br/><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Tile.
     *
     * @throws IOException The connection cannot be established correctly.
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
     * <br/>This method is called by the Client when picking a Character Card; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br/><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Character Card.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public synchronized void checkCharacterCard(CharacterCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_ANY_CARD || currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
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
            case PICK_ANY_CARD -> {
                List<Card> upperRow = model.getUpperRow().stream()
                        .filter(Card::isPickable)
                        .toList();
                List<Card> lowerRow = model.getLowerRow().stream()
                        .filter(Card::isPickable)
                        .toList();
                row = new ArrayList<>(upperRow);
                row.addAll(lowerRow);
            }
            // picked Character from upper row
            case PICK_FROM_UP -> row = model.getUpperRow().stream()
                    .filter(Card::isPickable)
                    .toList();
            // picked Character from lower row
            case PICK_FROM_DOWN -> row = model.getLowerRow().stream()
                    .filter(Card::isPickable)
                    .toList();
        }
        return row;
    }

    /**
     * Check if the move is valid (pick a Building Card).
     * <br/>This method is called by the Client when picking a Building Card; it checks Client-side the validity of the Move, before
     * sending it to the Controller.
     * <br/><strong>Pre:</strong> choice != null
     *
     * @param choice Picked Building Card.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public synchronized void checkBuildingCard(BuildingCard choice) throws IOException {
        // check if right turn
        Move currentMove = model.getCurrentMove();
        if (model.getCurrentUser().getNickname().equals(nickname) && (
                currentMove == Move.PICK_ANY_CARD || currentMove == Move.PICK_FROM_UP || currentMove == Move.PICK_FROM_DOWN
        )) {
            // check if valid Building
            List<BuildingCard> row = getBuildingCards(currentMove);
            if (row.contains(choice)) {
                if (model.getCurrentUser().getParameters().get(Parameter.FOOD)
                        - model.getCurrentUser().getParameters().get(Parameter.BUILDER) >= choice.getFoodCost()) {
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
            case PICK_ANY_CARD -> {
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
     * @see IF_GameView Implementation Client-Side of the notifyTurn method.
     */
    @Override
    public synchronized void notifyTurn(String nickname, Move move) throws IOException {
        model.setCurrentUser(nickname);
        model.setCurrentMove(move);
        userInterface.printMove(nickname, move);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the notifyError method.
     */
    @Override
    public synchronized void notifyError(ErrorType errorType) throws IOException {
        userInterface.printError(errorType);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the update method.
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

    /**
     * @see IF_GameView Implementation Client-Side of the askShowRankings method.
     */
    @Override
    public synchronized void askShowRankings() throws IOException {
        userInterface.askShowRankings();
    }

    /**
     * Send the response about the rankings.
     * <br/>This method is called by the Client in order to tell the Controller if it wants to see the Global Rankings.
     *
     * @param response True if the Client wants to see the Global Rankings, false otherwise.
     *
     * @throws IOException The connection cannot be established correctly.
     */
    public synchronized void answerShowRankings(boolean response) throws IOException {
        toController(Choice.RANKINGS, response);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the showRankings method.
     */
    @Override
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) throws IOException {
        userInterface.showRankings(playerRank, globalRankings);
    }

    /**
     * @see IF_GameView Implementation Client-Side of the ping method.
     */
    @Override
    public void ping() throws IOException { /* heartbeat */ }
}
