package main.java.view;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

/**
 * Basic class that creates the gameView
 */
public class Field extends Group {

    private final PhongMaterial material;
    private final Color basicColor = new Color(0.1,0.5,1,0.5);
    private final Color hoverColor = new Color(0.1, 0.3, 1, 0.5);
    private Sphere sphere;
    private Cross cross;
    private Box field;

    Field(double x, double y, double z) {
        field = new Box(128, 1, 128);
        field.setTranslateX(x);
        field.setTranslateY(y);
        field.setTranslateZ(z);
        material = new PhongMaterial();
        material.setDiffuseColor(basicColor);
        field.setMaterial(material);
        getChildren().add(field);
    }

    /**
     * Adds sphere on the field
     * @return status of the operation
     */
    public boolean addSphere() {
        if (cross != null || sphere != null)
            return false;
        sphere = new Sphere();
        PhongMaterial sphereMaterial = new PhongMaterial();
        sphereMaterial.setDiffuseColor(new Color(0.0, 0.0, 1, 1));
        sphere.setMaterial(sphereMaterial);
        sphere.setRadius(25);
        sphere.setTranslateX(field.getTranslateX());
        sphere.setTranslateY(field.getTranslateY() - 25);
        sphere.setTranslateZ(field.getTranslateZ());
        getChildren().add(sphere);
        return true;
    }

    /**
     * Adds cross on the field
     * @return status of the operation
     */
    public boolean addCross() {
        if (cross != null || sphere != null)
            return false;
        cross = new Cross(50);
        PhongMaterial cubeMaterial = new PhongMaterial();
        cubeMaterial.setDiffuseColor(Color.RED);
        cross.setMaterial(cubeMaterial);
        cross.setTranslateX(field.getTranslateX());
        cross.setTranslateY(field.getTranslateY() - 25);
        cross.setTranslateZ(field.getTranslateZ());
        Platform.runLater(() -> getChildren().add(cross));
        return true;
    }

    /**
     * Deletes sphere or cross from the field
     */
    void clearField() {
        getChildren().removeAll(cross, sphere); //?
        cross = null; //?
        sphere = null; //?
    }

    /**
     * Sets color of the field when hovered
     */
    void setHovered() {
        material.setDiffuseColor(hoverColor);
    }

    /**
     * Sets basic color when unhovered
     */
    void setUnHovered() {
        material.setDiffuseColor(basicColor);
    }

}
