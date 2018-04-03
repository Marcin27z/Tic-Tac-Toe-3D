package main.java;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

class View extends Group {

    MenuBar menuBar;
    Menu fileMenu;
    MenuItem menuItem;
    Group root;
    Scene scene;
    GameView gameView;

    View() {
        fileMenu = new Menu("File");
        menuBar = new MenuBar(fileMenu);
        menuItem = new MenuItem("Exit");
        fileMenu.getItems().add(menuItem);
        menuBar.setPrefSize(988, 19);
        Pane backgroundPane = new Pane();
        backgroundPane.setPrefSize(988, 888);
        backgroundPane.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(1),
                new Insets(0.0,0.0,0.0,0.0))));
        //root = new Group(backgroundPane, menuBar);
        //scene = new Scene(root, 988, 888, true);
        Button button = new Button("click me!");
        //root.getChildren().add(button);
        getChildren().addAll(backgroundPane, menuBar, button);
        button.setPrefSize(100, 50);
        button.setTranslateX(808);
        button.setTranslateY(100);
    }
}
