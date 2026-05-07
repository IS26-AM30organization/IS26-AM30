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

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("tileBoost");
        str3.append(buffToStr());
    }

    private String buffToStr() {
        switch (buffType) {
            case ADDITIONAL_FOOD_TILE : return "+Food";
            case ADDITIONAL_UP_TILE : return "+Up";
            default : return "";
        }
    }
}
