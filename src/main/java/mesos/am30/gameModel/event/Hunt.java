package mesos.am30.gameModel.event;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;

/**
 * Event "Hunt" from the Event Cards.
 * <br/>This Class Represents the Event "Hunt", which is a type of Event Card.
 * <br/>It gives one Food unit to all Players, then it adds extra Prestige Points depending on the Card specific Value multiplied by the number of Hunters each Player has.
 */
public class Hunt implements IF_Event {
    private final int prestigePoints;

    /**
     * Constructor for the Event "Hunt".
     * <br/><strong>Pre:</strong> prestigePoints > 0
     * <br/><strong>Post:</strong> this.prestigePoints = prestigePoints
     *
     * @param prestigePoints Prestige Points to add for each Hunter.
     */
    public Hunt(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    // Test getter for the attribute "prestigePoints".
    int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Handles the Event "Hunt".
     * <br/>This method handles the Event "Hunt" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != NULL &amp;&amp; board.players.contains(player)
     * <br/><strong>Post:</strong> player.parameters(FOOD) = \old(player.parameters(FOOD) + 1) &amp;&amp;
     *       player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) + (this.prestigePoints * player.tribe(HUNTER).size())
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        for (BuildingCard b : player.getBuildings()) {
            if (b.getEventType() == EventType.HUNT) b.getEvent().handleEvent(player);
        }
        int hunters = player.getTribe().get(Parameter.HUNTER).size();
        player.updateStats(Parameter.FOOD, 1);
        player.updateStats(Parameter.PRESTIGE_POINTS, hunters * prestigePoints);
    }

    /**
     * @see IF_Event Hunt implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Hunt");
        str2.append("pP:").append(prestigePoints);
        str3.append("Food:").append("1");
    }

    /**
     * @see IF_Event Hunt implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This is a Hunt Event Card: when resolved, the player receives 1 food and ").append(prestigePoints)
                .append(" pP for each Hunter in player's tribe.")
                .toString();
    }

    /**
     * @see IF_Event Hunt implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return prestigePoints + "h0h1";
    }
}
