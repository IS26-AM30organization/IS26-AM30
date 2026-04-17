package mesos.am30.view;

import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

public interface IF_GameUI {
    int askPlayersNumber();
    String askNickname();

    void printMove(String nickname, Move move);
    void printError(ErrorType errorType);
    void refresh(ViewModel viewModel);
}
