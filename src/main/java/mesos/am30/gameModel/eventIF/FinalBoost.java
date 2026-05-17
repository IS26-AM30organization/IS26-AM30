package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

/**
 * Event "Final Boost" from the Building Cards.
 * <br/>This Class Represents the Event "Final Boost", which is a type of Building Card.
 * <br/>At the end of the Game, the Player gets extra Prestige Points based on how many Characters have of a specific role.
 */
public class FinalBoost implements IF_Event {
    private final Parameter target;
    private final int multiplier;

    /**
     * Constructor for the Building Event "Final Boost".
     * <br/><strong>Pre:</strong> target != null && multiplier > 0
     * <br/><strong>Post:</strong> this.target = target && this.multiplier = multiplier
     *
     * @param target        Character specific for the Building Card.
     * @param multiplier    Prestige Points' multiplier.
     */
    public FinalBoost(Parameter target, int multiplier) {
        this.target = target;
        this.multiplier = multiplier;
    }

    // Test getter for the attribute "target"
    Parameter getTarget() {
        return target;
    }

    // Test getter for the attribute "multiplier"
    int getMultiplier() {
        return multiplier;
    }

    /**
     * Handles the Building Event "Final Boost".
     * <br/>This method handles the Building Event "Final Boost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + (player.getTribe().get(target).size() * multiplier)
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        int tot = nCharacter(player) * multiplier;
        player.updateStats(Parameter.PRESTIGE_POINTS,tot);
    }
    private int nCharacter (Player player){ //return how many character of the same type of the target has the player
        Map<Parameter, List<CharacterCard>> tribe = player.getTribe();
        return   tribe.get(target).size();
    }

    /**
     * @see IF_Event Final Boost implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("RoleMultiplier");
        str3.append("Role:").append(target).append("x").append(multiplier);
    }

    /**
     * @see IF_Event Final Boost implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives x").append(multiplier).append(" pP for #").append(target)
                .append(" Characters once the game has ended.")
                .toString();
    }

    /**
     * @see IF_Event Final Boost implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return target.name().toLowerCase().charAt(0) + "b";
    }
}
