package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event "Full Set" from the Building Cards.
 * <br/>This Class Represents the Event "Full Set", which is a type of Building Card.
 * <br/>If the Player completes a full set of Character Cards, he gains food.
 */
public class FullSet implements IF_Event {
    private final Map<Parameter, Integer> collectedSets = new HashMap<>();
    private final Map<Parameter,Integer> prevAmount = new HashMap<>(); //saves each characterType amount prior to drawing a new card
    private final int foodGain;

    /**
     * Constructor for the Building Event "Full Set".
     * <br/><strong>Pre:</strong> foodGain > 0
     * <br/><strong>Post:</strong> this.foodGain = foodGain
     *
     * @param foodGain Food to add if the Event condition is resolved.
     */
    public FullSet(int foodGain) {
        this.foodGain = foodGain;
    }

    // Test getter for the attribute "foodGain"
    int getFoodGain() {
        return foodGain;
    }

    /**
     * Handles the Building Event "Full Set".
     * <br/>This method handles the Building Event "Full Set" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> (collectedSets().size() == Parameter.values().length - 2) ==> player.parameters(FOOD) = \old(player.parameters(FOOD) + foodGain
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        CharacterCard newestCard = getLatestPickedCard(player);
        if (newestCard == null) return;
        int m = collectedSets.getOrDefault(newestCard.getRole(),0);
        collectedSets.put(newestCard.getRole(), m + 1);

        if (isThereOneOfEach()){
            player.updateStats(Parameter.FOOD, foodGain);
            for (Parameter p : collectedSets.keySet()) {
                collectedSets.replace(p, collectedSets.get(p) - 1);
            }
        }
    }

    //getLatestPickedCard returns said card and updates prevAmount based on the latest picked card
    private CharacterCard getLatestPickedCard(Player player){
        for (Parameter p : player.getTribe().keySet()){
            int currentAmount = player.getCharacterType(p).size();
            if (prevAmount.getOrDefault(p,0) < currentAmount){
                prevAmount.put(p, currentAmount);//updating cardType amount after card drawn
                List<CharacterCard> lastType = player.getCharacterType(p);

                return lastType.getLast(); //being the lastPickedCard = atLeast 1 of CardType
            }
        }
        return null;
    }

    // check is there is a Full Set
    private boolean isThereOneOfEach() {
        int count = (int) collectedSets.values().stream()
                                                .filter(i -> i > 0)
                                                .count();
        return count >= Parameter.values().length - 2; //-2 as there are 2parameter!=Characters in Parameters
    }

    /**
     * @see IF_Event Full Set implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("FullSet");
        str3.append("foodGain:").append(foodGain);
    }

    /**
     * @see IF_Event Full Set implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(foodGain)
                .append(" food to its owner, once he collects a set of 6 unique Characters.")
                .toString();
    }

    /**
     * @see IF_Event Full Set implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "fs";
    }
}

