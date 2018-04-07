package main.java;

import javafx.application.Platform;
import java.net.InetAddress;
import java.util.ArrayList;

class OnlineController extends SlaveController implements NetworkEventListener {

    private Model model;
    private Server server;
    private final ArrayList<Server> servers = new ArrayList<>();
    private ClientHandler clientHandler;
    private Client client;
    private MessageWaiter messageWaiter;

    void initModel(Model model) {
        this.model = model;
    }

    void joinServer(InetAddress address, int port) {
        client = new Client(address, port);
        client.start();
        client.addNetworkEventListener(this);
    }

    void startServer(int port) {
        boolean found = false;
        if(server != null) {
            for(Server s : servers) {
                if(s.getPort() == port) {
                    server = s;
                    found = true;
                }
            }
        }
        if(!found){
            server = new Server(port);
            servers.add(server);
            server.start();
            server.addNetworkEventListener(this);
        }
    }

    void sendPlayerStatus() {
        if(model.me == 0) clientHandler.send(model);
        else client.send(model);
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
        masterController.updateBoard(inMessage.y, inMessage.f);
        if(!model.player[model.getCurrentPlayer()].checkWin()) {
            model.changeTurn();
            masterController.updateTurnLabel();
        }
    }

    private void startGame() {
        Platform.runLater(() -> {
            masterController.resetGameView();
            masterController.closeMenu();
        });
    }

}
