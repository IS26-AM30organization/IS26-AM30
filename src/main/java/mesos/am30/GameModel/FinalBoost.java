package mesos.am30.GameModel;

import java.util.List;
import java.util.Map;

public class FinalBoost implements IF_Event {
    private final int prestigePoints;
    private final Parameter target;
    private final int multiplier;

    public FinalBoost(int prestigePoints, Parameter target, int multiplier) {
        this.prestigePoints = prestigePoints;
        this.target = target;
        this.multiplier = multiplier;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void handleEvent(Player player) {
        int tot= nCharacter(player) * multiplier;
        player.updateStats(Parameter.PRESTIGE_POINTS,tot);
    }
    private int nCharacter (Player player){ //return how many character of the same type of the target has the player
        Map<Parameter, List<CharacterCard>> tribe = player.getTribe();
        return   tribe.get(target).size();
    }
}
