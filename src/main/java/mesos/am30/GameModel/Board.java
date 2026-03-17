package mesos.am30.GameModel;

import java.util.*;

public class Board {
    private static Set<Card> allCards;
    private Set<Card> usedCards;     //potrebbe essere inutile

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

        //setto players e playersOrder:
        this.players = players;
        this.playersOrder = players;
        Collections.shuffle(playersOrder);

        //scelgo le tile utili
        usedTiles = allTiles.stream()
                .filter(x -> x.getPlayersMinimum() <= players.size())
                .toList();

        //mischio le carte edifici:
        List<BuildingCard> drawFromBuildings = new ArrayList<>(allCards.stream()
                .filter(x -> x instanceof BuildingCard)
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
        for (int i = 0; i < b.length; i++) {
            int era = i;
            List<BuildingCard> buildings = drawFromBuildings.stream()
                    .filter(x -> x.getEra() == era)
                    .limit(b[i])
                    .toList();
            buildingDecks.add(buildings);
        }

        //mischio le carte personaggi in base al numero di giocatori:
        for (int i = 0; i < 3; i++) {
            int era = i;
            List<Card> deck = new ArrayList<>(allCards.stream()
                    .filter(x -> x instanceof CharacterCard)
                    .filter(x -> x.getEra() == era)
                    .filter(x -> x.getPlayersMinimum() == players.size())
                    .toList());
            Collections.shuffle(deck);
            decks.add(deck);
        }

        //creo usedCards (inutile?):
        usedCards.clear();
        usedCards.addAll(decks.stream().flatMap(List::stream).toList());
        usedCards.addAll(buildingDecks.stream().flatMap(List::stream).toList());
    }

    // 3 METODI SEPARATI PER NEXT ROUND:
    public void discardLowerRow() {
        lowerRow.clear();
    }
    public void moveDown(){
        lowerRow.addAll(upperRow);
        upperRow.clear();
    }
    // pesca fino a completamento upperRow o a esaurimento carte dell'era corrente
    public void draw(){
        for (int i = upperRow.size(); i < players.size()+4; i++){
            if (!decks.isEmpty()&&!decks.get(1).isEmpty()) {
                upperRow.add(decks.get(1).get(1));
                decks.get(1).remove(1);
            }
        }
    }

    //chiama la nuova era: sposta e pesca gli edifici,
    //sblocca in draw() i personaggi dell'era successiva
    public void nextEra(){
        lowerBuildings.clear();
        lowerBuildings.addAll(upperBuildings);
        upperBuildings.clear();
        upperBuildings.addAll(buildingDecks.get(1));
        buildingDecks.remove(1);
        decks.remove(1);
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
        for(Tile t : usedTiles){
            t.getCurrentPlayer().ifPresent(x -> playersOrder.add(x));
            t.clearCurrentPlayer();
        }
        discardLowerRow();
        moveDown();
        draw();
        if (upperRow.size() < players.size()+4) {
            nextEra();
            draw();
            return true;
        } else return false;
    }

    public Set<Card> getUsedCards() {
        return usedCards;
    }

    //AZIONI GIOCATORE:

    public void pickCard(Player player, Card card) {
        //non assegna la carta se non la trova sul tavolo
        if (upperRow.contains(card)) {
            upperRow.remove(card);
        } else if (lowerRow.contains(card)) {
            lowerRow.remove(card);
        } else if (upperBuildings.contains((BuildingCard) card)) {
            upperBuildings.remove((BuildingCard) card);
        } else if (lowerBuildings.contains((BuildingCard) card)) {
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

    public List<Tile> getUsedTiles() {
        return usedTiles;
    }

    public List<List<Card>> getDecks() {
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
