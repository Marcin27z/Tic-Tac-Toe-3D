package main.java.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import main.java.MyEvent;
import main.java.model.Model;
import main.java.view.Controller;
import main.java.view.Field;
import main.java.view.GameView;
import main.java.view.MenuController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Controls game state
 */

interface CustomAction {
    void perform(Object args);
}

public class GameController extends Thread {

    private final GameView view;
    private final Controller controller;
    public final Model model;
    private Stage primaryStage;
    private Map<MyEvent, CustomAction> map;
    private MenuController menuController;
    private BlockingQueue<MyEvent> queue;
    private MasterController masterController;

    public void initMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    /**
     * Creates controller of the gameView and model
     * @param gameView view to control
     * @param model model to control
     * @param primaryStage main stage
     */
    public GameController(GameView gameView, Model model, Stage primaryStage, Controller controller, BlockingQueue<MyEvent> queue) {
        map = new HashMap<>();
        this.controller = controller;
        this.model = model;
        this.view = gameView;
        this.primaryStage = primaryStage;
        this.queue = queue;
        /*for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                int finalI = i;
                int finalJ = j;
                Field finalField = view.getBoard(i).getField(j);

                finalField.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (((model.getTurn() != Model.Turn.REMOTE_PLAYER_TURN && model.getIdentity() != Model.Identity.CLIENT)
                                || (model.getTurn() != Model.Turn.LOCAL_PLAYER_TURN && model.getIdentity() != Model.Identity.SERVER)) && model.getTurn() != Model.Turn.GAME_OVER) {
                            boolean result = model.makeMove(model.getCurrentPlayer(), finalI, finalJ);
                            if (result) {
                                makeBoardMove(finalField, model.getCurrentPlayer());
                                if (model.getMode() == Model.Mode.ONLINE)
                                    masterController.sendPlayerStatus();
                                if (!localCheckWin()) {
                                    model.changeTurn();
                                    masterController.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
                                }
                            }
                        }
                        event.consume();
                    }
                });
            }
        }*/
    }

    private void clicked(int i, int j) {
        if (((model.getTurn() != Model.Turn.REMOTE_PLAYER_TURN && model.getIdentity() != Model.Identity.CLIENT)
                || (model.getTurn() != Model.Turn.LOCAL_PLAYER_TURN && model.getIdentity() != Model.Identity.SERVER)) && model.getTurn() != Model.Turn.GAME_OVER) {
            boolean result = model.makeMove(model.getCurrentPlayer(), i, j);
            if (result) {
                makeBoardMove(view.getBoard(i).getField(j), model.getCurrentPlayer());
                if (model.getMode() == Model.Mode.ONLINE)
                    masterController.sendPlayerStatus();
                if (!localCheckWin()) {
                    model.changeTurn();
                    controller.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
                }
            }
        }
    }

