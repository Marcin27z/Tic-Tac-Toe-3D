package main.java.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Hosts the game, listens on the socket for incoming connections and handles that connection
 */
public class Server extends Thread {

    private Socket socket;
    //private ClientHandler clientHandler;
    private ServerSocket serverSocket;
    private final List<NetworkEventListener> networkEventListeners = new ArrayList<>();
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageWaiter messageWaiter;

    /**
     * Creates new Server with given ServerSocket for connection accepting
     * @param serverSocket socket on which accept connections
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Runs thread that listens for connection, creates input and output streams and notifies listeners that new client has connected
     */
    @Override
    public void run() {
        try {
            socket = serverSocket.accept();
            socket.setKeepAlive(true);
            //clientHandler = new ClientHandler(socket);
            //clientHandler.start();
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                messageWaiter = new MessageWaiter(in, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (NetworkEventListener networkEventListener : networkEventListeners) {
                networkEventListener.clientConnected();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds NetworkEventListener
     * @param toAdd NetworkEventListener to be added
     */
    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }

    /**
     * Sends given object through ObjectOutputStream
     * @param object object to be sent
     */
    public void send(Object object) {
        try {
            out.writeObject(object);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts messageWaiter thread and return it
     * @return started messageWaiter thread
     */
    public MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }

    /**
     * Waits for data structure containing clients nickname
     * @return object containing clients nickname
     */
    public Object waitForHandshake() {
        Object inObject = null;
        try {
            inObject = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inObject;
    }

    /**
     * Sends data structure that contains current game status with server and client nicknames
     * @param object
     */
    public void responseToHandshake(Object object) {
        try {
            out.writeObject(object);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes socket
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}