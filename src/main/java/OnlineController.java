package main.java;

import javafx.application.Platform;

import java.net.InetAddress;
import java.util.Random;

class OnlineController extends SlaveController implements NetworkEventListener, HandshakeListener {

    private Model model;
    private Server server;
    private Client client;
    private MessageWaiter messageWaiter;
    private MessagePoster messagePoster;

    void initModel(Model model) {
        this.model = model;
    }

    void joinServer(InetAddress address, int port) {
        client = new Client(address, port);
        client.start();
        client.addNetworkEventListener(this);
        client.addHandshakeListener(this);
    }

    void startServer(int port) {
        server = new Server(port);
        server.start();
        server.addNetworkEventListener(this);
        server.addClientHandlerHandshakeListener(this);
    }

    void sendPlayerStatus() {
        messagePoster.send(model);
    }

    @Override
    public void establishedConnection() {
        client.initHandshake(model);
        Model inModel = client.waitForResponse();
        model.player[0].setName(inModel.player[0].getName());
        model.setTurn(inModel.getTurn());
        messagePoster = client.startMessagePoster();
        messageWaiter = client.startMessageWaiter();
       /* try {
            messagePoster.queue.put("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
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
        messagePoster = server.clientHandler.startMessagePoster();
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
        model.changeTurn();
        masterController.updateTurnLabel();
    }

    @Override
    public void gotHandshakeRequest(Model model) {
        model.player[1].setName(model.player[1].getName());
    }

    @Override
    public Model getResponse() {
        return model;
    }
}
