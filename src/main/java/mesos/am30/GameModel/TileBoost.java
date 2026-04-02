package mesos.am30.GameModel;

import java.util.List;

public class TileBoost implements IF_Event{
    private final SpecialBuff buffType;

    public TileBoost(SpecialBuff buffType) {
        this.buffType = buffType;
    }

    @Override
    public void handleEvent(Player player) {
        player.updateStats(buffType);
    }
}
