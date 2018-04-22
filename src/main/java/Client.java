package main.java;

import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles connection with the server and allows to exchange data structures containing game state
 */
class Client extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageWaiter messageWaiter;
    private final List<NetworkEventListener> networkEventListeners = new ArrayList<>();

    /**
     * Creates client thread object with given socket connected to server
     * @param socket socket with server connection
     */
    Client(Socket socket) {
        this.socket = socket;
    }

    /**
     * Runs thread and creates input and output stream, creates messageWaiter thread.
     * Notifies all listeners that connection has been established.
     */
    @Override
    public void run() {
        try {
            socket.setKeepAlive(true);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            messageWaiter = new MessageWaiter(in, socket);
            for (NetworkEventListener networkEventListener : networkEventListeners)
                networkEventListener.establishedConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds NetworkEventListener which can be notified when new message will arrive
     * @param toAdd listener to add
     */
    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }

    /**
     * Starts messageWaiter thread
     * @return messageWaiter thread
     */
    MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }

    /**
     * Sends to the server initial games status on the client side, so the server can get to know clients nickname
     * @param object data structure with client nickname
     */
    void initHandshake(Object object) {
        try {
            out.writeObject(object);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for the response from the server, so the client can get to know servers nickname and turn
     * @return data structrue with server nickname and turn
     */
    Model waitForResponse() {
        Model model = null;
        try {
            model = (Model) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Sends data structure with current game status
     * @param object data structure with current game status
     */
    void send(Object object) {
        try {
            out.writeObject(object);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes socket connection
     */
    void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
