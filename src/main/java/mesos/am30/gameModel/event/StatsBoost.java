package mesos.am30.gameModel.event;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

/**
 * Event "Stats Boost" from the Building Cards.
 * <br/>This Class Represents the Event "Stats Boost", which is a type of Building Card.
 * <br/>During a specific Event, the player gains a boost in food and Prestige Points, depending on how many Characters of a given role he has.
 */
public class StatsBoost implements IF_Event {
    private final EventType type;
    private final int food;
    private final Integer prestigePoints;
    private final Parameter role;
    private int alreadyDiscounted = 0;

    /**
     * Constructor for the Building Event "Stats Boost".
     * <br/><strong>Pre:</strong> food >= 0 &amp;&amp; prestigePoints >= 0 &amp;&amp; role != null &amp;&amp; type != null
     * <br/><strong>Post:</strong> this.food = food &amp;&amp; this.prestigePoints = prestigePoints &amp;&amp; this.tribeType= role &amp;&amp; this.type = type
     *
     * @param food              Food gained.
     * @param prestigePoints    Prestige Points gained.
     * @param role              Character which determines the boots.
     * @param type              Type of the Event which activates the boost.
     */
    public StatsBoost(int food, int prestigePoints, Parameter role, EventType type) {
        this.food = food;
        this.prestigePoints = prestigePoints;
        this.role = role;
        this.type = type;
    }

    // Test getter for the attribute "food"
    int getFood() {
        return food;
    }

    // Test getter for the attribute "prestigePoints"
    int getPrestigePoints() {
        return prestigePoints;
    }

    // Test getter for the attribute "role"
    Parameter getRole() {
        return role;
    }

    // Test getter for the attribute "type"
    EventType getType() {
        return type;
    }

    /**
     * Handles the Building Event "Stats Boost".
     * <br/>This method handles the Building Event "Stats Boost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null &amp;&amp; board.players.contains(player)
     * <br/><strong>Post:</strong> player.parameters(FOOD) = \old(player.parameters(FOOD)) + food * player.getCharacterType(tribeType).size() &amp;&amp;
     *      player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) + prestigePoints * player.getCharacterType(tribeType).size()
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        //player's parameters contains 'discounted' statistics; if counted for a previous Event, only additional character must be considered
        int multiplier = player.getCharacterType(role).size() - alreadyDiscounted;
        alreadyDiscounted = alreadyDiscounted + multiplier;

        if (food!=0 && multiplier!=0) player.updateStats(Parameter.FOOD, food*multiplier);
        if (prestigePoints!=0 && multiplier!=0) player.updateStats(Parameter.PRESTIGE_POINTS, prestigePoints*multiplier);
    }

    /**
     * @see IF_Event Stats Boost implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("EventBonus");
        str2.append("Event:").append(type);
        str3.append("food:").append(food).append(" Role:").append(role);
    }

    /**
     * @see IF_Event Stats Boost implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(food).append(" food and ").append(prestigePoints)
                .append(" pP for each ").append(role).append(" in owner's tribe, during ").append(type)
                .append(" event.").toString();
    }

    /**
     * @see IF_Event Stats Boost implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return role.name().toLowerCase().charAt(0) + "s";
    }
}
