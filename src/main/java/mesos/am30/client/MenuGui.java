package mesos.am30.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuGui {
    boolean numAsked = false;
    boolean nameAsked = false;

    String nickname;
    VirtualView vView;

    @FXML
    private TextField number;

    @FXML
    private Label request;

    @FXML
    private Label quickMessage;

    @FXML
    private TextField name;

    @FXML
    private Label write;

    @FXML
    private Button button;

    public void setView (VirtualView vView){
        this.vView = vView;
    }

    public void askPlayersNumber() {
        System.out.println("Bernardo!!!");
        numAsked = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/prompt message.fxml"));
        loader.setController(this);
        Platform.runLater(() -> {
            try {
                Scene scene = new Scene(loader.load(), 496, 61);
                Stage stage1 = new Stage();
                stage1.setScene(scene);
                stage1.show();
                System.out.println("Sto per Loreggiare!!!");
            } catch (IOException e) {
                System.out.println("Dinap!!!");
            }
        });
        System.out.println("Lore!!!");
        return;
    }

    public void askNickname() {
        nameAsked = true;
        return;
    }

    public void hide(){
        Platform.runLater(() -> {
            if (number != null && number.getScene().getWindow().isShowing()) {
                number.getScene().getWindow().hide();
            }
            if (name != null && name.getScene().getWindow().isShowing()) {
                name.getScene().getWindow().hide();
            }
        });
    }

    public String getNickname() {
        return new String(nickname);
    }

    @FXML
    public void play(){
        try {
            vView.findServer("127.0.0.1", 12345);
        } catch (Exception e) {
            System.err.println("[ERROR: ] " + e.getMessage());
            System.exit(1);
        }

        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/launch.fxml"));
        loader.setController(this);

        Platform.runLater(() -> {
            try {
                Scene scene = new Scene(loader.load(), 551, 551);
                stage.setScene(scene);
                stage.show();
            } catch (IOException /*| InterruptedException*/ e) {
                System.out.println("Errore!!!");
            }
        });
    }

    @FXML
    public void setNumber(){
        String bernardo = number.getText();
        if (bernardo.matches("[1-5]")) {
            numAsked = false;
            vView.answerPlayersNumber(Integer.parseInt(bernardo));
            Platform.runLater(() -> {number.getScene().getWindow().hide();});
        } else {
            Platform.runLater(() -> request.setText("I TOLD YOU TO INSERT A NUMBER (between 1 and 5): "));
        }
    }

    @FXML
    public void setName() {
        if (numAsked) {
            quickMessage.setText("Please create a lobby first");
            askPlayersNumber();
        } else if (!nameAsked) {
            quickMessage.setText("Connecting... Please retry");
        } else {
            nickname = name.getText();
            vView.answerNickname(nickname);
            quickMessage.setText("Waiting for players...");
            name.setVisible(false);
            write.setVisible(false);
        }
    }
}
