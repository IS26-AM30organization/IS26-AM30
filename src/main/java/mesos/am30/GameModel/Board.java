package mesos.am30.GameModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.reflect.TypeToken;
import mesos.am30.IF_GameModel;

public class Board implements IF_GameModel {

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
    public Board(List<Player> players) {
        this.players = players;
        usedTiles = new ArrayList<>();
        decks = new ArrayList<>();
        buildingDecks = new ArrayList<>();
        playersOrder = new ArrayList<>();
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

    //board set
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
            int era = i;
            List<Card> deck = new ArrayList<>(fullDeck.stream()
                    .filter(x -> x.getEra() == era + 1)
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
            int era = i;
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
        List<Card> firstLowerRow = new ArrayList<>();
        List<Card> firstUpperRow = new ArrayList<>();
        lowerBuildings = new ArrayList<>();
        upperBuildings = buildingDecks.getFirst();
        buildingDecks.removeFirst();
        for(int i = 0; i < players.size()+1; i=firstLowerRow.size()) {
            if (!decks.isEmpty() && !decks.getFirst().isEmpty()) {
                Card card = decks.getFirst().getFirst();
                if (card instanceof EventCard) {
                    firstUpperRow.add(card);
                } else {
                    firstLowerRow.add(card);
                }
                decks.getFirst().removeFirst();
            }
        }
        for(int i=firstUpperRow.size(); i < players.size()+4; i++) {
            if (!decks.isEmpty() && !decks.getFirst().isEmpty()) {
                firstUpperRow.add(decks.getFirst().getFirst());
                decks.getFirst().removeFirst();
            }
        }
        lowerRow = firstLowerRow;
        upperRow = firstUpperRow;
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

    // draws until the completion of upperRow or the end of current era's deck
    private void draw(){
        for (int i = upperRow.size(); i < players.size()+4; i++){
            if (!decks.isEmpty()&&!decks.getFirst().isEmpty()) {
                upperRow.add(decks.getFirst().getFirst());
                decks.getFirst().removeFirst();
            }
        }
    }

    //event handler
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

    private void scanTiles(){
        playersOrder.clear();
        //scanning new players order, tiles get resetted
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> {
                playersOrder.add(x);
                if (tileBoost[playersOrder.size()]>=0 || (x.getParameters().get(Parameter.FOOD)>0))
                    x.updateStats(Parameter.FOOD,tileBoost[playersOrder.size()]);
                    else x.updateStats(Parameter.PRESTIGE_POINTS,-2);
                if (tileBoost[playersOrder.size()]>0 && x.getSpecialBuffs().contains(SpecialBuff.ADDITIONAL_FOOD_TILE)) x.updateStats(Parameter.FOOD, 1);
            });
            t.clearCurrentPlayer();
        }
    }

    //chiama la nuova era: sposta e pesca gli edifici,
    //sblocca in draw() i personaggi dell'era successiva
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
    public boolean nextRound() {
        scanTiles();

        for(Card card : lowerRow)
            if(card instanceof EventCard)
                handleBoardEvent((EventCard) card);

        moveDown();
        draw();

        if (upperRow.size() < players.size()+4) {
            nextEra();
            if(decks.isEmpty()) end();
                else draw();
            return true;
        } else return false;
    }

    private void end(){
        for(Card card : lowerRow)
            if(card instanceof EventCard)
                handleBoardEvent((EventCard) card);

        for(Card card : upperRow)
            if(card instanceof EventCard)
                handleBoardEvent((EventCard) card);

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

    //player actions:
    public void pickCard(Player player, CharacterCard card) {
        //non assegna la carta se non la trova sul tavolo
        if (upperRow.contains(card)) {
            upperRow.remove(card);
        } else if (lowerRow.contains(card)) {
            lowerRow.remove(card);
        } else return;
            player.getTribe().get(card.getRole()).add((CharacterCard) card);

            if (card.getRole().equals(Parameter.HUNTER)){
                player.updateStats(Parameter.HUNTER,1);
                player.updateStats(Parameter.FOOD,card.getValue());
            }

            else if (card.getRole().equals(Parameter.SHAMAN))
                if (!player.getInventions().contains(card.getValue())){
                    player.getInventions().add(card.getValue());
                    player.updateStats(Parameter.SHAMAN,1);
                }

            else player.updateStats(card.getRole(),card.getValue());

            handleBuildings(player, EventType.ROUND);
    }

    public void pickCard(Player player, BuildingCard card) {
        if (upperBuildings.contains((BuildingCard) card)) {
            upperBuildings.remove((BuildingCard) card);
        } else if ((lowerBuildings.contains((BuildingCard) card))) {
            lowerBuildings.remove((BuildingCard) card);
        } else return;
        player.getBuildings().add((BuildingCard) card);
        player.updateStats(Parameter.FOOD, 0-card.getFoodCost());
    }

    public void pickTile(Player player, Tile tile) {
            tile.setCurrentPlayer(player);
            playersOrder.remove(player);
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
