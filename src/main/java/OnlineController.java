package main.java;

import javafx.application.Platform;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class OnlineController extends SlaveController implements NetworkEventListener {

    private Model model;
    private Server server;
    private final ArrayList<ServerSocket> serverSockets = new ArrayList<>();
    private ClientHandler clientHandler;
    private Client client;
    private MessageWaiter messageWaiter;
    private ServerSocket serverSocket;

    void initModel(Model model) {
        this.model = model;
    }

    void joinServer(InetAddress address, int port) {
        Socket socket = null;
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        client = new Client(socket);
        client.start();
        client.addNetworkEventListener(this);
    }

    void startServer(int port) {
        if (server != null) {
            if (clientHandler != null) {
                clientHandler.disconnect();
            }
            server.joinClientHandler();
            try {
                server.join();
                clientHandler = null;
                server = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean found = false;
        for (ServerSocket ss : serverSockets) {
            if(ss.getLocalPort() == port) {
                serverSocket = ss;
                found = true;
            }
        }
        if (!found){
            try {
                serverSocket = new ServerSocket(port);
                serverSockets.add(serverSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (server == null) {
            server = new Server(serverSocket);
            server.addNetworkEventListener(this);
            server.start();
        }
    }

    void sendPlayerStatus() {
        if (model.getIdentity() == Model.SERVER)
            clientHandler.send(model);
        else
            client.send(model);
    }

    @Override
    public void establishedConnection() {
        client.initHandshake(model);
        Model inModel = client.waitForResponse();
        model.player[0].setName(inModel.player[0].getName());
        model.setTurn(inModel.getTurn());
        messageWaiter = client.startMessageWaiter();
        messageWaiter.addNetworkEventListener(this);
        startGame();
    }

    @Override
    public void clientConnected() {
        clientHandler = server.getClientHandler();
        Model inModel = clientHandler.waitForHandshake();
        model.player[1].setName(inModel.player[1].getName());
        clientHandler.responseToHandshake(model);
        messageWaiter = clientHandler.startMessageWaiter();
        messageWaiter.addNetworkEventListener(this);
        startGame();
    }

    @Override
    public void newMessageArrived(Model inMessage) {
        model.player = inMessage.player;
        masterController.updateBoard(inMessage.getLatestMoveY(), inMessage.getLatestMoveF());
        if (!model.player[model.getCurrentPlayer()].checkWin()) {
            model.changeTurn();
            masterController.updateTurnLabel();
        } else {
            disconnect();
        }

    }

    private void startGame() {
        Platform.runLater(() -> {
            masterController.resetGameView();
            masterController.closeMenu();
        });
    }

    void disconnect() {
        if (model.getIdentity() == Model.CLIENT) {
            if(client != null) {
                client.disconnect();
                try {
                    client.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (clientHandler != null) {
            clientHandler.disconnect();
            if (server != null) {
                try {
                    server.joinClientHandler();
                    server.join();
                    server = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void endDisconnected() {
        disconnect();
    }
}
