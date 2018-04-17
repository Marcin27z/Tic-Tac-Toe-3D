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

    Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        try {
            socket = serverSocket.accept();
            socket.setKeepAlive(true);
            clientHandler = new ClientHandler(socket);
            clientHandler.start();
            for (NetworkEventListener networkEventListener : networkEventListeners) {
                networkEventListener.clientConnected();
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

    void joinClientHandler() {
        try {
            clientHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            messageWaiter.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

interface NetworkEventListener {

    void newMessageArrived(Model model);

    void clientConnected();

    void establishedConnection();
}