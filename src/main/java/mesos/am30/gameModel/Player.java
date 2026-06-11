package mesos.am30.gameModel;

import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.Card;
import mesos.am30.gameModel.card.CharacterCard;

import java.io.Serializable;
import java.util.*;

/**
 * Representation of a Player.
 * <br/>This class works as the representation for a Player, storing all its cards and parameters.
 * <br/>each Player is identified by a unique nickname, and has a selection of moves it can make.
 */
public class Player implements Serializable {
    private final String nickname;

    private int remainingUpMoves;
    private int remainingDownMoves;

    private final Map<Parameter, Integer> parameters;
    private final Map<Parameter, List<CharacterCard>> tribe;
    private Set<Integer> inventions;
    private final List<BuildingCard> buildings;
    private final Set<SpecialBuff> specialBuffs;

    /**
     * Constructor of a Player.
     * <br/><strong>Pre:</strong> nickname != null
     * <br/><strong>Post:</strong> this.nickname = nickname && (* Other attributes are initialized *)
     *
     * @param nickname Nickname of the Player.
     */
    public Player(String nickname) {
        this.nickname = nickname;
        this.parameters = new HashMap<>();
        this.tribe = new HashMap<>();
        this.inventions = new HashSet<>(10);
        this.buildings = new ArrayList<>();
        this.specialBuffs = new HashSet<>();

        //Population both parameters and tribe Maps with default value (0) for each key
        for (Parameter role : Parameter.values()) {
            parameters.put(role, 0);
            if (role != Parameter.PRESTIGE_POINTS && role != Parameter.FOOD) {
                tribe.put(role, new ArrayList<>());
            }
        }
    }

    /**
     * Getter for the attribute "nickname".
     *
     * @return Unique Nickname of the Player.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Getter for the attribute "parameters".
     *
     * @return Map of Player's values for each parameter.
     */
    public Map<Parameter, Integer> getParameters() {
        return parameters;
    }

    /**
     * Getter for the attribute "tribe".
     *
     * @return Map of Player's Character Card for each role.
     */
    public Map<Parameter, List<CharacterCard>> getTribe() {
        return tribe;
    }

    /**
     * Getter for the attribute "inventions".
     *
     * @return Set of inventions obtained by the Player.
     */
    public Set<Integer> getInventions() {
        return inventions;
    }

    // Test setter as null for the attribute inventions
    void setNullInventions() {
        this.inventions = null;
    }

    /**
     * Getter for the attribute "buildings".
     *
     * @return List of Player's Building Cards.
     */
    public List<BuildingCard> getBuildings() {
        return buildings;
    }

    /**
     * Getter for the attribute "specialBuffs".
     *
     * @return Set of specialBuffs gained by the Player.
     */
    public Set<SpecialBuff> getSpecialBuffs() {
        return specialBuffs;
    }

    // Test getter for the attribute remainingUpMoves
    int getRemainingUpMoves() {
        return remainingUpMoves;
    }

    // Test getter for the attribute remainingDownMoves
    public int getRemainingDownMoves() {
        return remainingDownMoves;
    }

    /**
     * Set the Player's Moves.
     * <br/>This method sets the Moves the Player can do next, depending on the Tile it has chosen.
     * <br/><strong>Pre:</strong> up >= 0 && down >= 0
     * <br/><strong>Post:</strong> this.remainingUpMoves = up && this.remainingDownMoves = down
     *
     * @param up    Number of Cards the Player can pick from the upper row.
     * @param down  Number of Cards the Player can pick from the lower row.
     */
    public void setMoves(int up, int down) {
        setUpMoves(up);
        setDownMoves(down);
    }

    /**
     * Set the Player's Move (Pick From Up).
     * <br/><strong>Pre:</strong> up >= 0
     * <br/><strong>Post:</strong> this.remainingUpMoves = up
     *
     * @param up Number of Cards the Player can pick from the upper row.
     */
    public void setUpMoves(int up) {
        this.remainingUpMoves = up;
    }

    /**
     * Set the Player's Move (Pick From Down).
     * <br/><strong>Pre:</strong> down >= 0
     * <br/><strong>Post:</strong> this.remainingDownMoves = down
     *
     * @param down Number of Cards the Player can pick from the lower row.
     */
    public void setDownMoves(int down) {
        this.remainingDownMoves = down;
    }

    /**
     * Decrease the number of Moves (Pick From Up).
     * <br/><strong>Post:</strong> this.remainingUpMoves = /old(thius.remainingUpMoves) - 1
     */
    public void decreaseRemainingUpMoves() {
        remainingUpMoves--;
    }

    /**
     * Decrease the number of Moves (Pick From Down).
     * <br/><strong>Post:</strong> this.remainingDownMoves = /old(thius.remainingDownMoves) - 1
     */
    public void decreaseRemainingDownMoves() {
        remainingDownMoves--;
    }

    /**
     * Check if the Player has valid Moves.
     *
     * @return True if the Player has some Moves left, false otherwise.
     */
    public boolean hasNoMoves() {
        return this.remainingUpMoves == 0 && this.remainingDownMoves == 0;
    }

