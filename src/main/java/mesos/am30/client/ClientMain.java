package mesos.am30.client;

import java.rmi.RemoteException;

public class ClientMain {

    public static void main(String[] args) throws RemoteException {
        String serverIp = "127.0.0.1";
        int serverPort = 12345;

        Tui tui = new Tui();

        VirtualView view = new SocketView(tui);
        tui.vView = view;

        try {
            view.findServer(serverIp, serverPort);
        } catch (Exception e) {
            System.err.println("[ERROR: ] " + e.getMessage());
            System.exit(1);
        }

    }
}
