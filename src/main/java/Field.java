package main.java;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;


class Field extends Box {
    private final PhongMaterial material;
    private final Color basicColor = new Color(0.1,0.5,1,0.5);
    private final Color hoverColor = new Color(0.1, 0.3, 1, 0.5);
    Sphere sphere;
    Cross cross;// = new Cross(0);
    final Group field;
    Field(double x, double y, double z) {
        super(128, 1, 128);
        //sphere = new Sphere(50);
        field = new Group( this);
        setTranslateX(x);
        setTranslateY(y);
        setTranslateZ(z);
        material = new PhongMaterial();
        material.setDiffuseColor(basicColor);
        setMaterial(material);
        /*setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                material.setDiffuseColor(hoverColor);
                event.consume();
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                material.setDiffuseColor(basicColor);
                event.consume();
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sphere = new Sphere();
                PhongMaterial sphereMaterial = new PhongMaterial();
                sphereMaterial.setDiffuseColor(Color.MAGENTA);
                sphere.setMaterial(sphereMaterial);
                sphere.setRadius(45);
                sphere.setTranslateX(getTranslateX());
                sphere.setTranslateY(getTranslateY() - 45);
                sphere.setTranslateZ(getTranslateZ());
                field.getChildren().addAll(sphere);
                event.consume();
            }
        });*/
    }

    boolean addSphere() {
        if (cross != null || sphere != null)
            return false;
        sphere = new Sphere();
        PhongMaterial sphereMaterial = new PhongMaterial();
        sphereMaterial.setDiffuseColor(new Color(0.0, 0.0, 1, 1));
        sphere.setMaterial(sphereMaterial);
        sphere.setRadius(25);
        sphere.setTranslateX(getTranslateX());
        sphere.setTranslateY(getTranslateY() - 25);
        sphere.setTranslateZ(getTranslateZ());
        field.getChildren().add(sphere);
        return true;
    }

    boolean addCross() {
        if (cross != null || sphere != null)
            return false;
       // cube = new Box(40, 40, 40);
        cross = new Cross(50);
        PhongMaterial cubeMaterial = new PhongMaterial();
        cubeMaterial.setDiffuseColor(Color.RED);
        cross.setMaterial(cubeMaterial);
        cross.setTranslateX(getTranslateX());
        cross.setTranslateY(getTranslateY() - 25);
        cross.setTranslateZ(getTranslateZ());
        Platform.runLater(() -> field.getChildren().add(cross));
        return true;
    }

    void clearField() {
        field.getChildren().removeAll(cross, sphere); //?
        cross = null; //?
        sphere = null; //?
    }
    void setHovered() {
        material.setDiffuseColor(hoverColor);
    }

    void setUnHovered() {
        material.setDiffuseColor(basicColor);
    }

}
