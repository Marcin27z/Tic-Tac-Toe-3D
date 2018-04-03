package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class MessageWaiter extends Thread {

    private ObjectInputStream in;
    private Socket socket;
    private List<NetworkEventListener> networkEventListeners = new ArrayList<>();

    MessageWaiter(ObjectInputStream in, Socket socket) {
        this.in = in;
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try {
                Model inMessage = (Model) in.readObject();
                for (NetworkEventListener networkEventListener : networkEventListeners)
                    networkEventListener.newMessageArrived(inMessage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }
}