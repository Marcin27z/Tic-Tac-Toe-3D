package main.java;

import javafx.application.Platform;
import java.net.InetAddress;

class OnlineController extends SlaveController implements NetworkEventListener {

    private Model model;
    private Server server;
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
        server = new Server(port);
        server.start();
        server.addNetworkEventListener(this);
    }

    void sendPlayerStatus() {
        if(model.me == 0) server.clientHandler.send(model);
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
        Platform.runLater(() -> {
            masterController.closeMenu();
        });
    }

    @Override
    public void clientConnected() {
        Model inModel = server.clientHandler.waitForHandshake();
        model.player[1].setName(inModel.player[1].getName());
        server.clientHandler.responseToHandshake(model);
        messageWaiter = server.clientHandler.startMessageWaiter();
        messageWaiter.addNetworkEventListener(this);
        Platform.runLater(() -> {
            masterController.closeMenu();
        });
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

}
