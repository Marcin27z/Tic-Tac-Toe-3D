package main.java;

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

import java.io.IOException;


public class Controller extends SlaveController {

    Model model;
    private Stage primaryStage;
    private MenuController menuController;

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

    void updateTurnLabel() {
        Platform.runLater(() -> turnLabel.setText(model.player[model.getCurrentPlayer()].getName()));
    }

    void setNickNameLabel(String nickName) {
        nickNameLabel.setText(nickName);
    }

    @FXML
    void close() {
        //show confirmation
        System.exit(0);
    }

    @FXML
    void newGame() {
        invokeMenu(false);
    }

    @FXML
    void about() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../resources/about.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene dialogScene = new Scene(root, 500, 270);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    void initModel(Model model) {
        this.model = model;
    }

    void initStage(Stage stage) {
        primaryStage = stage;
    }

    void invokeMenu(boolean isCrucial) {
        Stage menu = new Stage();
        menu.initModality(Modality.APPLICATION_MODAL);
        menu.initOwner(primaryStage);
        Parent menuRoot = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/menu.fxml"));
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
        if(model.getTurn() != 5) updateTurnLabel();
    }

    void closeMenu() {
        menuController.closeMenu();
    }
}
