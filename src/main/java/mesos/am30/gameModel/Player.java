package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;

import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    private final String nickname;

    private int remainingUpMoves;
    private int remainingDownMoves;

    private final Map<Parameter, Integer> parameters; /**Contains each parameter amount*/
    private final Map<Parameter, List<CharacterCard>> tribe; /**Contains character parameter + list of that type*/
    private final Set<Integer> inventions; /**Contains set of player's inventions*/
    private final Set<BuildingCard> buildings;
    private final Set<SpecialBuff> specialBuffs;

    public Player(String nickname) {
        this.nickname = nickname;
        this.parameters = new HashMap<>();
        this.tribe = new HashMap<>();
        this.inventions = new HashSet<>(10);
        this.buildings = new HashSet<>();
        this.specialBuffs = new HashSet<>();

        //Population both parameters and tribe Maps with default value (0) for each key
        for (Parameter role : Parameter.values()) {
            parameters.put(role, 0);
            if (role != Parameter.PRESTIGE_POINTS && role != Parameter.FOOD) {
                tribe.put(role, new ArrayList<>());
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * Adds a building to the player's tribe
     */
    public void addBuilding(BuildingCard card){
        buildings.add(card);
        updateStats(
                Parameter.FOOD, card.getFoodCost()>parameters.get(Parameter.BUILDER) ?
                        parameters.get(Parameter.BUILDER)-card.getFoodCost()
                        : 0);
    }

    /**
     * Adds a character to the player's tribe, based on the character role
     */
    public void addCharacter (CharacterCard card){
        List<CharacterCard> currentTribe = tribe.computeIfAbsent(card.getRole(), k -> new ArrayList<>());
        tribe.get(card.getRole()).add(card);
        updateStats(card.getRole(),card.getValue());
    }

    public Map<Parameter, Integer> getParameters() {
        return parameters;
    }

    public Map<Parameter, List<CharacterCard>> getTribe() {
        return tribe;
    }

    public Set<Integer> getInventions() {
        return inventions;
    }

    public Set<BuildingCard> getBuildings() {
        return buildings;
    }

    public Set<SpecialBuff> getSpecialBuffs() {
        return specialBuffs;
    }

    /**
     * Method used to update player's stats when needed; mostly used when events are triggered and stats updates are due
     * @param stat type of stat to be updated
     * @param sum update amount
     */
    public void updateStats(Parameter stat, int sum) {
        //using getOrDefault default method of HashMap -> if no value is present, returns defaultValue.
        int currentValue = this.parameters.getOrDefault(stat, 0);

        if (stat.equals(Parameter.INVENTOR)) {
            if (!inventions.contains(sum)) {
                inventions.add(sum);
                sum = 1;
            } else sum = 0;
        }

        if (stat.equals(Parameter.HUNTER)){
            updateStats(Parameter.FOOD,sum);
            sum = 1;
        }

        int updatedValue = currentValue + sum;
        if(updatedValue < 0) {
            if (stat == Parameter.FOOD) updateStats(Parameter.PRESTIGE_POINTS, -2*updatedValue);
            if (stat != Parameter.PRESTIGE_POINTS) updatedValue = 0;
        }

        this.parameters.put(stat, updatedValue);
    }

    public void lastRoundPoints() {
        updateStats(Parameter.PRESTIGE_POINTS,(tribe.get(Parameter.ARTIST).size()/2)*10);
        for(CharacterCard card : tribe.get(Parameter.BUILDER))
            updateStats(Parameter.PRESTIGE_POINTS, card.getPrestigePoints());
        for(BuildingCard card : buildings)
            updateStats(Parameter.PRESTIGE_POINTS, card.getPpGainEnd());
        updateStats(Parameter.PRESTIGE_POINTS,tribe.get(Parameter.INVENTOR).size()*parameters.get(Parameter.INVENTOR));
    }

    public void updateStats(SpecialBuff eventBuff){
        specialBuffs.add(eventBuff);
    }

    public void removeBuff(SpecialBuff specialBuff) {
        specialBuffs.remove(specialBuff);
    }

    public List<CharacterCard> getCharacterType(Parameter characterType){
        return tribe.getOrDefault(characterType, new ArrayList<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; //same mem address
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        //return Objects.equals(nickname, player.nickname) && Objects.equals(parameters, player.parameters) && Objects.equals(tribe, player.tribe) && Objects.equals(inventions, player.inventions) && Objects.equals(buildings, player.buildings) && Objects.equals(specialBuffs, player.specialBuffs);
        return Objects.equals(nickname, player.nickname);

    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    public void decreaseRemainingUpMoves() {
        remainingUpMoves--;
    }

    public void decreaseRemainingDownMoves() {
        remainingDownMoves--;
    }

    public void setMoves(int up, int down) {
        setUpMoves(up);
        setDownMoves(down);
    }

    public void setUpMoves(int up) {
        this.remainingUpMoves = up;
    }

    public void setDownMoves(int down) {
        this.remainingDownMoves = down;
    }

    public boolean hasNoMoves() {
        return this.remainingUpMoves == 0 && this.remainingDownMoves == 0;
    }

    public boolean hasEnoughUpMoves() {
        return this.remainingUpMoves > 0;
    }

    public boolean hasEnoughDownMoves() {
        return this.remainingDownMoves > 0;
    }

    /**
     *Invoked buy TUI to display player's tribe
     */
    public void displayTribe() {
        List<Card> allCharacters = new ArrayList<>();
        for (List<CharacterCard> roles : tribe.values()) {
            allCharacters.addAll(roles);
        }

        createRows(allCharacters);
        createRows(buildings);
    }

    /**
     * It prints the player's food, pPoints and Unique Inventions
     */
    public void displayStats() {
        int food = parameters.get(Parameter.FOOD);
        int prestigePoints = parameters.get(Parameter.PRESTIGE_POINTS);
        System.out.printf("\033[31m" + "Food: %d, " + "\033[0m" + "\033[33m" + "pPoints: %d\n" + "\033[0m", food, prestigePoints);
        if (inventions != null) System.out.printf("Inventions: %s", inventions.toString());
    }

    /**
     * Takes a Collection of Cards an adds their info to the corresponding StringBuilder
     * @param cards either player's tribe or player's building;
     */
    private void createRows(Collection<? extends Card> cards) {
        if (cards.isEmpty()) return;

        int i = 0;
        int maxCardsXRow = 8;

        StringBuilder rowRoles = new StringBuilder();
        StringBuilder rowValue = new StringBuilder();
        StringBuilder rowPP = new StringBuilder();

        for (Card card : cards) {
            card.createRow(rowRoles, rowValue, rowPP);
            i++;

            if (i == maxCardsXRow) {
                System.out.println(rowRoles);
                System.out.println(rowValue);
                System.out.println(rowPP);
                System.out.println();

                rowRoles.setLength(0);
                rowValue.setLength(0);
                rowPP.setLength(0);
                i = 0;
            }
        }

        if (i > 0) {
            System.out.println(rowRoles);
            System.out.println(rowValue);
            System.out.println(rowPP);
            System.out.println();
        }
    }
}
