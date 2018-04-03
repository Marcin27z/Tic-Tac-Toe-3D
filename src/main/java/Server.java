package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server extends Thread {

    private Socket socket;
    ClientHandler clientHandler;
    private ServerSocket serverSocket;
    private List<NetworkEventListener> networkEventListeners = new ArrayList<>();

    Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();
                socket.setKeepAlive(true);
                clientHandler = new ClientHandler(socket);
                clientHandler.start();
                for (NetworkEventListener networkEventListener : networkEventListeners) {
                    networkEventListener.clientConnected();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }
}

class ClientHandler extends Thread {

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    //private MessagePoster messagePoster;
    private MessageWaiter messageWaiter;

    ClientHandler(Socket socket) {
        clientSocket = socket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            //messagePoster = new MessagePoster(out, clientSocket);
            in = new ObjectInputStream(clientSocket.getInputStream());
            messageWaiter = new MessageWaiter(in, clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void send(Model model) {
        try {
            out.writeObject(model);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*MessagePoster startMessagePoster() {
        messagePoster.start();
        return messagePoster;
    }*/

    MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }

    Model waitForHandshake() {
        System.out.println("in");
        Model inModel = null;
        try {
            inModel = (Model) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return inModel;
    }

    void responseToHandshake(Model model) {
        try {
            out.writeObject(model);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

interface NetworkEventListener {

    void newMessageArrived(Model model);

    void clientConnected();

    void establishedConnection();
}