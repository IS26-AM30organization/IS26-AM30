package mesos.am30.gameModel.eventIF;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

import java.util.List;
import java.util.Map;

public class FullSetFinal implements IF_Event {
    private final int ppGain;

    public FullSetFinal(int ppGain) {
        this.ppGain = ppGain;
    }

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
}
