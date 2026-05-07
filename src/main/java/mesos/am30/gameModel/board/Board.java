package mesos.am30.gameModel.board;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.*;
import mesos.am30.gameModel.eventIF.Sustenance;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.client.IF_GameView;

public class Board implements IF_GameModel {

    private List<IF_GameView> views;

    private List<Tile> usedTiles;
    private List<List<Card>> decks;
    private List<List<BuildingCard>> buildingDecks;

    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;

    private final List<Player> players;
    private List<Player> playersOrder;

    private int[] tileBoost;

    private GameManager game;

    //constructor
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

    //board setup
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
            case 0,1 -> new long[]{0, 0, 0};
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

    //first round
    public void start(){
        upperBuildings = buildingDecks.getFirst();
        buildingDecks.removeFirst();
        for(int i = 0; i < players.size()+1; i=lowerRow.size()) {
            if (!decks.isEmpty() && !decks.getFirst().isEmpty()) {
                Card card = decks.getFirst().getFirst();
                card.drawDown(this);
                decks.getFirst().removeFirst();
            }
        }
        for(int i=upperRow.size(); i < players.size()+4; i++) {
            if (!decks.isEmpty() && !decks.getFirst().isEmpty()) {
                decks.getFirst().getFirst().drawUp(this);
                decks.getFirst().removeFirst();
            }
        }

        Card toMoveLast = null;
        for(Card card : upperRow) {
            if((card instanceof EventCard)&&(((EventCard) card).getEvent() instanceof Sustenance)) {
                toMoveLast = card;
                break;
            }
        }
        if (toMoveLast!=null){
            upperRow.remove(toMoveLast);
            upperRow.add(toMoveLast);
        }

        try {
            game.iChangedTurn();
        } catch (IOException e) {
            System.err.println(("[ERROR]: error on fist update" + e.getMessage()));
            e.printStackTrace();
        }
    }


    // NEXT ROUND METHODS:

    private void moveDown(){
        lowerRow.clear();
        lowerRow.addAll(upperRow);
        upperRow.clear();
    }

    public void discard (Card card){
        if(upperRow.contains(card)){
            upperRow.remove(card);
        } else if (lowerRow.contains(card)){
            lowerRow.remove(card);
        }
    }

    // draws until the completion of upperRow or the end of current era's deck
    private void drawUpperRow(){
        for (int i = upperRow.size(); i < players.size()+4; i++){
            if (!decks.isEmpty()&&!decks.getFirst().isEmpty()) {
                decks.getFirst().getFirst().drawUp(this);
                decks.getFirst().removeFirst();
            }
        }
    }

    private void handleBuildings(Player player, EventType type){
        for (BuildingCard building : player.getBuildings()){
            if (building.getEventType()==type) building.getEvent().handleEvent(player);
        }
    }

    protected void scanTiles() throws IOException {
        playersOrder.clear();
        //scanning new players order, tiles get resetted
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> {
                playersOrder.add(x);

                //boost based on new playersOrder
                x.updateStats(Parameter.FOOD,tileBoost[playersOrder.size()-1]);
                if (tileBoost[playersOrder.size()-1]>0 && x.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_FOOD_TILE)) {
                    x.updateStats(Parameter.FOOD, 1);
                    x.removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
                }

                //player's moves update
                x.setMoves(t.getUpArrows(), t.getDownArrows());
            });

            //tile reset:
            t.clearCurrentPlayer();

        }
    }

    /**
     * starts new era: moves and draws buildings,
     * unlocks in draw() the new era's characters and events
     */
    private void nextEra(){
        lowerBuildings.clear();
        lowerBuildings.addAll(upperBuildings);
        upperBuildings.clear();
        if (!buildingDecks.isEmpty()) {
            upperBuildings.addAll(buildingDecks.getFirst());
            buildingDecks.removeFirst();
        }
        if(!decks.isEmpty()) {
            decks.removeFirst();
        }
    }

    //NEXTROUND

    /**
     * Does everything to change round.
     * Returns true if it changed era.
     * @return true if nextEra
     */
    public boolean nextRound() throws IOException {
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
                return true;
            }
            else {
                drawUpperRow();
            }
        }

        game.iChangedTurn();
        return false;
    }

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
    }

    //player's actions:

    /**
     * If the character is found on the board, it's drawn and added to the player's tribe
     * @param player who picks
     * @param card character picked
     */
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
     * If the building is found on the board, the player pays for it, it gets drawn and added to the player's buildings.
     *
     * @param player who picks
     * @param card   character picked
     * @return
     */
    public boolean pickCard(Player player, BuildingCard card) throws IOException {
        if (upperBuildings.contains(card)) {
            upperBuildings.remove(card);
            game.updateEveryone(ViewParameter.UPPER_ROW, upperRow);
        } else if ((lowerBuildings.contains(card))) {
            lowerBuildings.remove(card);
            game.updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
        } else return false;
        player.addBuilding(card);
        return game.iPickedCard(player);
    }

    public void pickTile(Player player, Tile tile) throws IOException {
            tile.setCurrentPlayer(player);
            game.iPickedTile(player);
    }

    public void testGame(GameManager game) { this.game = game; }

    public void drawUp (Card card){
        upperRow.add(card);
    }

    public void drawDown(Card card){
        lowerRow.add(card);
    }

    public Player getCurrentPlayer() { return playersOrder.getFirst(); }

    public List<Tile> getTiles() { return usedTiles; }

    public List<List<Card>> getDecks() { return decks; }

    public List<Card> getUpperRow() { return upperRow; }

    public List<BuildingCard> getUpperBuildings() { return upperBuildings; }

    public List<Card> getLowerRow() { return lowerRow; }

    public List<BuildingCard> getLowerBuildings() { return lowerBuildings; }

    public List<Player> getPlayersOrder() { return playersOrder; }

    public Move getCurrentMove() {
        return game.getCurrentMove();
    }
}