package main.java.controller;

import main.java.model.Model;
import java.net.InetAddress;

public class MasterController {

    private final GameController gameController;
    private final OnlineController onlineController;

    public MasterController(GameController gameController, Model model) {
        this.gameController = gameController;
        onlineController = new OnlineController(model);
        onlineController.initMasterController(this);
    }

    public void updateTurnLabel() {
        gameController.updateTurnLabel();
    }

    public void resetGameView() {
        gameController.resetView();
    }

    void sendPlayerStatus() {
        onlineController.sendPlayerStatus();
    }

    public void startServer(int port) {
        onlineController.startServer(port);
    }

    public void joinServer(InetAddress inetAddress, int port) {
        onlineController.joinServer(inetAddress, port);
        gameController.model.changeTurn();
    }

    void closeMenu() {
        gameController.closeMenu();
    }

    void updateBoard(int i, int j) {
        gameController.updateBoard(i , j);
    }

    public void disconnect() {
        onlineController.disconnect();
    }

    public void opponentDisconnected() {
        gameController.opponentDisconnected();
    }

    public void hostUnreachable() {
        gameController.hostUnreachable();
    }
}
