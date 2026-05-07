package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;

public class TileBoost implements IF_Event {
    private final SpecialBuff buffType;

    public TileBoost(SpecialBuff buffType) {
        this.buffType = buffType;
    }

    @Override
    public void handleEvent(Player player) {
        player.updateStats(buffType);
    }
}
