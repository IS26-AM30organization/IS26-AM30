package mesos.am30.GameModel;

public class FinalPPBoost implements IF_Event{
    private final int ppGain;

    public FinalPPBoost(int ppGain) {
        this.ppGain = ppGain;
    }

    public int getPpGain() {
        return ppGain;
    }


    @Override
    public void handleEvent(Player player) {
        player.updateStats(Parameter.PRESTIGE_POINTS, ppGain);
    }
}
