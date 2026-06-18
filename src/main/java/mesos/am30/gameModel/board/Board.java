package mesos.am30.gameModel.board;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import mesos.am30.db.GameResultsDAO;
import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.*;
import mesos.am30.gameModel.event.Sustenance;
import mesos.am30.common.enumerations.Move;
import mesos.am30.common.enumerations.ViewParameter;
import mesos.am30.common.interfaces.IF_GameView;

/**
 * Representation of the Game Board.
 * <br/>This Class works as the representation of the Game Board, which defines the Model in the ModelViewController Pattern.
 */
public class Board implements IF_GameModel {
    private final List<IF_GameView> views;

    private List<Tile> usedTiles;
    private List<List<Card>> decks;
    private List<List<BuildingCard>> buildingDecks;

    private final List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private final List<Card> lowerRow;
    private final List<BuildingCard> lowerBuildings;

    private final List<Player> players;
    private final List<Player> playersOrder;

    private final int[] tileBoost;

    private GameManager game;

    /**
     * Constructor of the Board.
     * <br/><strong>Pre:</strong> players != null &amp;&amp; !players.contains(null) &amp;&amp; views != null &amp;&amp; !views.contains(null)
     * <br/><strong>Post:</strong> this.players = players &amp;&amp; this.views = views &amp;&amp; (* Other attributes are initialized *)
     *
     * @param players   List of Players for the Game.
     * @param views     List of Views associated to each Player.
     */
    public Board(List<Player> players, List<IF_GameView> views) {
        this.players = players;

        this.playersOrder = new ArrayList<>(players);
        this.views = views;

        decks = new ArrayList<>();
        buildingDecks = new ArrayList<>();
        usedTiles = new ArrayList<>();

        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();
        upperBuildings = new ArrayList<>();
        lowerBuildings = new ArrayList<>();

        tileBoost = switch(players.size()){
            case 2 -> new int[]{1,-1};
            case 3 -> new int[]{2,0,-1};
            case 4 -> new int[]{2,1,0,-1};
            case 5 -> new int[]{3,1,0,0,-1};
            default -> new int[]{};
        };
    }

    // GETTERS

    // Test getter for the attribute "decks"
    List<List<Card>> getDecks() { return decks; }

    // Test getter for the attribute "buildingDecks"
    List<List<BuildingCard>> getBuildingDecks() {
        return buildingDecks;
    }

    // Test getter for the attribute "tileBoost"
    int[] getTileBoost() { return tileBoost; }

    // Test setter for the attribute "game"
    void testGame(GameManager game) { this.game = game; }

    /**
     * @see IF_GameModel Board implementation of the getTiles method.
     */
    @Override
    public List<Tile> getTiles() { return usedTiles; }

    /**
     * @see IF_GameModel Board implementation of the getUpperRow method.
     */
    @Override
    public List<Card> getUpperRow() { return upperRow; }

    /**
     * @see IF_GameModel Board implementation of the getUpperBuildings method.
     */
    @Override
    public List<BuildingCard> getUpperBuildings() { return upperBuildings; }

    /**
     * @see IF_GameModel Board implementation of the getLowerRow method.
     */
    @Override
    public List<Card> getLowerRow() { return lowerRow; }

    /**
     * @see IF_GameModel Board implementation of the getLowerBuildings method.
     */
    @Override
    public List<BuildingCard> getLowerBuildings() { return lowerBuildings; }

    /**
     * @see IF_GameModel Board implementation of the getPlayersOrder method.
     */
    @Override
    public List<Player> getPlayersOrder() { return playersOrder; }

    /**
     * @see IF_GameModel Board implementation of the getCurrentPlayer method.
     */
    @Override
    public Player getCurrentPlayer() { return playersOrder.getFirst(); }

    /**
     * @see IF_GameModel Board implementation of the getCurrentMove method.
     */
    @Override
    public Move getCurrentMove() {
        return game.getCurrentMove();
    }

    // STARTING METHODS

