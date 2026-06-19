package mesos.am30.gameModel.event;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

/**
 * Event "ShamanBoost" from the Building Cards.
 * <br/>This Class Represents the Event "ShamanBoost", which is a type of Building Card.
 * <br/>Due to the type of Building Card:
 * <ul>
 *     <li>if the Player has the most amount of Stars, it does gain double the Prestige Points.</li>
 *     <li>if the Player has the least amount of Stars, it does not lose Prestige Points.</li>
 * </ul>
 */
public class ShamanBoost implements IF_Event {
    private final boolean firstOrLast;
    private int eventPrestigePoints;

    /**
     * Constructor for the Event "ShamanBoost".
     * <br/><strong>Post:</strong> this.firstOrLast = firstOrLast
     *
     * @param firstOrLast True if the Boost applies when Player is First, False when is Last.
     */
    public ShamanBoost(boolean firstOrLast) {
        this.firstOrLast = firstOrLast;
    }

    // check when the boost applies
    boolean isFirstOrLast() {
        return firstOrLast;
    }

    /**
     * Setter for the attribute "eventPrestigePoints".
     * <br/><strong>Post:</strong> this.eventPrestigePoints = eventPrestigePoints
     *
     * @param eventPrestigePoints Prestige Points of teh current "Shamanic Ritual" Event.
     */
    void setEventPrestigePoints(int eventPrestigePoints) {
        this.eventPrestigePoints = eventPrestigePoints;
    }

    /**
     * Handles the Event "ShamanBoost".
     * <br/>This method handles the Event "ShamanBoost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != NULL &amp;&amp; board.players.contains(player)
     * <br/><strong>Post:</strong> (firstOrLast ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) + eventPrestigePoints) &amp;&amp;
     *       (!firstOrLast ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) - eventPrestigePoints)
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        player.updateStats(Parameter.PRESTIGE_POINTS, (firstOrLast) ? eventPrestigePoints : -eventPrestigePoints);
    }

    /**
     * @see IF_Event Shaman Boost implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("shmnBoost");
        str3.append("pP: ").append(eventPrestigePoints);
    }

    /**
     * @see IF_Event Shaman Boost implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives a boost during the Shamanic Ritual Event; if the Player has the ")
                .append(firstOrLast ? "most" : "least"). append( " amount of stars, it ")
                .append(firstOrLast ? "doubles the won" : "recovers the lost").append(" pP.")
                .toString();
    }

    /**
     * @see IF_Event Shaman Boost implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "s" + (firstOrLast ? "2" : "0") + "b";
    }
}
