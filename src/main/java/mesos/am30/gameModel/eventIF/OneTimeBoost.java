package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

/**
 * Event "One Time Boost" from the Building Cards.
 * <br/>This Class Represents the Event "One Time Boost", which is a type of Building Card.
 * <br/>The Player gets a boost in a specific target, then the Building Card loses its effect.
 */
public class OneTimeBoost implements IF_Event {
    private final int gain;
    private final Parameter target;

    /**
     * Constructor for the Building Event "One Time Boost".
     * <br/><strong>Pre:</strong> gain > 0 && target != null
     * <br/><strong>Post:</strong> this.gain = gain && this.target = target
     *
     * @param gain      Gain for the target parameter.
     * @param target    Target parameter to which add the gain.
     */
    public OneTimeBoost(int gain, Parameter target) {
        this.gain = gain;
        this.target = target;
    }

    // Test getter for the attribute "gain"
    int getGain() {
        return gain;
    }

    // Test getter for the attribute "target"
    public Parameter getTarget() {
        return target;
    }

    /**
     * Handles the Building Event "One Time Boost".
     * <br/>This method handles the Building Event "One Time Boost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> player.parameters(target) = \old(player.parameters(target) + gain
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        player.updateStats(target, gain);
    }

    /**
     * @see IF_Event One Time Boost Set implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("3Stars");
        str3.append("Stars:").append(gain);
    }

    /**
     * @see IF_Event One Time Boost Set implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(gain).append(" stars during a Shamanic Ritual.")
                .toString();
    }

    /**
     * @see IF_Event One Time Boost Set implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return target.name().toLowerCase().charAt(0) + "o";
    }
}
