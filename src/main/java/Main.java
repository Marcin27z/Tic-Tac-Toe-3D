package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Model model;
    private MasterController masterController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        GameView gameView = new GameView();
        model = new Model();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../resources/view.fxml"));
        BorderPane root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.initModel(model);
        controller.initStage(primaryStage);
        Group view = new Group(root);

        GameController gameController = new GameController(gameView, model);

        masterController = new MasterController(gameController, controller, model);
        gameController.initMasterController(masterController);
        controller.initMasterController(masterController);

        Scene scene = new Scene(view, 988, 888, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Toe 3D");
        primaryStage.show();
        controller.invokeMenu(true);
        masterController.updateTurnLabel();
        view.getChildren().add(gameView);
        primaryStage.setOnCloseRequest(event -> {
            //confirmation window
            System.exit(0);
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
