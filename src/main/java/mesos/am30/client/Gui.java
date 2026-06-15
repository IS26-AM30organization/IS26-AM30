package mesos.am30.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import mesos.am30.client.gui.*;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Gui extends Application implements IF_GameUI {
    VirtualView vView;
    ViewModel vBoard;
    int playersNumber;
    boolean window = false;
    boolean ready = false;

    private String serverIp;
    private int serverPort;
    private Stage stage;

    private boolean started = false;

    private Stage little;
    private Stage big;

    private Scene launch;
    private Scene start;
    private Scene prompt;
    private Scene scene;

    private Parent root;

    private MMMGui menu;
    private TableGui game;
    private Map<String, Integer> availableLobbies;

    public void start (Stage big) throws IOException {

        Font myfont = Font.loadFont(getClass().getResourceAsStream("/fonts/Mesos.ttf"), 14);
        //System.out.println("Font name: " + myfont.getFamily());

        int port = 0;
        if (!ClientMain.getRMI()) {
            vView = new SocketView(this);
            port = 12345;
        } else if (ClientMain.getRMI()) {
            vView = new RMIView(this);
            port = 1099;
        } else {
            System.err.println("[Wrong argument] : use 'socket' or 'rmi'!!!");
            System.exit(1);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/multimatch_menu.fxml"));
        //System.out.println("creato gui");

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
            e.printStackTrace();
        }

        game = loader.getController();
        game.setView(vView);
        game.setStage(big);

        TribeGui.set(scene, root);
        LeaderboardGui.set(scene,root);

        this.big = big;
        big.show();
    }

    public void ready() {
        ready = true;
        notifyAll();
    }

    @Override
    public void askNickname() {
        menu.askNickname();
    }


    /**
     * Shows the available lobbies the User can join
     *
     * @param availableLobbies map of lobby codes to their current number of players
     */
    @Override
    public void showLobbies(Map<String, Integer> availableLobbies) {
        menu.showLobbies(availableLobbies);
    }

    /**
     * Notifies the User that the connection to the Server has been established
     */
    @Override
    public void confirmConnection() {
        menu.confirmConnection();
    }

    /**
     * Notifies the User that they have successfully joined the lobby
     */
    @Override
    public void confirmLobbyJoined() {
        menu.confirmLobbyJoined();
    }

    @Override
    public void printMove(String nickname, Move move) {
        game.printMove(nickname, move);
    }

    @Override
    public void printError(ErrorType errorType) {
        if(!started) menu.printError(errorType);
        else game.printError(errorType);
    }

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

    @Override
    public void askShowRankings() {
        game.askShowRankings();
    }

    @Override
    public void showRankings(Map<String, String> playerRank, List<Map<String, String>> globalRankings) {
        game.showRankings(playerRank, globalRankings);
    }

    @Override
    public void printEnd() {
        game.printEnd();
    }

    @Override
    public void setvView(VirtualView view) {
        this.vView = view;
    }

    @Override
    public void setvModel(ViewModel vBoard) {
        this.vBoard = vBoard;
    }

    public void setStage(Stage stagee) {
        stage = stagee;
    }
}
