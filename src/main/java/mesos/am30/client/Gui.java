package mesos.am30.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mesos.am30.client.gui.MenuGui;
import mesos.am30.client.gui.TableGui;
import mesos.am30.client.gui.TribeGui;
import mesos.am30.common.ErrorType;
import mesos.am30.common.Move;

import java.io.IOException;
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

    private Stage little;
    private Stage big;

    private Scene launch;
    private Scene start;
    private Scene prompt;
    private Scene table;

    private MenuGui menu;
    private TableGui game;

    public void start (Stage little) throws IOException {
        vView = new SocketView(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start.fxml"));
        System.out.println("creato gui");

        Scene scene = new Scene(loader.load(), 551, 551);
        little.setTitle("MESOS");
        little.setScene(scene);

        menu = loader.getController();
        menu.setView(vView);

        loader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
        try {
            big = new Stage();
            table = new Scene(loader.load(), 1920, 1080);
            big.setScene(table);
            big.setFullScreen(true);
        } catch (IOException e) {
            System.out.println("Error loading graphic interface");
            e.printStackTrace();
        }

        game = loader.getController();
        game.setView(vView);
        game.setStage(big);

        TribeGui.set(table, table.getRoot());

        this.little = little;
        little.show();
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

    }

    /**
     * Notifies the User that the connection to the Server has been established
     */
    @Override
    public void confirmConnection() {

    }

    /**
     * Notifies the User that they have successfully joined the lobby
     */
    @Override
    public void confirmLobbyJoined() {

    }

    @Override
    public void printMove(String nickname, Move move) {
        game.printMove(nickname, move);
    }

    @Override
    public void printError(ErrorType errorType) {

    }

    @Override
    public void refresh(ViewModel viewModel) {
        Platform.runLater(() -> {
            game.setBoard(viewModel);
            if (little.isShowing()) {
                menu.hide();
                System.out.println("menu closed");
            }
            if (!big.isShowing()) {
                game.setName(menu.getNickname());
                game.createTable();
                big.show();
            }
            game.refresh(viewModel);
        });
    }

    @Override
    public void printEnd() {
        game.printEnd();
    }

    @Override
    public void setvView(VirtualView view) {

    }

    @Override
    public void setvModel(ViewModel vBoard) {

    }

    public void setStage(Stage stagee) {
        stage = stagee;
    }
}