    /**
     * @see IF_GameModel Board implementation of the prepare method.
     */
    @Override
    public void prepare() throws IOException {
        //first playersOrder
        game = new GameManager(this, players, views);

        int playerNum = players.size();

        Type charcType = new TypeToken<CharacterCard>(){}.getType();
        Type eventType = new TypeToken<EventCard>(){}.getType();

        List<CharacterCard> requiredCharacters = Utility.cardLoader("characters.json", playerNum, charcType);
        List<EventCard> requiredEvents = Utility.cardLoader("events.json", playerNum, eventType);

        List<Card> fullDeck = new ArrayList<>();
        fullDeck.addAll(requiredCharacters);
        fullDeck.addAll(requiredEvents);

        Type tileType = new  TypeToken<Tile>(){}.getType();
        usedTiles = Utility.cardLoader("tiles.json", playerNum, tileType);

        Type buildingType = new  TypeToken<BuildingCard>(){}.getType();
        List<BuildingCard> requiredBuildings = Utility.cardLoader("buildings.json", playerNum, buildingType);

        //Creating Deck + Shuffling
        List<List<Card>> createDecks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int era = i +1;
            List<Card> deck = new ArrayList<>(fullDeck.stream()
                    .filter(x -> x.getEra() == era)
                    .toList()); //returns immutable List, no good to draw cards
            Collections.shuffle(deck);
            createDecks.add(deck);
        }
        decks = createDecks;

        //defining #Buildings based on playerNum
        long[] b = switch (playerNum) {
            case 2 -> new long[]{1, 2, 3};
            case 3 -> new long[]{2, 2, 4};
            case 4 -> new long[]{2, 3, 4};
            default -> new long[]{2, 3, 5};
        };

