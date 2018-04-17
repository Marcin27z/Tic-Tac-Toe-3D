package main.java;

import java.net.InetAddress;

class MasterController {

    private final GameController gameController;
    private final Controller controller;
    private final OnlineController onlineController;

    MasterController(GameController gameController, Controller controller, Model model) {
        this.gameController = gameController;
        this.controller = controller;
        onlineController = new OnlineController();
        onlineController.initMasterController(this);
        onlineController.initModel(model);
    }

    void updateTurnLabel() {
        controller.updateTurnLabel();
    }

    void setNickNameLabel(String nickName) {
        controller.setNickNameLabel(nickName);
    }

    void resetGameView() {
        gameController.resetView();
    }

    void sendPlayerStatus() {
        onlineController.sendPlayerStatus();
    }

    void startServer(int port) {
        onlineController.startServer(port);
    }

    void joinServer(InetAddress inetAddress, int port) {
        onlineController.joinServer(inetAddress, port);
        controller.model.changeTurn();
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

    void disconnect() {
        onlineController.disconnect();
    }
}
