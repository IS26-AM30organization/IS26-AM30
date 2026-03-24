package mesos.am30.GameModel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mesos.am30.IF_GameModel;

import static java.nio.charset.StandardCharsets.*;

public class Board implements IF_GameModel {
    private static Set<Card> allCards;
    private final Set<Card> allBuildings;
    //private Set<Card> usedCards;     //potrebbe essere inutile

    private static List<Tile> allTiles;
    private final List<Tile> usedTiles;
    private static Set<EventCard> finalEventCards;
    private List<List<Card>> decks;
    private List<List<BuildingCard>> buildingDecks;

    private List<Card> upperRow;
    private List<BuildingCard> upperBuildings;
    private List<Card> lowerRow;
    private List<BuildingCard> lowerBuildings;

    private final List<Player> players;
    private List<Player> playersOrder;

    //COSTRUTTORE
        // ATTENZIONE!! non sono previste eccezioni o controlli, richiesti al chiamante
        // (non pesca carte se giocatori<2, usa regole da 5 se giocatori>5)
    public Board(List<Player> players) {

        //perparo la lettura del json
        Gson gson = new Gson();
        Type setType = new TypeToken<Set<Player>>(){}.getType();
        Type tileType = new  TypeToken<List<Tile>>(){}.getType();

        //creo allCards (dal json)
        InputStream allcharacter = Board.class.getClassLoader().getResourceAsStream("characters.json");
        Reader reader = new InputStreamReader(allcharacter, UTF_8);
        Set<Card> cardsFromJson = gson.fromJson(reader, setType);
        InputStream allevents = Board.class.getClassLoader().getResourceAsStream("events.json");
        reader = new InputStreamReader(allevents, UTF_8);
        cardsFromJson.addAll(gson.fromJson(reader, setType));
        allCards = cardsFromJson;

        //creo allBuildings (dal json)
        InputStream allbuildings = Board.class.getClassLoader().getResourceAsStream("buildings.json");
        reader = new InputStreamReader(allbuildings, UTF_8);
        allBuildings = gson.fromJson(reader, setType);

        //creo allTiles (dal json)
        InputStream alltiles =  Board.class.getClassLoader().getResourceAsStream("tiles.json");
        reader = new InputStreamReader(alltiles, UTF_8);
        allTiles = gson.fromJson(reader, tileType);

        //setto players e playersOrder:
        this.players = players;
        this.playersOrder = new ArrayList<>();
        playersOrder.addAll(players);
        Collections.shuffle(playersOrder);

        //scelgo le tile utili
        usedTiles = allTiles.stream()
                .filter(x -> x.getPlayersMinimum() <= players.size())
                .toList();

        //mischio le carte edifici:
        List<BuildingCard> drawFromBuildings = new ArrayList<>(allBuildings.stream()
                .map(x -> (BuildingCard) x)
                .toList());
        Collections.shuffle(drawFromBuildings);

        //scelgo quanti edifici pescare:
        long[] b = switch (players.size()) {
            case 0,1 -> new long[]{0, 0, 0};
            case 2 -> new long[]{1, 2, 3};
            case 3 -> new long[]{2, 2, 4};
            case 4 -> new long[]{2, 3, 4};
            default -> new long[]{2, 3, 5};
        };

        //creo i deck di edifici:
        List<List<BuildingCard>> createBuildingDecks = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            int era = i;
            List<BuildingCard> buildings = drawFromBuildings.stream()
                    .filter(x -> x.getEra() == era)
                    .limit(b[i])
                    .toList();
            createBuildingDecks.add(buildings);
        }
        buildingDecks = createBuildingDecks;

