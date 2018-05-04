package main.java.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.MyEvent;
import main.java.controller.SlaveController;
import main.java.model.Model;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Controls view around the gameView
 */
public class Controller {

    //Model model;
    private Stage primaryStage;
    private MenuController menuController;
    private BlockingQueue<MyEvent> queue;

    @FXML
    MenuBar menuBar;
    @FXML
    Menu fileMenu;
    @FXML
    Menu helpMenu;
    @FXML
    MenuItem closeItem;
    @FXML
    MenuItem aboutItem;
    @FXML
    Label turnLabel;
    @FXML
    Label nickNameLabel;

    /**
     * Sets turnLabel to the current value from the model
     */
    public void updateTurnLabel(String text) {
        Platform.runLater(() -> turnLabel.setText(text));
    }

    /**
     * Sets nickname label to the given value
     * @param nickName value to set
     */
    public void setNickNameLabel(String nickName) {
        nickNameLabel.setText(nickName);
    }

    /**
     * Closes the app
     */
    @FXML
    void close() {
        //show confirmation
        System.exit(0);
    }

    /**
     * Invokes the menu in order to start new game
     */
    @FXML
    void newGame() {
        invokeMenu(false);
    }

    /**
     * Opens about window with basic information about the program
     */
    @FXML
    void about() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../../resources/about.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene dialogScene = new Scene(root, 500, 270);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /*public void initModel(Model model) {
        this.model = model;
    }*/

    public void initStage(Stage stage) {
        primaryStage = stage;
    }

    public void initQueue(BlockingQueue<MyEvent> queue) {
        this.queue = queue;
    }

    /**
     * Invokes menu
     * @param isCrucial if true closing menu closes application, otherwise closing menu has no effect
     */
    public void invokeMenu(boolean isCrucial) {
        /*Stage menu = new Stage();
        menu.initModality(Modality.APPLICATION_MODAL);
        menu.initOwner(primaryStage);
        Parent menuRoot = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../resources/menu.fxml"));
        try {
            menuRoot = fxmlLoader.load();
            menuController = fxmlLoader.getController();
            menuController.initModel(model);
            menuController.initStage(menu);
            menuController.initMasterController(masterController);
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
        if(model.getTurn() != Model.Turn.GAMEOVER) updateTurnLabel(model.player[model.getCurrentPlayer()].getName());*/

        try {
            /*if(isCrucial)
                queue.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU_CRUCIAL));
            else
                queue.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU));*/
            queue.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU, isCrucial));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeMenu() {
        try {
            queue.put(new MyEvent(MyEvent.MyEventType.CLOSE_MENU));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //menuController.closeMenu();
    }
}
