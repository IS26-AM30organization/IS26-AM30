package mesos.am30.GameModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FullSet implements IF_Event {
    private final Map<Parameter, Integer> collectedSets;
    private final Map<Parameter,Integer> prevAmount; //saves each characterType amount prior to drawing a new card
    private final int foodGain;

    public FullSet(int foodGain) {
        this.collectedSets = new HashMap<>();
        this.prevAmount = new HashMap<>();
        this.foodGain = foodGain;
    }


    @Override
    public void handleEvent(Player player) {
        CharacterCard newestCard = getLatestPickedCard(player);
        int m = collectedSets.getOrDefault(newestCard.getRole(),0);
        collectedSets.put(newestCard.getRole(), m + 1);

        if (isThereOneOfEach(player)){
            player.updateStats(Parameter.FOOD, foodGain);
            for (Parameter p : collectedSets.keySet()) {
                int currentValue = collectedSets.get(p);
                if (currentValue>0) {
                    collectedSets.put(p, currentValue - 1);
                }
            }
        }
        return;
    }

    //getLatestPickedCard returns said card and updates prevAmount based on the latest picked card
    private CharacterCard getLatestPickedCard(Player player){
        for (Parameter p : player.getTribe().keySet()){
            if(!(p.equals(Parameter.FOOD)) && (!(p.equals(Parameter.PRESTIGE_POINTS)))){
                int currentAmount = player.getCharacterType(p).size();
                if (prevAmount.getOrDefault(p,0) < currentAmount){
                    prevAmount.put(p, currentAmount);//updating cardType amount after card drawn
                    List<CharacterCard> lastType = player.getCharacterType(p);

                    return lastType.get(lastType.size() - 1); //being the lastPickedCard = atLeast 1 of CardType
                }
            }
        }
        return null;
    }

    private boolean isThereOneOfEach(Player player) {
        // int count = (int) set.keySet().stream().map(p -> set.get(p)).filter(i -> i > 0).count(); //same, but worse.
        int count = (int) collectedSets.values().stream()
                                                .filter(i -> i > 0)
                                                .count();
                                                //values() returns a Collection of the values of each key
        return count >= Parameter.values().length - 2; //-2 as there are 2parameter!=Characters in Parameters
    }
}

