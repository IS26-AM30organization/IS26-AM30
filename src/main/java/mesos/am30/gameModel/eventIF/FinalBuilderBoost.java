package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

/**
 * Event "Final Builder Boost" from the Building Cards.
 * <br/>This Class Represents the Event "Final Builder Boost", which is a type of Building Card.
 * <br/>At the end of the Game, the Player gets extra Prestige Points based on the builders he has.
 */
public class FinalBuilderBoost implements IF_Event {
    private final int multiplier;

    /**
     * Constructor for the Building Event "Final Builder Boost".
     * <br/><strong>Pre:</strong> multiplier > 0
     * <br/><strong>Post:</strong> this.multiplier = multiplier
     *
     * @param multiplier Number of times to count each Builder's Prestige Points.
     */
    public FinalBuilderBoost(int multiplier) {
        this.multiplier = multiplier;
    }

    // Test getter for the attribute "multiplier"
    int getMultiplier() {
        return multiplier;
    }

    /**
     * Handles the Building Event "Final Boost".
     * <br/>This method handles the Building Event "Final Builder Boost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> (\forall CharacterCard c; player.getTribe().get(BUILDER).contains(c); player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + (c.getPrestigePoints() * multiplier))
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        Map<Parameter, List<CharacterCard>> tribe = player.getTribe();
        int tot= tribe.get(Parameter.BUILDER).stream()
                .mapToInt(CharacterCard::getPrestigePoints)
                .sum();
        player.updateStats(Parameter.PRESTIGE_POINTS,tot * (multiplier - 1));
    }

    /**
     * @see IF_Event Final Builder Boost implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("BuilderMultiplier");
        str3.append("x").append(multiplier);
    }

    /**
     * @see IF_Event Final Builder Boost implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(multiplier)
                .append(" x pP showed on owner's Builders once the game has ended.")
                .toString();
    }

    /**
     * @see IF_Event Final Builder Boost implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "fb";
    }
}

