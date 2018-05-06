package main.java.controller;

import javafx.application.Platform;
import main.java.model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Window that handles interactions between server or client threads and other controllers or model
 */
class OnlineController extends SlaveController implements NetworkEventListener {

    private Model model;
    private Server server;
    private final ArrayList<ServerSocket> serverSockets = new ArrayList<>();
    //private ClientHandler clientHandler;
    private Client client;
    private MessageWaiter messageWaiter;
    private ServerSocket serverSocket;

    /**
     * Creates controller
     * @param model model to control
     */
    OnlineController(Model model) {
        this.model = model;
    }

    /**
     * Creates socket and starts Client thread with this socket
     * @param address address for the socket to bind
     * @param port port for the socket to bind
     */
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

    /**
     * Stops running server, checks if there is already ServerSocket on given port and creates new if there isn't, starts server with this socket
     * @param port port for listening for incoming connections
     */
    void startServer(int port) {
        if (server != null) {
            try {
                server.join();
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

    /**
     * Sends current game status
     */
    void sendPlayerStatus() {
        if (model.getIdentity() == Model.Identity.SERVER)
            server.send(model);
        else
            client.send(model);
    }

    /**
     * Performs handshake on client side and starts the game
     */
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

    /**
     * Performs handshake on server side and starts the game
     */
    @Override
    public void clientConnected() {
        Model inModel = (Model) server.waitForHandshake();
        model.player[1].setName(inModel.player[1].getName());
        server.responseToHandshake(model);
        messageWaiter = server.startMessageWaiter();
        messageWaiter.addNetworkEventListener(this);
        startGame();
    }

    /**
     * Handles the incoming game status, board and turn
     * @param inMessage incoming game state
     */
    @Override
    public void newMessageArrived(Model inMessage) {
        model.board = inMessage.board;
        masterController.updateBoard(inMessage.getLatestMoveY(), inMessage.getLatestMoveF());
        if (!model.player[model.getCurrentPlayer()].checkWin()) {
            model.changeTurn();
            masterController.updateTurnLabel();
        } else {
            disconnect();
        }

    }

    /**
     * Resets gameView and closes menu
     */
    private void startGame() {
        Platform.runLater(() -> {
            masterController.resetGameView();
            masterController.closeMenu();
        });
    }

    /**
     * Closes socket and joins all threads
     */
    void disconnect() {
        if (model.getIdentity() == Model.Identity.CLIENT) {
            if(client != null) {
                client.disconnect();
                try {
                    client.join();
                    client = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (server != null) {
            server.disconnect();
            try {
                server.join();
                server = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void endDisconnected() {
        disconnect();
        if(model.getTurn() != Model.Turn.GAME_OVER)
            Platform.runLater(() -> masterController.opponentDisconnected());
    }
}