        //Creating Buildings + Shuffling
        Collections.shuffle(requiredBuildings);
        List<List<BuildingCard>> createBuildingDecks = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            int era = i + 1;
            List<BuildingCard> buildings = new ArrayList<>(requiredBuildings.stream()
                    .filter(x -> x.getEra() == era)
                    .limit(b[i])
                    .toList());
            createBuildingDecks.add(buildings);
        }
        buildingDecks = createBuildingDecks;

        Collections.shuffle(playersOrder);

    }

    /**
     * @see IF_GameModel Board implementation of the start method.
     */
    @Override
    public void start(){
        upperBuildings = buildingDecks.getFirst();
        buildingDecks.removeFirst();
        for (int i = 0; i < players.size() + 1; i = lowerRow.size()) {
            if (!decks.getFirst().isEmpty()) {
                decks.getFirst().getFirst().drawDown(this);
                decks.getFirst().removeFirst();
            } else break;
        }
        for (int i = upperRow.size(); i < players.size() + 4; i++) {
            if (!decks.getFirst().isEmpty()) {
                decks.getFirst().getFirst().drawUp(this);
                decks.getFirst().removeFirst();
            } else break;
        }

        for(Card card : upperRow) {
            if((card instanceof EventCard)&&(((EventCard) card).getEvent() instanceof Sustenance)) {
                upperRow.remove(card);
                upperRow.add(card);
                break;
            }
        }

        try {
            game.iChangedTurn();
        } catch (IOException e) {
            System.err.println(("[ERROR]: error on fist update" + e.getMessage()));
        }
    }


    // NEXT ROUND METHODS:

    private void moveDown(){
        lowerRow.clear();
        lowerRow.addAll(upperRow);
        upperRow.clear();
    }

    /**
     * Discard a Card from the correct row.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Card to discard.
     */
    public void discard(Card card){
        if(upperRow.contains(card)) upperRow.remove(card);
        else lowerRow.remove(card);
    }

    /**
     * Discard a Card from the correct row (Building Card specific).
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Card to discard.
     */
    public void discard(BuildingCard card){
        if (getLowerBuildings().contains(card)) getLowerBuildings().remove(card);
        else getUpperBuildings().remove(card);
    }

    // draws until the completion of upperRow or the end of current era's deck
    private void drawUpperRow(){
        for (int i = upperRow.size(); i < players.size()+4; i++){
            if (!decks.getFirst().isEmpty()) {
                decks.getFirst().getFirst().drawUp(this);
                decks.getFirst().removeFirst();
            } else break;
        }
    }

    // handle all buildings with a given event type
    private void handleBuildings(Player player, EventType type){
        for (BuildingCard building : player.getBuildings()){
            if (building.getEventType()==type) building.getEvent().handleEvent(player);
        }
    }

    // scan the Tiles for getting PlayersOrder
    protected void scanTiles() {
        playersOrder.clear();
        //scanning new players order, tiles get reset
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> {
                playersOrder.add(x);

                //player's moves update
                x.setMoves(t.getUpArrows(), t.getDownArrows());
            });

            //tile reset:
            t.clearCurrentPlayer();

        }
    }

    // starts new era: moves and draws buildings, unlocks in draw() the new era's characters and events
    private void nextEra(){
        lowerBuildings.clear();
        lowerBuildings.addAll(upperBuildings);
        upperBuildings.clear();
        if (!buildingDecks.isEmpty()) {
            upperBuildings.addAll(buildingDecks.getFirst());
            buildingDecks.removeFirst();
        }
        decks.removeFirst();
    }

    // NEXT ROUND

    /**
     * @see IF_GameModel Board implementation of the nextRound method.
     */
    @Override
    public void nextRound() throws IOException {
        if (playersOrder.isEmpty()) scanTiles();
        for (Player player : players) {
            handleBuildings(player,EventType.ONETIME);
        }

        ArrayList<Card> tempLower = new ArrayList<>(lowerRow);
        for(Card card : tempLower)
            card.discard(this);

        moveDown();
        drawUpperRow();

        if (upperRow.size() < players.size()+4) {
            nextEra();
            System.out.println("[GAME LOG] changed era");
            if(decks.isEmpty()) {
                System.out.println("[GAME LOG] deck is empty");
                end();
                game.sendClientEnd();
                return;
            }
            else {
                drawUpperRow();
            }
        }
        int i=0;
        for(Player p : playersOrder){
            //boost based on new playersOrder
            p.updateStats(Parameter.FOOD,tileBoost[i]);
            if (tileBoost[i]>0 && p.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_FOOD_TILE)) {
                p.updateStats(Parameter.FOOD, 1);
                p.removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
            }
            i++;
        }

        game.iChangedTurn();
    }

    // end of the Game
    private void end(){
        List<Card> tempLower = new ArrayList<>(lowerRow);
        for(Card card : tempLower)
            card.discard(this);

        List<Card> tempUpper = new ArrayList<>(upperRow);
        for(Card card : tempUpper)
            card.discard(this);

        for (Player player : players) {
            handleBuildings(player, EventType.FINAL);
            player.lastRoundPoints();
        }

        upperRow.clear();
        lowerRow.clear();
        upperBuildings.clear();
        lowerBuildings.clear();

        // update the DB
        try {
            List<Map<String, String>> results = new ArrayList<>();
            for (Player player : players) {
                Map<String, String> playerResult = new LinkedHashMap<>(2);
                playerResult.put("Nickname", player.getNickname());
                playerResult.put("Score", String.valueOf(player.getParameters().get(Parameter.PRESTIGE_POINTS)));
                results.add(playerResult);
            }
            GameResultsDAO.addNewResults(results);
            System.out.println("[GAME LOG] successful DB update");
        } catch (IOException | SQLException exception) {
            System.out.println("[GAME LOG] failed DB update");
        }
    }

    // player's actions:

    /**
     * @see IF_GameModel Board implementation of the pickCard method (Character Card).
     */
    @Override
    public boolean pickCard(Player player, CharacterCard card) throws IOException {
        if (upperRow.contains(card)) {
            upperRow.remove(card);
            game.updateEveryone(ViewParameter.UPPER_ROW, upperRow);
        } else if (lowerRow.contains(card)) {
            lowerRow.remove(card);
            game.updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
        } else return false;

        player.addCharacter(card);
        handleBuildings(player, EventType.ROUND);
        return game.iPickedCard(player);
    }

    /**
     * @see IF_GameModel Board implementation of the pickCard method (Building Card).
     */
    @Override
    public boolean pickCard(Player player, BuildingCard card) throws IOException {
        if (upperBuildings.contains(card)) {
            upperBuildings.remove(card);
            game.updateEveryone(ViewParameter.UPPER_BUILDINGS, upperBuildings);
        } else if ((lowerBuildings.contains(card))) {
            lowerBuildings.remove(card);
            game.updateEveryone(ViewParameter.LOWER_BUILDINGS, lowerBuildings);
        } else return false;
        player.addBuilding(card);
        return game.iPickedCard(player);
    }

    /**
     * @see IF_GameModel Board implementation of the pickTile method.
     */
    @Override
    public void pickTile(Player player, Tile tile) throws IOException {
        tile.setCurrentPlayer(player);
        game.iPickedTile(player);
    }

    /**
     * Draw a Card and put it in the upper row.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Card drawn.
     */
    public void drawUp (Card card){
        upperRow.add(card);
    }

    /**
     * Draw a Card and put it in the lower row.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Card drawn.
     */
    public void drawDown(Card card){
        lowerRow.add(card);
    }
}