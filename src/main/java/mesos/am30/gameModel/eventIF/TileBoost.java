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
    public String getArt(){
        return new String("tb");
    }

    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Additional");
        str3.append(buffToStr());
    }

    private String buffToStr() {
        return switch (buffType) {
            case ADDITIONAL_FOOD_TILE -> "Food";
            case ADDITIONAL_UP_TILE -> "UpMove";
            default -> "";
        };
    }

    @Override
    public String getCardInfo(StringBuilder info) {
         info.append("This Building grants its owner ");
        return switch (buffType) {
            case ADDITIONAL_FOOD_TILE -> info.append("1more food if his totem is then placed on a food tile.").toString();
            case ADDITIONAL_UP_TILE -> info.append("1more UpMove at the end of the round").toString();
            default -> "";
        };
    }
}
