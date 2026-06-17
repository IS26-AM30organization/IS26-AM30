package mesos.am30.client;

import javafx.application.Application;

import java.io.IOException;
import java.net.Socket;

/**
 * Static Client entry point for the game "Mesos".
 * <br/>This class works as a static implementation of the Client for the game "Mesos".
 * <br/>It is the entry point for the Client application, which creates the View and connect it to the Server.
 */
public class ClientMain {
    private static Boolean isItRMI;
    private static String IP;

    /**
     * Main entry point for the "Mesos" Client.
     * <br/>This method is the main entry point for the "Mesos" Client; it creates the View in the specified way (socket vs RMI / TUI vs GUI),
     * then connects to the Server.
     */
    static void main(String[] args) {
        // check arguments
        if (args.length != 3) {
            System.err.println("[Wrong arguments] : You must add the arguments as follows: \"java -jar am30-client.jar 'ip' 'tui/gui' 'socket/rmi'\"");
            return;
        }
        isItRMI = args[2].equalsIgnoreCase("rmi");
        IP = args[0];

        // get User Interface
        IF_GameUI userInterface = null;
        if (args[1].equalsIgnoreCase("tui")) {
            userInterface = new Tui();
        } else if (args[1].equalsIgnoreCase("gui")) {
            Application.launch(Gui.class, args);
            System.exit(0);
        } else {
            System.err.println("[Wrong argument] : " + args[1] + "is not valid!!! Use 'tui' or 'gui'!!!");
            System.exit(1);
        }

        // run the View
        try {
            VirtualView view = null;
            int port = 0;
            if (args[2].equalsIgnoreCase("socket")) {
                view = new SocketView(userInterface);
                port = 12345;
            } else if (args[2].equalsIgnoreCase("rmi")) {
                port = 1099;
                try (Socket s = new Socket(IP, port)) {
                    System.setProperty("java.rmi.server.hostname", s.getLocalAddress().getHostAddress());
                }
                view = new RMIView(userInterface);
            } else {
                System.err.println("[Wrong argument] : " + args[2] + "is not valid!!! Use 'socket' or 'rmi'!!!");
                System.exit(1);
            }
            view.findServer(IP, port);
        } catch (IOException exception) {
            System.err.println("[ERROR: ] " + exception.getMessage());
            System.exit(1);
        }
    }

    /**
     * Check if the View instance uses the RMI communication Protocol.
     *
     * @return True if the instance uses RMI, false otherwise.
     */
    public static Boolean isRMI() {
        return isItRMI;
    }

    /**
     * Getter for the attribute "IP".
     *
     * @return IP of the Server.
     */
    public static String getIP() {
        return IP;
    }
}