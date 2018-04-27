package main.java.controller;

import main.java.view.Controller;
import main.java.model.Model;

import java.net.InetAddress;

public class MasterController {

    private final GameController gameController;
    private final Controller controller;
    private final OnlineController onlineController;

    public MasterController(GameController gameController, Controller controller, Model model) {
        this.gameController = gameController;
        this.controller = controller;
        onlineController = new OnlineController(model);
        onlineController.initMasterController(this);
    }

    public void updateTurnLabel(String text) {
        controller.updateTurnLabel(text);
    }

    public void setNickNameLabel(String nickName) {
        controller.setNickNameLabel(nickName);
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
        controller.closeMenu();
    }

    void updateBoard(int i, int j) {
        gameController.updateBoard(i , j);
    }

    void invokeMenu() {
        controller.invokeMenu(false);
    }

    public void disconnect() {
        onlineController.disconnect();
    }

    public void opponentDisconnected() {
        gameController.opponentDisconnected();
    }
}
