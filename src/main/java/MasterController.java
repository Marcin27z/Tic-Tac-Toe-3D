package main.java;

import java.net.InetAddress;

class MasterController {

    private GameController gameController;
    private Controller controller;
    private OnlineController onlineController;

    MasterController(GameController gameController, Controller controller, Model model) {
        //this.menuController = menuController;
        this.gameController = gameController;
        this.controller = controller;
        onlineController = new OnlineController();
        onlineController.initMasterController(this);
        onlineController.initModel(model);
    }

    void updateTurnLabel() {
        controller.updateTurnLabel();
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

    public void invokeMenu() {
        controller.invokeMenu(false);
    }
}