    /**
     * Check if the Player has valid Moves (Pick From Up).
     *
     * @return True if the Player has some Moves left, false otherwise.
     */
    public boolean hasEnoughUpMoves() {
        return this.remainingUpMoves > 0;
    }

    /**
     * Check if the Player has valid Moves (Pick From Down).
     *
     * @return True if the Player has some Moves left, false otherwise.
     */
    public boolean hasEnoughDownMoves() {
        return this.remainingDownMoves > 0;
    }

    /**
     * Add a Character to the Players' tribe.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Character Card to add.
     */
    public void addCharacter (CharacterCard card){
        tribe.get(card.getRole()).add(card);
        updateStats(card.getRole(),card.getValue());
        updateStats(Parameter.PRESTIGE_POINTS,card.getPrestigePoints());
    }

    /**
     * Get all the Cards of a specific Character.
     *
     * @param characterType Character from which get the Cards.
     *
     * @return List of Cards of the given Character.
     */
    public List<CharacterCard> getCharacterType(Parameter characterType){
        return tribe.getOrDefault(characterType, new ArrayList<>());
    }

    /**
     * Add a Building to the Players' tribe.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card Building Card to add.
     */
    public void addBuilding(BuildingCard card){
        buildings.add(card);
        updateStats(
                Parameter.FOOD, card.getFoodCost()>-parameters.get(Parameter.BUILDER) ?
                        -parameters.get(Parameter.BUILDER)-card.getFoodCost()
                        : 0);
    }

    /**
     * Update Player's stats.
     * <br/>This method updates the Player's stats for a specific parameter.
     * <br/>It handles correctly various edge cases, like not having enough food (avoiding getting a negative value).
     * <br/><strong>Pre:</strong> stat != null
     * <br/><strong>Post:</strong> this.getParameters().get(stat) = /old(this.getParameters().get(stat)) + sum
     *
     * @param stat  Parameter to update.
     * @param sum   Value to sum to the old value.
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
            if (stat == Parameter.FOOD) updateStats(Parameter.PRESTIGE_POINTS, 2*updatedValue);
            if (stat != Parameter.PRESTIGE_POINTS && stat != Parameter.BUILDER && stat != Parameter.GATHERER) updatedValue = 0;
        }

        this.parameters.put(stat, updatedValue);
    }

    /**
     * Update Player's stats (specialBuffs specific).
     * <br/>This method updates the Player's stats for the specialBuffs, adding the right buff due to the right Event.
     * <br/><strong>Pre:</strong> eventBuff != null
     * <br/><strong>Post:</strong> this.getSpecialBuffs().get(eventBuff) != null
     *
     * @param eventBuff SpecialBuff to add due to an Event.
     */
    public void updateStats(SpecialBuff eventBuff){
        specialBuffs.add(eventBuff);
    }

    /**
     * Remove a SpecialBuff.
     * <br/>This method removes a specialBuff from the Player's stats.
     * <br/><strong>Pre:</strong> eventBuff != null
     * <br/><strong>Post:</strong> this.getSpecialBuffs().get(eventBuff) == null
     *
     * @param specialBuff SpecialBuff to remove.
     */
    public void removeBuff(SpecialBuff specialBuff) {
        specialBuffs.remove(specialBuff);
    }

    /**
     * Update Player's stats with Characters end game prestige points.
     */
    public void lastRoundPoints() {
        updateStats(Parameter.PRESTIGE_POINTS,(tribe.get(Parameter.ARTIST).size()/2)*10);
        for(CharacterCard card : tribe.get(Parameter.BUILDER))
            updateStats(Parameter.PRESTIGE_POINTS, card.getPrestigePoints());
        for(BuildingCard card : buildings)
            updateStats(Parameter.PRESTIGE_POINTS, card.getPpGain());
        updateStats(Parameter.PRESTIGE_POINTS,tribe.get(Parameter.INVENTOR).size()*parameters.get(Parameter.INVENTOR));
    }

    /**
     * Add multiple Card's info to the StringBuilders for the Terminal.
     * <br/>This method works by adding the Card's info to the StringBuilders, in order to display it properly on the Terminal.
     * <br/>It displays the information for each Card passed as an attribute.
     * <br/><strong>Pre:</strong> cards != null
     *
     * @param cards Collection of Card related to the Player (Tribe or Buildings).
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

    /**
     * Display correctly the Tribe on the Terminal.
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
     * Display correctly the Player's stats on the Terminal.
     */
    public void displayStats() {
        int food = parameters.get(Parameter.FOOD);
        int prestigePoints = parameters.get(Parameter.PRESTIGE_POINTS);
        System.out.printf("\033[31m" + "Food: %d, " + "\033[0m" + "\033[33m" + "pPoints: %d\n" + "\033[0m", food, prestigePoints);
        if (inventions != null) System.out.printf("Inventions: %s", inventions);
    }

    // Object Methods Override

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(nickname, player.nickname);

    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
