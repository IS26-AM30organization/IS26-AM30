package mesos.am30.GameModel;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DoubleInventions implements IF_Event {
    private final Set<Integer> uniqueInventions;
    private final int foodGain;

    public DoubleInventions(int foodGain) {
        this.uniqueInventions = new HashSet<>(10);
        this.foodGain = foodGain;
    }

    public Set<Integer> getUniqueInventions() {
        return uniqueInventions;
    }

    @Override
    public void handleEvent(Player player) {
        Integer latestInvention = getLatestInvention(player);

        if(uniqueInventions.contains(latestInvention)){ //reminder: Optional<Integer>.get() returns the Integer if present
            player.updateStats(Parameter.FOOD, foodGain);
            uniqueInventions.remove(latestInvention);
            return;
        }
        uniqueInventions.add(latestInvention);

    }

    private Integer getLatestInvention(Player player){
        List<CharacterCard> inventors = player.getCharacterType(Parameter.INVENTOR);
        return inventors.get(inventors.size() - 1).getValue(); //inventors.size()!=0 always, as this class is called as an Inventor is chosen.
    }
}
