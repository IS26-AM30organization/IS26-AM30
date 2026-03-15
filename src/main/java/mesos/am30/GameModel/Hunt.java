package mesos.am30.GameModel;

import java.util.List;
import java.util.Map;

public class Hunt implements IF_Event {
    private final int prestigePoints;

    public Hunt(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void handleEvent(Player player) {}
}
