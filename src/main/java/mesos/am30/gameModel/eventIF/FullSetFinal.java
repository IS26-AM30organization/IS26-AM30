package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

/**
 * Event "Full Set Final" from the Building Cards.
 * <br/>This Class Represents the Event "Full Set", which is a type of Building Card.
 * <br/>At the end of the Game, the Player gains Prestige Points for each full set of Character Cards he has completed.
 */
public class FullSetFinal implements IF_Event {
    private final int ppGain;

    /**
     * Constructor for the Building Event "Full Set Final".
     * <br/><strong>Pre:</strong> ppGain > 0
     * <br/><strong>Post:</strong> this.ppGain = ppGain
     *
     * @param ppGain Prestige Points to add if the Event condition is resolved.
     */
    public FullSetFinal(int ppGain) {
        this.ppGain = ppGain;
    }

    // Test getter for the attribute "ppGain"
    int getPpGain() {
        return ppGain;
    }

    /**
     * Handles the Building Event "Full Set Final".
     * <br/>This method handles the Building Event "Full Set Final" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> player.parameters(PRESTIGE_POINTS) = \old(player.parameters(PRESTIGE_POINTS) + (ppGain * (\min int nSets; nSets >= 0; (\forall Parameter p; ; player.getTribe().get(p).size())))
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        int nSets = min(player);

        if (nSets>0) {
            int totPP = nSets * ppGain;
            player.updateStats(Parameter.PRESTIGE_POINTS, totPP);
        }
    }

    private int min (Player player) {
        Map<Parameter, List<CharacterCard>> tribe = player.getTribe();
        int distinctRoles = (int) tribe.keySet().stream()
                .filter(p -> p != Parameter.FOOD && p != Parameter.PRESTIGE_POINTS)
                .count();

        // no full set
        if (distinctRoles < Parameter.values().length-2)
            return 0;

        // min nSet
        return tribe.entrySet().stream()
             .filter(entry -> entry.getKey() != Parameter.FOOD && entry.getKey() != Parameter.PRESTIGE_POINTS)
             .mapToInt(entry -> entry.getValue().size())
             .min()
             .orElse(0);
    }

    /**
     * @see IF_Event Full Set Final implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("FullSetEnd");
        str3.append("pPGain:").append(ppGain);
    }

    /**
     * @see IF_Event Full Set Final implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(ppGain)
                .append(" pP to its owner, for each set of 6 unique Characters he has collected during the game.")
                .toString();
    }

    /**
     * @see IF_Event Full Set Final implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "sf";
    }
}
