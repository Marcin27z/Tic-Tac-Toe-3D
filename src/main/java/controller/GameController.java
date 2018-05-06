package main.java.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import main.java.MyEvent;
import main.java.model.Model;
import main.java.view.Window;
import main.java.view.Field;
import main.java.view.GameView;
import main.java.view.MenuWindow;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

interface CustomAction {
    void perform(Object args);
}

/**
 * Controls game state
 */
public class GameController extends Thread {

    private final GameView view;
    private final Window window;
    public final Model model;
    private Stage primaryStage;
    private Map<MyEvent, CustomAction> map;
    private MenuWindow menuWindow;
    private BlockingQueue<MyEvent> queue;
    private MasterController masterController;
    private boolean running = true;

    public void initMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    /**
     * Creates window of the gameView and model
     * @param gameView view to control
     * @param model model to control
     * @param primaryStage main stage
     */
    public GameController(GameView gameView, Model model, Stage primaryStage, Window window, BlockingQueue<MyEvent> queue) {
        map = new HashMap<>();
        this.window = window;
        this.model = model;
        this.view = gameView;
        this.primaryStage = primaryStage;
        this.queue = queue;
    }


    /**
     * Action performed when filed is clicked
     * @param i y coordinate of the clicked field
     * @param j index of field clicked
     */
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
                    window.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
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
            window.updateTurnLabel("Game Over");
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
            Platform.runLater(() -> {
                window.updateTurnLabel("Game Over");
                showWinAlert(winner);
            });
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
        window.updateTurnLabel("Game Over");
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
     * Performed on INVOKE_MENU event
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
            menuWindow = fxmlLoader.getController();
            menuWindow.initStage(menu);
            menuWindow.initQueue(queue);
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
    }

    /**
     * Closes menu
     * Performed on CLOSE event
     */
    public void closeMenu() {
        updateTurnLabel();
        menuWindow.closeMenu();
    }

    /**
     * Starts new game
     * Performed on START_LOCAL event
     */
    private void startLocal() {
        model.setPlayersNames(menuWindow.getLocalPlayerOne(), menuWindow.getLocalPlayerTwo());
        startGame(Model.Mode.LOCAL);
        resetView();
        window.setNickNameLabel("");
        masterController.disconnect();
        updateTurnLabel();
    }

    /**
     * Performed on HOST event
     */
    private void host() {
        model.player[0].setName(menuWindow.getLocalOnlineGamePlayer());
        model.setIdentity(Model.Identity.SERVER);
        startGame(Model.Mode.ONLINE);
        Random random = new Random();
        if(random.nextBoolean())
            model.changeTurn();
        window.setNickNameLabel(menuWindow.getLocalOnlineGamePlayer());
        masterController.startServer(Integer.parseInt(menuWindow.getServerHostPort()));
    }

    /**
     * Performed on JOIN_SERVER event
     */
    private void joinServer() {
        model.player[1].setName(menuWindow.getLocalOnlineGamePlayer());
        model.setIdentity(Model.Identity.CLIENT);
        startGame(Model.Mode.ONLINE);
        window.setNickNameLabel(menuWindow.getLocalOnlineGamePlayer());
        try {
            masterController.joinServer(InetAddress.getByName(menuWindow.getServerAddress()), Integer.parseInt(menuWindow.getServerJoinPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void startGame(Model.Mode mode) {
        model.setMode(mode);
        model.restart();
    }

    /**
     * Performed on STOP event
     */
    private void stopController() {
        running = false;
    }

    public void updateTurnLabel() {
        window.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
    }


    @Override
    public void run() {
        map.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU), (args) -> invokeMenu((boolean)args));
        map.put(new MyEvent(MyEvent.MyEventType.HOST), (args) -> host());
        map.put(new MyEvent(MyEvent.MyEventType.JOIN_SERVER), (args) -> joinServer());
        map.put(new MyEvent(MyEvent.MyEventType.START_LOCAL), (args) -> startLocal());
        map.put(new MyEvent(MyEvent.MyEventType.CLICKED), (args) -> clicked(((Pair<Integer, Integer>)args).getKey(), ((Pair<Integer, Integer>)args).getValue()));
        map.put(new MyEvent(MyEvent.MyEventType.STOP), (args) -> stopController());
        MyEvent event = null;
        while (running) {
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
