package mesos.am30.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mesos.am30.client.gui.*;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

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
        if (!ClientMain.getRMI()) {
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
     * Delegates nickname request to the menu controller.
     *
     * @see IF_GameUI#askNickname()
     */
    @Override
    public void askNickname() {
        menu.askNickname();
    }


    /**
     * Shows the available lobbies the player can join.
     * <br/><strong>Pre:</strong> availableLobbies != null
     *
     * @param availableLobbies Map of lobby codes to their current number of players.
     * @see IF_GameUI#showLobbies(Map)
     */
    @Override
    public void showLobbies(Map<String, Integer> availableLobbies) {
        menu.showLobbies(availableLobbies);
    }

    /**
     * Notifies the player that the connection to the server has been established.
     *
     * @see IF_GameUI#confirmConnection()
     */
    @Override
    public void confirmConnection() {
        menu.confirmConnection();
    }

    /**
     * Notifies the player that they have successfully joined the lobby.
     *
     * @see IF_GameUI#confirmLobbyJoined()
     */
    @Override
    public void confirmLobbyJoined() {
        menu.confirmLobbyJoined();
    }

    /**
     * Delegates move display to the game table controller.
     * <br/><strong>Pre:</strong> nickname != null
     * <br/><strong>Pre:</strong> move != null
     *
     * @param nickname The acting player's nickname.
     * @param move The move type required.
     * @see IF_GameUI#printMove(String, Move)
     */
    @Override
    public void printMove(String nickname, Move move) {
        game.printMove(nickname, move);
    }

    /**
     * Routes the error to the active controller (menu or game table).
     * <br/><strong>Pre:</strong> errorType != null
     *
     * @param errorType The type of error to display.
     * @see IF_GameUI#printError(ErrorType)
     */
    @Override
    public void printError(ErrorType errorType) {
        if(!started) menu.printError(errorType);
        else game.printError(errorType);
    }

    /**
     * Initializes the game table on first call, then delegates refresh to the game controller.
     * <br/><strong>Pre:</strong> viewModel != null
     *
     * @param viewModel The updated view model.
     * @see IF_GameUI#refresh(ViewModel)
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
                //big.setScene(table);
                big.setFullScreen(true);
                game.setBoard(viewModel);
                game.setName(menu.getNickname());
                game.createTable(scene, root);
            }
            game.refresh(viewModel);
        });
    }

    /**
     * Delegates leaderboard button visibility to the game table controller.
     *
     * @see IF_GameUI#askShowRankings()
     */
    @Override
    public void askShowRankings() {
        game.askShowRankings();
    }

    /**
     * Delegates ranking display to the game table controller.
     * <br/><strong>Pre:</strong> playerRank != null
     * <br/><strong>Pre:</strong> globalRankings != null
     *
     * @param playerRank The current player's rank entry.
     * @param globalRankings All players' rankings in order.
     * @see IF_GameUI#showRankings(Map, List)
     */
    @Override
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        game.showRankings(playerRank, globalRankings);
    }

    /**
     * Delegates end-game overlay display to the game table controller.
     *
     * @see IF_GameUI#printEnd()
     */
    @Override
    public void printEnd() {
        game.printEnd();
    }

    /**
     * @see IF_GameUI#setvView(VirtualView)
     */
    @Override
    public void setvView(VirtualView view) {
        this.vView = view;
    }

    /**
     * @see IF_GameUI#setvModel(ViewModel)
     */
    @Override
    public void setvModel(ViewModel vBoard) {
        this.vBoard = vBoard;
    }
}
