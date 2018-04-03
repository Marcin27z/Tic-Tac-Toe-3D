package main.java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Server extends Thread {

    private Socket socket;
    ClientHandler clientHandler;
    private ServerSocket serverSocket;
    private List<NetworkEventListener> networkEventListeners = new ArrayList<>();
    private HandshakeListener toAdd;

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
                clientHandler.addHandshakeListener(toAdd);
                for (NetworkEventListener networkEventListener : networkEventListeners) {
                    networkEventListener.clientConnected();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    MessageWaiter getMessageWaiter() {
        return clientHandler.getMessageWaiter();
    }

    MessagePoster getMessagePoster() {
        return clientHandler.getMessagePoster();
    }*/

    void addClientHandlerHandshakeListener(HandshakeListener toAdd) {
        this.toAdd = toAdd;
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }
}

class ClientHandler extends Thread {

    private Socket clientSocket;
/*    private BufferedReader in;
    private DataOutputStream out;*/
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessagePoster messagePoster;
    private MessageWaiter messageWaiter;

    private List<HandshakeListener> hlisteners = new ArrayList<>();

    ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            messagePoster = new MessagePoster(out, clientSocket);


            /*messagePoster.start();
            messageWaiter.start();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MessagePoster startMessagePoster() {
        messagePoster.start();
        return messagePoster;
    }

    MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }

/*    MessageWaiter getMessageWaiter() {
        while(messageWaiter == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messageWaiter;
    }

    MessagePoster getMessagePoster() {
        while(messagePoster == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messagePoster;
    }*/

    void addHandshakeListener(HandshakeListener toAdd) {hlisteners.add(toAdd);}

    Model waitForHandshake() {
        /*try {
            Model secondPlayersName = (Model) in.readObject();
            hlisteners.get(0).getResponse().player[1].setName(secondPlayersName.player[1].getName());
            out.writeObject(hlisteners.get(0).getResponse());
            out.reset();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }*/
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            messageWaiter = new MessageWaiter(in, clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

interface HandshakeListener {
    void gotHandshakeRequest(Model model);

    Model getResponse();
}