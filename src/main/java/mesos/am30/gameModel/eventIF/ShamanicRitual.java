package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Event "Shamanic Ritual" from the Event Cards.
 * This Class Represents the Event "Shamanic Ritual", which is a type of Event Card.
 * The Players who have the fewer amount of Stars loses Prestige Points, meanwhile the ones with the most Stars gain Prestige Points.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class ShamanicRitual implements IF_Event {
    private Map<Player, Integer> starsPool = new HashMap<>();
    private final int playersNumber;
    private final int lostPrestigePoints;
    private final int gainedPrestigePoints;

    /**
     * Constructor for the Event "Shamanic Ritual".
     * Pre: playersNumber > 0 && lostPrestigePoints < 0 && gainedPrestigePoints > 0
     * Post: this.playersNumber = playersNumber && this.lostPrestigePoints = lostPrestigePoints &&
     *       this.gainedPrestigePoints = gainedPrestigePoints && this.starsPool != NULL && this.starsPool.size() = 0
     *
     * @param playersNumber Number of Player to Analyze
     * @param lostPrestigePoints Number of Prestige Points to remove (negative)
     * @param gainedPrestigePoints Number of Prestige Points to add (positive)
     */
    public ShamanicRitual(int playersNumber, int lostPrestigePoints, int gainedPrestigePoints) {
        //this.starsPool = new HashMap<>(playersNumber);
        this.playersNumber = playersNumber;
        this.lostPrestigePoints = lostPrestigePoints;
        this.gainedPrestigePoints = gainedPrestigePoints;
    }

    /**
     * Getter for the attribute "lostPrestigePoints".
     *
     * @return this.lostPrestigePoints
     */
    public int getLostPrestigePoints() {
        return lostPrestigePoints;
    }

    /**
     * Getter for the attribute "gainedPrestigePoints".
     *
     * @return this.gainedPrestigePoints
     */
    public int getGainedPrestigePoints() {
        return gainedPrestigePoints;
    }

    /**
     * Handles the Event "Shamanic Ritual".
     * This method handles the Event "Shamanic Ritual" for the given Player, updating its parameters.
     * Pre: player != NULL && board.players.contains(player)
     * Post: ((\forall Player p; board.players.contains(p); player.parameters(SHAMAN) <= p.parameters(SHAMAN)) ==>
     *          player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + lostPrestigePoints) &&
     *       ((\forall Player p; board.players.contains(p); player.parameters(SHAMAN) >= p.parameters(SHAMAN)) ==>
     *          player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + gainedPrestigePoints)
     *
     * @param player Player to update due to the Event
     */
    @Override
    public void handleEvent(Player player) {
        if (starsPool == null) {
            starsPool = new HashMap<>(playersNumber);
        }
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

    @Override
    public String getArt(){
        return new String(gainedPrestigePoints+"r"+(lostPrestigePoints<0 ? lostPrestigePoints*(-1) : lostPrestigePoints)+"r0");
    }

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Shamanic");
        str2.append("Lost:" + lostPrestigePoints);
        str3.append("Gained: " + gainedPrestigePoints);
    }
}
