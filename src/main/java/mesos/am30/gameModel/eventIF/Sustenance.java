package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.*;
import mesos.am30.gameModel.card.BuildingCard;

/**
 * Event "Sustenance" from the Event Cards.
 * <br/>This Class Represents the Event "Sustenance", which is a type of Event Card.
 * <br/>If the Player has fewer Artists than the required number, he looses Prestige Points, otherwise he gains some.
 */
public class Sustenance implements IF_Event {
    private final int prestigePoints;

    /**
     * Constructor for the Event "Sustenance".
     * <br/><strong>Pre:</strong> prestigePoints < 0
     * <br/><strong>Post:</strong> this.prestigePoints = prestigePoints
     *
     * @param prestigePoints Number of Prestige Points to remove (negative) for each Character if there is not enough Food.
     */
    public Sustenance(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    // Test getter for the attribute "prestigePoints".
     int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Handles the Event "Sustenance".
     * <br/>This method handles the Event "Sustenance" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != NULL && board.players.contains(player)
     * <br/><strong>Post:</strong> (player.tribe().values().size() <= \old(player.parameters(FOOD)) ==> player.parameters(FOOD) = \old(player.parameters(FOOD)) - player.tribe().values().size()) &&
     *       (player.tribe().values().size() > \old(player.parameters(FOOD)) ==>  player.parameters(FOOD) = 0 &&
     *          player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS)) - (player.tribe().values().size() - \old(player.parameters(FOOD))))
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        // handle the Building Stats Boost
        for (BuildingCard b : player.getBuildings()) {
            if (b.getEventType() == EventType.SUSTENANCE) b.getEvent().handleEvent(player);
        }
        // compute the Food cost
        int foodCost = 0;
        for (Parameter p : player.getTribe().keySet()) {
            foodCost += player.getTribe().get(p).size();
        }
        foodCost -= player.getParameters().get(Parameter.GATHERER);
        // pay for the Characters
        if (foodCost > 0) {
            int foodAvailable = player.getParameters().get(Parameter.FOOD);
            int foodLeft = foodAvailable - foodCost;
            if (foodLeft >= 0) {
                // Player has enough Food
                player.updateStats(Parameter.FOOD, -foodCost);
            } else {
                // Player does not have enough Food
                player.updateStats(Parameter.FOOD, -foodAvailable);
                player.updateStats(Parameter.PRESTIGE_POINTS, Math.abs(foodLeft) * prestigePoints);
            }
        }
    }

    /**
     * @see IF_Event Sustenance implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Sustenance");
        str2.append("pP:").append(prestigePoints);
    }

    /**
     * @see IF_Event Sustenance implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This is a Sustenance Event Card: when resolved, each player must pay 1 food for each Character in player's tribe.")
                .append("\nIf player's food is not enough, he loses ").append(prestigePoints)
                .append(" for each Character remaining.").toString();
    }

    /**
     * @see IF_Event Sustenance implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "0s" + (prestigePoints * (-1)) + "s1";
    }
}
