package mesos.am30.gameModel.event;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Event "Double Inventions" from the Building Cards.
 * <br/>This Class Represents the Event "Double Inventions", which is a type of Building Card.
 * <br/>If the Player get two identical inventions, he gains food.
 */
public class DoubleInventions implements IF_Event {
    private Set<Integer> uniqueInventions;
    private final int foodGain;

    /**
     * Constructor for the Building Event "Double Inventions".
     * <br/><strong>Pre:</strong> foodGain > 0
     * <br/><strong>Post:</strong> this.foodGain = foodGain
     *
     * @param foodGain Food to add if the Event condition is resolved.
     */
    public DoubleInventions(int foodGain) {
        this.foodGain = foodGain;
    }

    // Test getter for the attribute "foodGain"
    int getFoodGain() {
        return foodGain;
    }

    // Test getter for the attribute "uniqueInventions"
    Set<Integer> getUniqueInventions() {
        return uniqueInventions;
    }

    /**
     * Handles the Building Event "Double Inventions".
     * <br/>This method handles the Building Event "Double Inventions" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null &amp;&amp; board.players.contains(player)
     * <br/><strong>Post:</strong> (uniqueInventions.contains(latestInvention) ==> player.parameters(FOOD) = \old(player.parameters(FOOD) + foodGain) &amp;&amp;
     *       (!uniqueInventions.contains(latestInvention) ==> uniqueInventions.add(latestInvention))
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        if(uniqueInventions == null) uniqueInventions = new HashSet<>(10);

        Integer latestInvention = getLatestInvention(player);

        if(uniqueInventions.contains(latestInvention)){
            player.updateStats(Parameter.FOOD, foodGain);
            uniqueInventions.remove(latestInvention);
            return;
        }
        uniqueInventions.add(latestInvention);

    }

    // get the latest added invention
    private Integer getLatestInvention(Player player){
        List<CharacterCard> inventors = player.getCharacterType(Parameter.INVENTOR);

        if (inventors.isEmpty()) return 0;
        return inventors.getLast().getValue(); //inventors.size()!=0 always, as this class is called as an Inventor is chosen.
    }

    /**
     * @see IF_Event Double Inventions implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("2Inventions");
        str2.append("food:").append(foodGain);
    }

    /**
     * @see IF_Event Double Inventions implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
        return info.append("This Building gives ").append(foodGain)
                .append(" food to its owner when he acquires two inventions of the same type.")
                .toString();
    }

    /**
     * @see IF_Event Double Inventions implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "di";
    }
}
