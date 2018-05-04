package main.java.view;

import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Pair;
import main.java.MyEvent;

import java.util.concurrent.BlockingQueue;

public class GameView extends Group {

    private final PerspectiveCamera camera;
    private final PointLight light;
    private final Board[] boards;
    private final Group root3d;
    private final SubScene scene3d;
    private final BorderPane pane;
    private final BlockingQueue<MyEvent> queue;

    private final Rotate rotateX;
    private final Rotate rotateY;
    private double mouseOldX, mouseOldY, mousePosX, mousePosY;
    private final double DEF_ROTATE_X = 0, DEF_ROTATE_Y = 0, DEF_CAMERA_Z = 0;

    public GameView(BlockingQueue<MyEvent> queue) {
        this.queue = queue;
        rotateX = new Rotate(DEF_ROTATE_X, 225, 250, 225 ,Rotate.X_AXIS);
        rotateY = new Rotate(DEF_ROTATE_Y, 225, 250, 255, Rotate.Y_AXIS);
        light = new PointLight();
        light.setTranslateX(-225);
        light.setTranslateY(-225);
        light.setTranslateZ(-500);
        camera = new PerspectiveCamera(false);
        camera.setTranslateX(-150);
        camera.setTranslateY(-200);
        camera.setTranslateZ(DEF_CAMERA_Z);
        boards = new Board[4];
        for (int i = 0; i < 4; i++) {
            boards[i] = new Board(100 * (i + 1));
        }
        pane = new BorderPane();
        setTranslateX(20);
        setTranslateY(40);
        root3d = new Group(boards);
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
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                Field finalField = getBoard(i).getField(j);
                finalField.setOnMouseEntered(event -> {
                    finalField.setHovered();
                    event.consume();
                });
                finalField.setOnMouseExited(event -> {
                    finalField.setUnHovered();
                    event.consume();
                });

                int finalI = i;
                int finalJ = j;
                finalField.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        try {
                            this.queue.put(new MyEvent(MyEvent.MyEventType.CLICKED, new Pair<>(finalI, finalJ)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    event.consume();
                });
            }
        }
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
        rotateX.setAngle(DEF_ROTATE_X);
        rotateY.setAngle(DEF_ROTATE_Y);
        camera.setTranslateZ(DEF_CAMERA_Z);
    }

    public Board getBoard(int i) {
        return boards[i];
    }

    public void reset() {
        resetView();
        for(Board b : boards) {
            b.clear();
        }
    }
}
