package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

public class FinalBuilderBoost implements IF_Event {
    private final int multiplier;

    public FinalBuilderBoost(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void handleEvent(Player player) {
        Map<Parameter, List<CharacterCard>> tribe = player.getTribe();
        int tot= tribe.get(Parameter.BUILDER).stream()
                .mapToInt(CharacterCard::getPrestigePoints)
                .sum();
        // considerando che avviene già un conteggio
        player.updateStats(Parameter.PRESTIGE_POINTS,tot * (multiplier - 1));
    }

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("fnlBuilder");
        str3.append("x" + multiplier);
    }

    @Override
    public String getArt(){
        return new String("fb");
    }
}

