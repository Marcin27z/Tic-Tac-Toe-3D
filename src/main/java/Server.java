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
    private ClientHandler clientHandler;
    private ServerSocket serverSocket;
    private final List<NetworkEventListener> networkEventListeners = new ArrayList<>();
    private int port;

    Server(int port) {
        try {
            this.port = port;
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

    void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }

    ClientHandler getClientHandler() {
        return clientHandler;
    }

    int getPort() {
        return port;
    }
}

class ClientHandler extends Thread {

    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageWaiter messageWaiter;

    ClientHandler(Socket socket) {
        clientSocket = socket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
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

    void disconnect() {
        try {
            clientSocket.close();
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