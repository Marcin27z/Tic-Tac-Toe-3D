package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class MessageWaiter extends Thread {

    //private BufferedReader in;
    private ObjectInputStream in;
    private Socket socket;
    private List<NetworkEventListener> listeners = new ArrayList<>();

    MessageWaiter(ObjectInputStream in, Socket socket) {
        this.in = in;
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try {
                //String inMessage = in.readLine();
                Model inMessage = (Model) in.readObject();
                System.out.println(inMessage.player[0].getName() + "," + inMessage.player[1].getName());
                for (NetworkEventListener hl : listeners)
                    hl.newMessageArrived(inMessage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        listeners.add(toAdd);
    }
}