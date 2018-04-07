package main.java;

import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

class GameView extends Group {

    private final PerspectiveCamera camera;
    private final PointLight light;
    final Board[] boards;
    private final Group root3d;
    private final SubScene scene3d;
    private final BorderPane pane;

    private final Rotate rotateX;
    private final Rotate rotateY;
    private double mouseOldX, mouseOldY, mousePosX, mousePosY;
    final private double defRotateX = 0, defRotateY = 0, defCameraZ = 0;

    GameView() {

        rotateX = new Rotate(defRotateX, 225, 250, 225 ,Rotate.X_AXIS);
        rotateY = new Rotate(defRotateY, 225, 250, 255, Rotate.Y_AXIS);
        light = new PointLight();
        light.setTranslateX(-225);
        light.setTranslateY(-225);
        light.setTranslateZ(-500);
        camera = new PerspectiveCamera(false);
        camera.setTranslateX(-150);
        camera.setTranslateY(-200);
        camera.setTranslateZ(defCameraZ);
        boards = new Board[4];
        for (int i = 0; i < 4; i++) {
            boards[i] = new Board(100 * (i + 1));
        }
        pane = new BorderPane();
        setTranslateX(20);
        setTranslateY(40);
        root3d = new Group(boards[0].board, boards[1].board, boards[2].board, boards[3].board);
        root3d.getTransforms().addAll (rotateX, rotateY, new Translate(0, 0, 0));
        scene3d = new SubScene(new Group(light, root3d),768,768, true, SceneAntialiasing.BALANCED );
        scene3d.setCamera(camera);
        pane.setPrefSize(768, 768);
        pane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pane.setBackground(new Background(new BackgroundFill(Color.color(0.1, 0.5, 0.5, 1), new CornerRadii(1),
                new Insets(0.0,0.0,0.0,0.0))));
        pane.setCenter(scene3d);
        getChildren().add(pane);


        setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                mouseOldX = event.getSceneX();
                mouseOldY = event.getSceneY();
            }
        });
        setOnMouseDragged(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();
                rotateCamera(mousePosY - mouseOldY, mousePosX - mouseOldX);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });
        setOnScroll(event -> zoom(event.getDeltaY()));

        Button resetButton = new Button("center");
        resetButton.setTranslateX(700);
        resetButton.setTranslateY(700);
        resetButton.setOnAction(event -> resetView());
        getChildren().add(resetButton);
    }

    private void rotateCamera(double xAngle, double yAngle) {
        rotateX.setAngle(rotateX.getAngle() - xAngle);
        rotateY.setAngle(rotateY.getAngle() - yAngle);
    }

    private void zoom(double distance) {
        camera.setTranslateZ(camera.getTranslateZ() + distance);
    }

    private void resetView() {
        rotateX.setAngle(defRotateX);
        rotateY.setAngle(defRotateY);
        camera.setTranslateZ(defCameraZ);
    }

    void reset() {
        resetView();
        for(Board b : boards) {
            for(Field f: b.field) {
                f.clearField();
            }
        }
    }
}
