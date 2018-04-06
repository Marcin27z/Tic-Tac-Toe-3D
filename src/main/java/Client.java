package main.java;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Client extends Thread {
    private Socket socket;
    private InetAddress address;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageWaiter messageWaiter;
    private List<NetworkEventListener> networkEventListeners = new ArrayList<>();
    private int port;

    Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
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

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }

    MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }

    void initHandshake(Model model) {
        try {
            out.writeObject(model);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Model waitForResponse() {
        Model model = null;
        try {
            model = (Model) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    void send(Model model) {
        try {
            out.writeObject(model);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
