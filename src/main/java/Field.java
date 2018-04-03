package main.java;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;


class Field extends Box {
    private PhongMaterial material;
    private Color basicColor = new Color(0.1,0.5,1,0.5);
    private Color hoverColor = new Color(0.1, 0.3, 1, 0.5);
    private Sphere sphere;
    private Box cube;
    Group field;
    Field(double x, double y, double z) {
        super(128, 1, 128);
        field = new Group(this);
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
        if(cube != null || sphere != null)
            return false;
        sphere = new Sphere();
        PhongMaterial sphereMaterial = new PhongMaterial();
        sphereMaterial.setDiffuseColor(Color.BLUE);
        sphere.setMaterial(sphereMaterial);
        sphere.setRadius(25);
        sphere.setTranslateX(getTranslateX());
        sphere.setTranslateY(getTranslateY() - 25);
        sphere.setTranslateZ(getTranslateZ());
        field.getChildren().add(sphere);
        return true;
    }

    boolean addCube() {
        if(cube != null || sphere != null)
            return false;
        cube = new Box(40, 40, 40);
        PhongMaterial cubeMaterial = new PhongMaterial();
        cubeMaterial.setDiffuseColor(Color.RED);
        cube.setMaterial(cubeMaterial);;
        cube.setTranslateX(getTranslateX());
        cube.setTranslateY(getTranslateY() - 25);
        cube.setTranslateZ(getTranslateZ());
        Platform.runLater(() -> field.getChildren().add(cube));
        return true;
    }

    void clearField() {
        field.getChildren().removeAll(cube, sphere); //?
        cube = null; //?
        sphere = null; //?
    }
    void setHovered() {
        material.setDiffuseColor(hoverColor);
    }
    void setUnHovered() {
        material.setDiffuseColor(basicColor);
    }

}