    /**
     * Checks if move made by local player caused him to win and if so shows alert
     * @return true if player won, false otherwise
     */
    private boolean localCheckWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            String winner = model.player[model.getCurrentPlayer()].getName();
            model.setEndGame();
            if(model.getMode() == Model.Mode.ONLINE)
                masterController.disconnect();
            showWinAlert(winner);
            return true;
        }
        return false;
    }

    /**
     * Checks if move made by remote player caused him to win and if so shows alert
     */
    private void checkWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            String winner = model.player[model.getCurrentPlayer()].getName();
            Platform.runLater(() -> showWinAlert(winner));
        }
    }

    /**
     * Places sphere or cross according to the given player id on the given field
     * @param field field to place sphere or cross
     * @param player id whether to place sphere or cross
     */
    private void makeBoardMove(Field field, int player) {
        if (player == 0) field.addSphere();
        else field.addCross();
    }

    /**
     * Places cross on the given field if the application is running as server or sphere if is running as client
     * @param i id of the board
     * @param j id of the field on the board
     */
    void updateBoard(int i, int j) {
        if (model.getIdentity() == Model.Identity.SERVER)
            Platform.runLater(() -> view.getBoard(i).getField(j).addCross());
        else
            Platform.runLater(() -> view.getBoard(i).getField(j).addSphere());
        checkWin();
    }

    /**
     * Creates alert, shows it and waits for OK button to be clicked, when clicked OK invokes menu
     * @param name name of the player who won
     */
    private void showWinAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("");
        alert.setContentText(name + " won!!!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait().ifPresent(result -> {
            if(result == ButtonType.OK) {
                invokeMenu(false);
            }
        });
    }

    /**
     * Ends game, updateds turn label and shows alert when opponent disconnects
     */
    void opponentDisconnected() {
        model.setEndGame();
        controller.updateTurnLabel("Game Over");
        showDisconnectAlert();
    }

    /**
     * Shows alert with text that opponent has disconnected
     */
    private void showDisconnectAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("");
        alert.setContentText("Opponent has disconnected");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    void resetView() {
        view.reset();
    }

    /**
     * Invokes menu
     * @param isCrucial if true closing menu closes application, otherwise closing menu has no effect
     */
    public void invokeMenu(boolean isCrucial) {
        Stage menu = new Stage();
        menu.initModality(Modality.APPLICATION_MODAL);
        menu.initOwner(primaryStage);
        Parent menuRoot = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../resources/menu.fxml"));
        try {
            menuRoot = fxmlLoader.load();
            menuController = fxmlLoader.getController();
            //menuController.initModel(model);
            menuController.initStage(menu);
            menuController.initQueue(queue);
           // menuController.initMasterController(masterController);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene dialogScene = new Scene(menuRoot, 500, 300);
        menu.setScene(dialogScene);
        menu.setOnCloseRequest(event -> {
            if (isCrucial)
                System.exit(0);
        });
        menu.showAndWait();
        //if(model.getTurn() != Model.Turn.GAME_OVER)
            //updateTurnLabel();
    }

    public void closeMenu() {
        updateTurnLabel();
        menuController.closeMenu();
    }

    private void startLocal() {
        model.setPlayersNames(menuController.getLocalPlayerOne(), menuController.getLocalPlayerTwo());
        startGame(Model.Mode.LOCAL);
        resetView();
        controller.setNickNameLabel("");
        masterController.disconnect();
        updateTurnLabel();
    }

    private void host() {
        model.player[0].setName(menuController.getLocalOnlineGamePlayer());
        model.setIdentity(Model.Identity.SERVER);
        startGame(Model.Mode.ONLINE);
        Random random = new Random();
        if(random.nextBoolean())
            model.changeTurn();
        controller.setNickNameLabel(menuController.getLocalOnlineGamePlayer());
        masterController.startServer(Integer.parseInt(menuController.getServerHostPort()));
        updateTurnLabel();
    }

    private void joinServer() {
        model.player[1].setName(menuController.getLocalOnlineGamePlayer());
        model.setIdentity(Model.Identity.CLIENT);
        startGame(Model.Mode.ONLINE);
        controller.setNickNameLabel(menuController.getLocalOnlineGamePlayer());
        try {
            masterController.joinServer(InetAddress.getByName(menuController.getServerAddress()), Integer.parseInt(menuController.getServerJoinPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void startGame(Model.Mode mode) {
        model.setMode(mode);
        model.restart();
    }

    public void updateTurnLabel() {
        controller.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
    }

    public void setNickNameLabel(String text) {
        controller.setNickNameLabel(text);
    }

    @Override
    public void run() {
        map.put(new MyEvent(MyEvent.MyEventType.CLOSE_MENU), (args) -> closeMenu());
        map.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU), (args) -> invokeMenu((boolean)args));
        map.put(new MyEvent(MyEvent.MyEventType.HOST), (args) -> host());
        map.put(new MyEvent(MyEvent.MyEventType.JOIN_SERVER), (args) -> joinServer());
        map.put(new MyEvent(MyEvent.MyEventType.START_LOCAL), (args) -> startLocal());
        map.put(new MyEvent(MyEvent.MyEventType.CLICKED), (args) -> clicked(((Pair<Integer, Integer>)args).getKey(), ((Pair<Integer, Integer>)args).getValue()));
        MyEvent event = null;
        while (true) {
            try {
                event = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                MyEvent finalEvent = event;
                Platform.runLater(() -> map.get(finalEvent).perform(finalEvent.getArgs()));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
