package mesos.am30.GameModel;

public class FinalBoost implements IF_Event {
    private final int prestigePoints;

    public FinalBoost(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public void handleEvent(Player player) {}
}
