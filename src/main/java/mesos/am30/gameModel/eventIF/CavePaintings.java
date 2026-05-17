package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;

/**
 * Event "Cave Paintings" from the Event Cards.
 * <br/>This Class Represents the Event "Cave Paintings", which is a type of Event Card.
 * <br/>If the Player has fewer Artists than the required number, he looses Prestige Points, otherwise he gains some.
 */
public class CavePaintings implements IF_Event {
    private final int artistMinimum;
    private final int lostPrestigePoints;
    private final int gainedPrestigePoints;

    /**
     * Constructor for the Event "Cave Paintings".
     * <br/><strong>Pre:</strong> artistMinimum > 0 && lostPrestigePoints < 0 && gainedPrestigePoints > 0
     * <br/><strong>Post:</strong> this.artistMinimum = artistMinimum && this.lostPrestigePoints = lostPrestigePoints && this.gainedPrestigePoints = gainedPrestigePoints
     *
     * @param artistMinimum         Minimum number of Artists required to gain Prestige Points.
     * @param lostPrestigePoints    Number of Prestige Points to remove (negative).
     * @param gainedPrestigePoints  Number of Prestige Points to add (positive).
     */
    public CavePaintings(int artistMinimum, int lostPrestigePoints, int gainedPrestigePoints) {
        this.artistMinimum = artistMinimum;
        this.lostPrestigePoints = lostPrestigePoints;
        this.gainedPrestigePoints = gainedPrestigePoints;
    }

    // Test getter for the attribute "artistMinimum"
    int getArtistMinimum() {
        return artistMinimum;
    }

    // Test getter for the attribute "lostPrestigePoints"
    int getLostPrestigePoints() {
        return lostPrestigePoints;
    }

    // Test getter for the attribute "gainedPrestigePoints"
    int getGainedPrestigePoints() {
        return gainedPrestigePoints;
    }

    /**
     * Handles the Event "Cave Paintings".
     * <br/>This method handles the Event "Cave Paintings" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> (player.tribe(ARTIST).size() < artistMinimum ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + lostPrestigePoints) &&
     *       (player.tribe(ARTIST).size() > artistMinimum ==> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + gainedPrestigePoints)
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        for (BuildingCard b : player.getBuildings()) {
            if (b.getEventType() == EventType.CAVE_PAINTINGS) b.getEvent().handleEvent(player);
        }
        int artists = player.getTribe().get(Parameter.ARTIST).size();
        player.updateStats(Parameter.PRESTIGE_POINTS, (artists < artistMinimum) ? lostPrestigePoints : gainedPrestigePoints * artists);
    }

    /**
     * @see IF_Event Cave Paintings implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Paint");
        str2.append("PainterMin:").append(artistMinimum);
        str3.append("pP.Lost:").append(lostPrestigePoints).append(" Gained:").append(gainedPrestigePoints);
    }

    /**
     * @see IF_Event Cave Paintings implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This is a Painting Event Card: when resolved, if the player has at least ")
                .append(artistMinimum).append(" he gains ").append(gainedPrestigePoints)
                .append(" pP, otherwise player loses ").append(lostPrestigePoints).append(" pP.")
                .toString();
    }

    /**
     * @see IF_Event Cave Paintings implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return gainedPrestigePoints + "c" + lostPrestigePoints * (-1) + "c" + artistMinimum;
    }
}
