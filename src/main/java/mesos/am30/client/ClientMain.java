package mesos.am30.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

public class ClientMain {
    Tui tui;

    public static void main(String[] args) throws RemoteException {
        String serverIp = "127.0.0.1";
        int serverPort = 12345;
        boolean doYouWantGui = true;
        VirtualView view = null;
        Tui tui = null;


        if (doYouWantGui == false) {
            tui = new Tui();
            view = new SocketView(tui);
            try {
                view.findServer(serverIp, serverPort);
            } catch (Exception e) {
                System.err.println("[ERROR: ] " + e.getMessage());
                System.exit(1);
            }
            tui.vView = view;
        } else {
            Application.launch(Gui.class, args);
        }
    }
}
