package mesos.am30.GameModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import mesos.am30.common.Move;
import mesos.am30.common.ViewParameter;
import mesos.am30.view.IF_GameView;

public class Board implements IF_GameModel {

    private List<IF_GameView> views;

    private final List<Tile> usedTiles;
    private List<List<Card>> decks;
    private List<List<BuildingCard>> buildingDecks;

    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;

    private final List<Player> players;
    private List<Player> playersOrder;

    private int[] tileBoost;

    //constructor
    public Board(List<Player> players, List<IF_GameView> views) {
        this.players = players;
        playersOrder = new ArrayList<>();
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
        playersOrder.addAll(players);
        Collections.shuffle(playersOrder);

        int playerNum = players.size();

        Type charcType = new TypeToken<CharacterCard>(){}.getType();
        Type eventType = new TypeToken<EventCard>(){}.getType();

        List<CharacterCard> requiredCharacters = Utility.cardLoader("characters.json", playerNum, charcType);
        List<EventCard> requiredEvents = Utility.cardLoader("events.json", playerNum, eventType);
        List<EventCard> finalEvents = Utility.cardLoader("finals.json", playerNum, eventType);

        List<Card> fullDeck = new ArrayList<>();
        fullDeck.addAll(requiredCharacters);
        fullDeck.addAll(requiredEvents);
        fullDeck.addAll(finalEvents);

        Type tileType = new  TypeToken<Tile>(){}.getType();
        List<Tile> usedTiles = Utility.cardLoader("tiles.json", playerNum, tileType);


        Type buildingType = new  TypeToken<BuildingCard>(){}.getType();
        List<BuildingCard> requiredBuildings = Utility.cardLoader("buildings.json", playerNum, buildingType);

        //Creating Deck + Shuffling
        List<List<Card>> createDecks = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
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
    }


    // NEXT ROUND METHODS:

    private void moveDown(){
        lowerRow.clear();
        lowerRow.addAll(upperRow);
        upperRow.clear();
    }

    protected void drawUp (Card card){
        upperRow.add(card);
    }

    protected void drawDown(Card card){
        lowerRow.add(card);
    }

    protected void discard (Card card){
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

    //event handler -- NO USAGES AFTER LAST UPDATE
    private void handleBoardEvent(EventCard card){
        for (Player player : players) {
            card.getEvent().handleEvent(player);
        }
        if (lowerRow.contains(card)) lowerRow.remove(card);
        else if (upperRow.contains(card)) upperRow.remove(card);
    }

    private void handleBuildings(Player player, EventType type){
        for (BuildingCard building : player.getBuildings()){
            if (building.getEventType()==type) building.getEvent().handleEvent(player);
        }
    }

    /*
    private void handleEvent(BuildingCard card, Player player){
        card.getEvent().handleEvent(player);
    }
     */

    private void scanTiles() throws IOException {
        playersOrder.clear();
        //scanning new players order, tiles get resetted
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> {
                playersOrder.add(x);

                //boost based on new playersOrder
                x.updateStats(Parameter.FOOD,tileBoost[playersOrder.size()]);
                if (tileBoost[playersOrder.size()-1]>0 && x.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_FOOD_TILE)) {
                    x.updateStats(Parameter.FOOD, 1);
                    x.removeBuff(SpecialBuff.ADDITIONAL_FOOD_TILE);
                }

                //player's moves update
                x.setMoves(t.getUpArrows(), t.getDownArrows());
            });

            //tile reset:
            t.clearCurrentPlayer();

            updateEveryone(ViewParameter.TILES, usedTiles);
            updateEveryone(ViewParameter.PLAYERS, players);
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
            discard(card);

        moveDown();
        drawUpperRow();

