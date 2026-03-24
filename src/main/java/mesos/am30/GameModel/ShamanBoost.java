package mesos.am30.GameModel;

/**
 * Event "ShamanBoost" from the Building Cards.
 * This Class Represents the Event "ShamanBoost", which is a type of Building Card.
 * Due to the type of Building Card:
 *  - if the Player has the most amount of Stars, it does gain double the Prestige Points.
 *  - if the Player has the least amount of Stars, it does not lose Prestige Points.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class ShamanBoost implements IF_Event {
    private final boolean firstOrLast;
    private int eventPrestigePoints;

    /**
     * Constructor for the Event "ShamanBoost".
     * Post: this.eventPrestigePoints = eventPrestigePoints
     *
     * @param firstOrLast True if the Boost applies when Player is First, False when is Last
     */
    public ShamanBoost(boolean firstOrLast) {
        this.firstOrLast = firstOrLast;
    }

    /**
     * Getter for the attribute "firstOrLast".
     *
     * @return this.firstOrLast
     */
    public boolean isFirstOrLast() {
        return firstOrLast;
    }

    public void setEventPrestigePoints(int eventPrestigePoints) {
        this.eventPrestigePoints = eventPrestigePoints;
    }

    /**
     * Handles the Event "ShamanBoost".
     * This method handles the Event "ShamanBoost" for the given Player, updating its parameters.
     * Pre: player != NULL && board.players.contains(player)
     * Post: (firstOfLast ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + eventPrestigePoints) &&
     *       (!firstOfLast ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) - eventPrestigePoints)
     *
     * @param player Player to update due to the Event
     */
    @Override
    public void handleEvent(Player player) {
        player.updateStats(Parameter.PRESTIGE_POINTS, (firstOrLast) ? eventPrestigePoints : -eventPrestigePoints);
    }
}
