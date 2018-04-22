package main.java;

import javafx.application.Platform;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

class MessageWaiter extends Thread {

    private final ObjectInputStream in;
    private final Socket socket;
    private final List<NetworkEventListener> networkEventListeners = new ArrayList<>();

    MessageWaiter(ObjectInputStream in, Socket socket) {
        this.in = in;
        this.socket = socket;
    }

    public void run() {
        try {
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (!socket.isClosed()) {
            try {
                Model inMessage = (Model) in.readObject();
                for (NetworkEventListener networkEventListener : networkEventListeners)
                    networkEventListener.newMessageArrived(inMessage);
            } catch (SocketException e) {
                for (NetworkEventListener networkEventListener : networkEventListeners)
                    networkEventListener.endDisconnected();
                break;
            } catch (SocketTimeoutException e) {
                //
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }
}