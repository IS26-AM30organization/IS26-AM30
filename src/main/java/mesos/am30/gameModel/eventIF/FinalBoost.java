package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

public class FinalBoost implements IF_Event {
    private final Parameter target;
    private final int multiplier;

    public FinalBoost(Parameter target, int multiplier) {
        this.target = target;
        this.multiplier = multiplier;
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

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("fnlBoost");
        str3.append("Role:" + target).append("x" + multiplier);
    }
}
