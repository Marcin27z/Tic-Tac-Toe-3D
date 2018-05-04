package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.controller.GameController;
import main.java.controller.MasterController;
import main.java.model.Model;
import main.java.view.Controller;
import main.java.view.GameView;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main extends Application {

    private Model model;
    private MasterController masterController;
    private BlockingQueue<MyEvent> eventPassingQueue;

    @Override
    public void start(Stage primaryStage) throws Exception {

        eventPassingQueue = new ArrayBlockingQueue<>(10);
        GameView gameView = new GameView(eventPassingQueue);
        model = new Model();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/view.fxml"));
        BorderPane root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        //controller.initModel(model);
        controller.initStage(primaryStage);
        Group view = new Group(root);

        GameController gameController = new GameController(gameView, model, primaryStage, controller, eventPassingQueue);
        gameController.start();

        masterController = new MasterController(gameController, model);
        gameController.initMasterController(masterController);
        //controller.initMasterController(masterController);
        controller.initQueue(eventPassingQueue);

        Scene scene = new Scene(view, 988, 888, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Toe 3D");
        primaryStage.show();
        gameController.invokeMenu(true);
        //masterController.updateTurnLabel(model.player[model.getCurrentPlayer()].getName());
        view.getChildren().add(gameView);
        primaryStage.setOnCloseRequest(event -> {
            //showExitConfirmationWindow();
            event.consume();
            System.exit(0);
        });
    }

    private void showExitConfirmationWindow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");
        alert.initStyle(StageStyle.UTILITY);
        ButtonType exitButtonType = new ButtonType("Exit");
        ButtonType cancelButtonType = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(exitButtonType, cancelButtonType);
        alert.showAndWait().ifPresent(result -> {
            if (result == exitButtonType){
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
