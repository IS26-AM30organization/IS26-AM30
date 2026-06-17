package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;

public class OneTimeBoost implements IF_Event {
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

    @Override
    public String getArt(){
        return new String(target.name().toLowerCase().charAt(0)+"o");
    }

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("3Stars");
        str3.append("Stars:").append(gain);
    }

    @Override
    public String getCardInfo(StringBuilder info) {
        return info.append("This Building gives ").append(gain).append("stars during a Schiamanic Ritual")
                .toString();
    }
}
