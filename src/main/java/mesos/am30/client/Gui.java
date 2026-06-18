package mesos.am30.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mesos.am30.client.gui.*;
import mesos.am30.client.view.RMIView;
import mesos.am30.client.view.SocketView;
import mesos.am30.client.view.ViewModel;
import mesos.am30.client.view.VirtualView;
import mesos.am30.common.enumerations.ErrorType;
import mesos.am30.common.enumerations.Move;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JavaFX entry point for the graphical client.
 * <br/>Implements {@link IF_GameUI} and delegates all game events to the appropriate scene controller.
 */
public class Gui extends Application implements IF_GameUI {
    VirtualView vView;
    ViewModel vBoard;

    private boolean started = false;

    private Stage big;
    private Scene scene;
    private Parent root;

    private MMMGui menu;
    private TableGui game;

    /**
     * Initializes the JavaFX stage, loads FXML scenes, and connects the virtual view.
     * <br/><strong>Pre:</strong> big != null
     *
     * @param big The primary application stage.
     * @throws IOException If an FXML resource cannot be loaded.
     */
    public void start (Stage big) throws IOException {

        Font.loadFont(getClass().getResourceAsStream("/fonts/Mesos.ttf"), 14);

        int port;
        if (!ClientMain.isRMI()) {
            vView = new SocketView(this);
            port = 12345;
        } else {
            vView = new RMIView(this);
            port = 1099;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/multimatch_menu.fxml"));

        scene = new Scene(loader.load(), 551, 551);
        big.setTitle("MESOS");
        big.setScene(scene);

        menu = loader.getController();
        menu.setView(vView);
        menu.setPort(port);

        loader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
        try {
            root = loader.load();
            big.setFullScreen(true);
        } catch (IOException e) {
            System.out.println("Error loading graphic interface");
        }

        game = loader.getController();
        game.setView(vView);

        TribeGui.set(scene, root);
        LeaderboardGui.set(scene,root);

        this.big = big;
        big.show();
    }

    /**
     * @see IF_GameUI GUI implementation of the setVView method.
     */
    @Override
    public void setVView(VirtualView view) {
        this.vView = view;
    }

    /**
     * @see IF_GameUI GUI implementation of the setVModel method.
     */
    @Override
    public void setVModel(ViewModel vBoard) {
        this.vBoard = vBoard;
    }

    /**
     * @see IF_GameUI GUI implementation of the confirmConnection method.
     */
    @Override
    public void confirmConnection() {
        menu.confirmConnection();
    }

    /**
     * @see IF_GameUI GUI implementation of the showLobbies method.
     */
    @Override
    public void showLobbies(Map<String, Integer> availableLobbies) {
        menu.showLobbies(availableLobbies);
    }

    /**
     * @see IF_GameUI GUI implementation of the askNickname method.
     */
    @Override
    public void askNickname() {
        menu.askNickname();
    }

    /**
     * @see IF_GameUI GUI implementation of the confirmLobbyJoined method.
     */
    @Override
    public void confirmLobbyJoined() {
        menu.confirmLobbyJoined();
    }

    /**
     * @see IF_GameUI GUI implementation of the printMove method.
     */
    @Override
    public void printMove(String nickname, Move move) {
        game.printMove(nickname, move);
    }

    /**
     * @see IF_GameUI GUI implementation of the printError method.
     */
    @Override
    public void printError(ErrorType errorType) {
        if(!started) menu.printError(errorType);
        else game.printError(errorType);
    }

    /**
     * @see IF_GameUI GUI implementation of the refresh method.
     */
    @Override
    public void refresh(ViewModel viewModel) {
        Platform.runLater(() -> {
            if (!started)
                menu.loading();
        });
        Platform.runLater(() -> {
            if (!started) {
                started = true;
                big.setFullScreen(true);
                game.setBoard(viewModel);
                game.setName(menu.getNickname());
                game.createTable(scene, root);
            }
            game.refresh(viewModel);
        });
    }

    /**
     * @see IF_GameUI GUI implementation of the askShowRankings method.
     */
    @Override
    public void askShowRankings() {
        game.askShowRankings();
    }

    /**
     * @see IF_GameUI GUI implementation of the showRankings method.
     */
    @Override
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        game.showRankings(playerRank, globalRankings);
    }

    /**
     * @see IF_GameUI GUI implementation of the printEnd method.
     */
    @Override
    public void printEnd() {
        game.printEnd();
    }
}