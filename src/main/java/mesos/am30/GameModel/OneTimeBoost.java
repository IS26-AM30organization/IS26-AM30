package mesos.am30.GameModel;

public class OneTimeBoost implements IF_Event{
    private final int gain;
    private final Parameter target;

    public OneTimeBoost(int gain, Parameter target) {
        this.gain = gain;
        this.target = target;
    }

    public int getGain() {
        return gain;
    }

    public Parameter getTarget() {
        return target;
    }

    @Override
    public void handleEvent(Player player) {
        player.updateStats(target, gain);
    }
}
