package mesos.am30.GameModel;

/**
 * Event "Cave Paintings" from the Event Cards.
 * This Class Represents the Event "Cave Paintings", which is a type of Event Card.
 * If the Player has fewer Artists than the required number, he looses Prestige Points, otherwise he gains some.
 *
 * @author LoreDN - Lorenzo Di Napoli
 * @version 1.0
 * @since 1.0
 */
public class CavePaintings implements IF_Event {
    private final int artistMinimum;
    private final int lostPrestigePoints;
    private final int gainedPrestigePoints;

    /**
     * Constructor for the Event "Cave Paintings".
     * Pre: artistMinimum > 0 && lostPrestigePoints < 0 && gainedPrestigePoints > 0
     * Post: this.artistMinimum = artistMinimum && this.lostPrestigePoints = lostPrestigePoints && this.gainedPrestigePoints = gainedPrestigePoints
     *
     * @param artistMinimum Minimum number of Artists required to gain Prestige Points
     * @param lostPrestigePoints Number of Prestige Points to remove (negative)
     * @param gainedPrestigePoints Number of Prestige Points to add (positive)
     */
    public CavePaintings(int artistMinimum, int lostPrestigePoints, int gainedPrestigePoints) {
        this.artistMinimum = artistMinimum;
        this.lostPrestigePoints = lostPrestigePoints;
        this.gainedPrestigePoints = gainedPrestigePoints;
    }

    /**
     * Getter for the attribute "artistMinimum".
     *
     * @return this.artistMinimum
     */
    public int getArtistMinimum() {
        return artistMinimum;
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
     * Handles the Event "Cave Paintings".
     * This method handles the Event "Cave Paintings" for the given Player, updating its parameters.
     * Pre: player != NULL && board.players.contains(player)
     * Post: (player.tribe(ARTIST).size() < artistMinimum ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + lostPrestigePoints) &&
     *       (player.tribe(ARTIST).size() > artistMinimum ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + gainedPrestigePoints)
     *
     * @param player Player to update due to the Event
     */
    @Override
    public void handleEvent(Player player) {
        int artists = player.getTribe().get(Parameter.ARTIST).size();
        player.updateStats(Parameter.PRESTIGE_POINTS, (artists < artistMinimum) ? lostPrestigePoints : gainedPrestigePoints);
    }
}
