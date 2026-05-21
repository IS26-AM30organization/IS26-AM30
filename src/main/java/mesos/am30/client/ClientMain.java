package mesos.am30.client;

import java.io.IOException;

public class ClientMain {

    static void main(String[] args) {
        // check arguments
        if (args.length != 3) {
            System.err.println("[Wrong arguments] : You must add the arguments as follows: \"java -jar am30-client.jar 'ip' 'tui/gui' 'socket/rmi'\"");
            return;
        }

        // get User Interface
        IF_GameUI userInterface = null;
        if (args[1].equalsIgnoreCase("tui")) {
            userInterface = new Tui();
        } else if (args[1].equalsIgnoreCase("gui")) {
            //userInterface = new GUI();
            System.err.println("[Wrong argument] : GUI not ready yet!!!");
            System.exit(1);
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
                view = new RMIView(userInterface);
                port = 1099;
            } else {
                System.err.println("[Wrong argument] : " + args[2] + "is not valid!!! Use 'socket' or 'rmi'!!!");
                System.exit(1);
            }
            view.findServer(args[0], port);
        } catch (IOException exception) {
            System.err.println("[ERROR: ] " + exception.getMessage());
            System.exit(1);
        }
    }
}
