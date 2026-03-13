package mesos.am30.GameModel;

import java.util.List;

public class TileBoost implements IF_Event{
    private final int boost;

    public TileBoost(int boost) {
        this.boost = boost;
    }

    public int getBoost() {
        return boost;
    }

    @Override
    public void handleEvent(Player player) {
        return List.of();
    }
}