        //mischio le carte personaggi ed eventi in base al numero di giocatori:
        List<List<Card>> createDecks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int era = i;
            List<Card> deck = new ArrayList<>(allCards.stream()
                    .filter(x -> (x instanceof CharacterCard))
                    .filter(x -> x.getEra() == era)
                    .filter(x -> x.getPlayersMinimum() == players.size())
                    .toList());
            deck.addAll(allCards.stream()
                    .filter(x -> x instanceof EventCard)
                    .filter(x -> x.getEra() == era)
                    .toList());
            Collections.shuffle(deck);
            createDecks.add(deck);
        }
        decks = createDecks;

        //PRIMO ROUND!!!
        List<Card> firstLowerRow = new ArrayList<>();
        List<Card> firstUpperRow = new ArrayList<>();
        lowerBuildings = new ArrayList<>();
        upperBuildings = buildingDecks.getFirst();
        buildingDecks.removeFirst();
        while(firstLowerRow.size() < players.size()+2) {
            Card card = decks.getFirst().getFirst();
            if(card instanceof EventCard) {
                firstUpperRow.add(card);
            } else {
                firstLowerRow.add(card);
            }
            decks.getFirst().removeFirst();
        }
        while(firstUpperRow.size() < players.size()+5) {
            firstUpperRow.add(decks.getFirst().getFirst());
            decks.getFirst().removeFirst();
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


        //creo usedCards (inutile?):
        /*Set<Card> usedCards = new HashSet<>();
        usedCards.clear();
        usedCards.addAll(decks.stream().flatMap(List::stream).toList());
        usedCards.addAll(buildingDecks.stream().flatMap(List::stream).toList());*/
    }

    // 3 METODI SEPARATI PER NEXT ROUND:
    private void discardLowerRow() {
        lowerRow.clear();
    }
    private void moveDown(){
        lowerRow.addAll(upperRow);
        upperRow.clear();
    }
    // pesca fino a completamento upperRow o a esaurimento carte dell'era corrente
    private void draw(){
        for (int i = upperRow.size(); i < players.size()+4; i++){
            if (!decks.isEmpty()&&!decks.getFirst().isEmpty()) {
                upperRow.add(decks.getFirst().getFirst());
                decks.getFirst().removeFirst();
            }
        }
    }

    //gestisco eventi (per ora privato)
    private void handleEvent(EventCard card){
        for (Player player : players) {
            card.getEvent().handleEvent(player);
        }
    }

    private void handleEvent(BuildingCard card, Player player){
        card.getEvent().handleEvent(player);
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
        //draw();
    }

    //NEXTROUND UNITO (resetta anche i tile)

    /**
     * Does everything to change round.
     * Returns true if it changed era.
     * @return true if nextEra
     */
    public boolean nextRound() {
        playersOrder.clear();
        //creo nuovo order e resetto tiles
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> {
                playersOrder.add(x);
                //BONUS DI ORDINE
            });
            t.clearCurrentPlayer();
        }
        for(Card card : lowerRow){
            if(card instanceof EventCard) {
                handleEvent((EventCard) card);
            }
        }
        discardLowerRow();
        moveDown();
        draw();
        if (upperRow.size() < players.size()+4) {
            nextEra();
            if(decks.isEmpty()){
                for(Card card : upperRow){
                    if(card instanceof EventCard) {
                        handleEvent((EventCard) card);
                    }
                }
                upperRow.clear();
                lowerRow.clear();
                upperBuildings.clear();
                lowerBuildings.clear();
            }else draw();
            return true;
        } else return false;
    }

    /*
    public Set<Card> getUsedCards() {
        return usedCards;
    }
     */

    //AZIONI GIOCATORE:

    public void pickCard(Player player, Card card) {
        //non assegna la carta se non la trova sul tavolo
        if (upperRow.contains(card)) {
            upperRow.remove(card);
        } else if (lowerRow.contains(card)) {
            lowerRow.remove(card);
        } else if ((card instanceof BuildingCard)&&(upperBuildings.contains((BuildingCard) card))) {
            upperBuildings.remove((BuildingCard) card);
        } else if ((card instanceof BuildingCard)&&(lowerBuildings.contains((BuildingCard) card))) {
            lowerBuildings.remove((BuildingCard) card);
        } else return;
        if (card instanceof CharacterCard) {
            Parameter tipo = ((CharacterCard) card).getRole();
            player.getTribe().get(tipo).add((CharacterCard) card);
        } else if (card instanceof BuildingCard) {
            player.getBuildings().add((BuildingCard) card);
        }
    }

    public void pickTile(Player player, Tile tile) {
            tile.setCurrentPlayer(player);
            playersOrder.remove(player);
    }

    public List<Tile> getTiles() {
        return usedTiles;
    }

    private List<List<Card>> getDecks() {
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

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getPlayersOrder() {
        return playersOrder;
    }
}
