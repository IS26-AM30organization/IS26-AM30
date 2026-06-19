package mesos.am30.gameModel.event;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Event "Shamanic Ritual" from the Event Cards.
 * <br/>This Class Represents the Event "Shamanic Ritual", which is a type of Event Card.
 * <br/>The Players who have the fewer amount of Stars loses Prestige Points, meanwhile the ones with the most Stars gain Prestige Points.
 */
public class ShamanicRitual implements IF_Event {
    private Map<Player, Integer> starsPool;
    private final int playersNumber;
    private final int lostPrestigePoints;
    private final int gainedPrestigePoints;

    /**
     * Constructor for the Event "Shamanic Ritual".
     * <br/><strong>Pre:</strong> playersNumber > 0 &amp;&amp; lostPrestigePoints &lt; 0 &amp;&amp; gainedPrestigePoints > 0
     * <br/><strong>Post:</strong> this.playersNumber = playersNumber &amp;&amp; this.lostPrestigePoints = lostPrestigePoints &amp;&amp;
     *       this.gainedPrestigePoints = gainedPrestigePoints
     *
     * @param playersNumber         Number of Player to Analyze.
     * @param lostPrestigePoints    Number of Prestige Points to remove (negative).
     * @param gainedPrestigePoints  Number of Prestige Points to add (positive).
     */
    public ShamanicRitual(int playersNumber, int lostPrestigePoints, int gainedPrestigePoints) {
        this.playersNumber = playersNumber;
        this.lostPrestigePoints = lostPrestigePoints;
        this.gainedPrestigePoints = gainedPrestigePoints;
    }

    // Test getter for the attribute "lostPrestigePoints".
    int getLostPrestigePoints() {
        return lostPrestigePoints;
    }

    // Test getter for the attribute "gainedPrestigePoints".
    int getGainedPrestigePoints() {
        return gainedPrestigePoints;
    }

    /**
     * Handles the Event "Shamanic Ritual".
     * <br/>This method handles the Event "Shamanic Ritual" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != NULL &amp;&amp; board.players.contains(player)
     * <br/><strong>Post:</strong> ((\forall Player p; board.players.contains(p); player.parameters(SHAMAN) &lt;= p.parameters(SHAMAN)) ==>
     *          player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) + lostPrestigePoints) &amp;&amp;
     *       ((\forall Player p; board.players.contains(p); player.parameters(SHAMAN) >= p.parameters(SHAMAN)) ==>
     *          player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) + gainedPrestigePoints)
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        if (starsPool == null) starsPool = new HashMap<>(playersNumber);
        starsPool.put(player, player.getParameters().get(Parameter.SHAMAN));
        if (starsPool.size() == playersNumber) {
            // last Player --> check the Stars
            List<Integer> sortedStars = starsPool.values().stream().sorted().toList();
            for (Player p : starsPool.keySet()) {
                if (starsPool.get(p).equals(sortedStars.getFirst())) {
                    // Players with fewer Stars
                    for(BuildingCard b : p.getBuildings()) {
                        // handle ShamanBoost Building
                        if (b.getEventType() == EventType.SHAMANIC_RITUAL ) {
                            ShamanBoost event = (ShamanBoost) b.getEvent();
                            if (!event.isFirstOrLast()) {
                                event.setEventPrestigePoints(lostPrestigePoints);
                                event.handleEvent(p);
                            }
                        }
                    }
                    p.updateStats(Parameter.PRESTIGE_POINTS, lostPrestigePoints);
                }
                if (starsPool.get(p).equals(sortedStars.getLast())) {
                    // Players with more Stars
                    for(BuildingCard b : p.getBuildings()) {
                        // handle ShamanBoost Building
                        if (b.getEventType() == EventType.SHAMANIC_RITUAL ) {
                            ShamanBoost event = (ShamanBoost) b.getEvent();
                            if (event.isFirstOrLast()) {
                                event.setEventPrestigePoints(gainedPrestigePoints);
                                event.handleEvent(p);
                            }
                        }
                    }
                    p.updateStats(Parameter.PRESTIGE_POINTS, gainedPrestigePoints);
                }
            }
        }
    }

    /**
     * @see IF_Event Shamanic Ritual implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("ShamanicRite");
        str2.append("+pP:").append(gainedPrestigePoints);
        str3.append("-pP:").append(lostPrestigePoints);

    }

    /**
     * @see IF_Event Shamanic Ritual implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This is a ShamanicRitual Event Card: when resolved, the player with the most amount of stars gains ")
                .append(gainedPrestigePoints).append(" pP, the one with the least amount loses ")
                .append(lostPrestigePoints).append(" pP.").toString();
    }

    /**
     * @see IF_Event Shamanic Ritual implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return gainedPrestigePoints + "r" + lostPrestigePoints * (-1) + "r0";
    }
}