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

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Controls view around the gameView
 */
public class Controller {

    private Stage primaryStage;
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
    private void invokeMenu(boolean isCrucial) {
        try {
            queue.put(new MyEvent(MyEvent.MyEventType.INVOKE_MENU, isCrucial));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
