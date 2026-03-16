package mesos.am30.GameModel;

/**
 * Event "Hunt" from the Event Cards.
 * This Class Represents the Event "Hunt", which is a type of Event Card.
 * It gives one Food unit to all Players, then it adds extra Prestige Points depending on the Card specific Value multiplied by the number of Hunters each Player has.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class Hunt implements IF_Event {
    private final int prestigePoints;

    /**
     * Constructor for the Event "Hunt".
     * Pre: prestigePoints > 0
     * Post: this.prestigePoints = prestigePoints
     *
     * @param prestigePoints Prestige Points to add for each Hunter
     */
    public Hunt(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    /**
     * Getter for the attribute "prestigePoints".
     *
     * @return this.prestigePoints
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Handles the Event "Hunt".
     * This method handles the Event "Hunt" for the given Player, updating its parameters.
     * Pre: player != NULL && board.players.contains(player)
     * Post: player.parameters(FOOD) = \old(player.parameters(FOOD) + 1) &&
     *       player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + (this.prestigePoints * player.tribe(HUNTER).size()))
     *
     * @param player Player to update due to the Event
     */
    @Override
    public void handleEvent(Player player) {
        int hunters = player.getTribe().get(Parameter.HUNTER).size();
        player.updateStats(Parameter.FOOD, 1);
        player.updateStats(Parameter.PRESTIGE_POINTS, hunters * prestigePoints);
    }
}