        while (upperRow.size() < players.size()+4) {
            nextEra();
            if(decks.isEmpty()) {
                updateEveryone(ViewParameter.UPPER_ROW, upperRow);
                updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
                updateEveryone(ViewParameter.LOWER_BUILDINGS, lowerBuildings);
                updateEveryone(ViewParameter.UPPER_BUILDINGS, upperBuildings);
                end();
                return true;
            }
                else {
                    drawUpperRow();
            }
        }
        updateEveryone(ViewParameter.UPPER_ROW, upperRow);
        updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
        updateEveryone(ViewParameter.LOWER_BUILDINGS, lowerBuildings);
        updateEveryone(ViewParameter.UPPER_BUILDINGS, upperBuildings);
        notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
        return false;
    }

    private void end(){
        for(Card card : lowerRow)
            discard(card);

        for(Card card : upperRow)
            discard(card);

        for (Player player : players) {
            handleBuildings(player, EventType.FINAL);
            player.updateStats(Parameter.PRESTIGE_POINTS,(player.getTribe().get(Parameter.ARTIST).size()/2)*10);
            for(CharacterCard card : player.getTribe().get(Parameter.BUILDER))
                player.updateStats(Parameter.PRESTIGE_POINTS, card.getPrestigePoints());
            for(BuildingCard card : player.getBuildings())
                player.updateStats(Parameter.PRESTIGE_POINTS, card.getPpGainEnd());
            player.updateStats(Parameter.PRESTIGE_POINTS,player.getTribe().get(Parameter.INVENTOR).size()*player.getParameters().get(Parameter.INVENTOR));
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
            updateEveryone(ViewParameter.UPPER_ROW, upperRow);
        } else if (lowerRow.contains(card)) {
            lowerRow.remove(card);
            updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
        } else return false;
            player.getTribe().get(card.getRole()).add((CharacterCard) card);

            if (card.getRole().equals(Parameter.HUNTER)){
                player.updateStats(Parameter.HUNTER,1);
                player.updateStats(Parameter.FOOD,card.getValue());
            }

            else if (card.getRole().equals(Parameter.INVENTOR)) {
                if (!player.getInventions().contains(card.getValue())) {
                    player.getInventions().add(card.getValue());
                    player.updateStats(Parameter.INVENTOR, 1);
                }
            }
            else player.updateStats(card.getRole(),card.getValue());
            updateEveryone(ViewParameter.PLAYERS, players);
            handleBuildings(player, EventType.ROUND);
            return iPickedCardWhosNext(player);
    }

    /**
     * If the building is found on the board, the player pays for it, it gets drawn and added to the player's buildings.
     *
     * @param player who picks
     * @param card   character picked
     * @return
     */
    public boolean pickCard(Player player, BuildingCard card) throws IOException {
        if (upperBuildings.contains((BuildingCard) card)) {
            upperBuildings.remove((BuildingCard) card);
            updateEveryone(ViewParameter.UPPER_ROW, upperRow);
        } else if ((lowerBuildings.contains((BuildingCard) card))) {
            lowerBuildings.remove((BuildingCard) card);
            updateEveryone(ViewParameter.LOWER_ROW, lowerRow);
        } else return false;
        player.getBuildings().add((BuildingCard) card);
        player.updateStats(Parameter.FOOD, card.getFoodCost()>player.getParameters().get(Parameter.BUILDER) ? player.getParameters().get(Parameter.BUILDER)-card.getFoodCost() : 0);
        updateEveryone(ViewParameter.PLAYERS, players);
        return iPickedCardWhosNext(player);
    }

    public void pickTile(Player player, Tile tile) throws IOException {
            tile.setCurrentPlayer(player);
            iPickedTileWhosNext(player);
            updateEveryone(ViewParameter.TILES, usedTiles);
    }

    private boolean iPickedCardWhosNext(Player player) throws IOException {
        while(anyChoosableCard(player)) {
            if (player.hasNoMoves()) {
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
            }
        }
        if(playersOrder.getFirst().hasNoMoves())
            return true;
        else
            notifyEveryone(playersOrder.getFirst(), whereDoIPickCards(player));
        return false;
    }

    private void iPickedTileWhosNext(Player player) throws IOException {
        playersOrder.remove(player);

        if (playersOrder.isEmpty()){
            scanTiles();
            notifyEveryone(playersOrder.getFirst(), whereDoIPickCards(player));
        } else {
            notifyEveryone(playersOrder.getFirst(), Move.PICK_TILE);
        }
    }

    private boolean anyChoosableCard(Player player){
        if (player.hasEnoughUpMoves() &&
                anyCharacterLeft(upperRow)) {
            player.setUpMoves(0);
            return true;
        }
        if (player.hasEnoughDownMoves() &&
                anyCharacterLeft(lowerRow)) {
            player.setDownMoves(0);
            return true;
        }
        return false;
    }

    private boolean anyCharacterLeft(List<Card> cards){
        for (Card card : cards)
            if (card.isPickacble()) return true;
        return false;
    }

    private Move whereDoIPickCards(Player player) {
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

    private void notifyEveryone (Player player, Move move) throws IOException {
        for(IF_GameView view : views){
            view.notifyTurn(player, move);
        }
    }

    private void updateEveryone(ViewParameter where, List<?> what) throws IOException {
        for(IF_GameView view : views){
            view.update(where, what);
        }
    }

    public Player getCurrentPlayer() {
        return playersOrder.getFirst();
    }

    public void endPlayerTurn() {
    }

    public IF_GameView getPlayerView(Player requestingPlayer) {
        return null;
    }


    public List<Tile> getTiles() {
        return usedTiles;
    }

    protected List<List<Card>> getDecks() {
        return decks;
    }

    public List<Card> getUpperRow() {
        return upperRow;
    }

    public List<BuildingCard> getUpperBuildings() {
        return upperBuildings;
    }

    public List<Card> getLowerRow() {
        return lowerRow;
    }

    public List<BuildingCard> getLowerBuildings() {
        return lowerBuildings;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }
}
