package mesos.am30.GameModel;

import java.util.Map;

public class FullSet implements IF_Event {
    private Map<Parameter, Integer> set;

    public FullSet() {}

    public Map<Parameter, Integer> getSet() {
        return set;
    }

    @Override
    public void handleEvent(Player player) {}
}
