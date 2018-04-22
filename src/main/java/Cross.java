package main.java;

import javafx.scene.Group;
import javafx.scene.paint.Material;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

/**
 * Basic element of the view. Can be added to the field
 */
class Cross extends Group {

    private Box[] arms;

    Cross(double diameter) {
        arms = new Box[3];
        for(int i = 0; i < 3; ++i) {
            arms[i] = new Box(diameter, diameter/10, diameter/10);
        }
        arms[0].getTransforms().addAll(new Rotate(45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        arms[1].getTransforms().addAll(new Rotate(0, 0, 0, 0, Rotate.Y_AXIS), new Rotate(-45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        arms[2].getTransforms().addAll(new Rotate(-45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        /*arms[0].getTransforms().addAll(new Rotate(45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        arms[1].getTransforms().addAll(new Rotate(45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(-45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        arms[2].getTransforms().addAll(new Rotate(-45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));
        arms[3].getTransforms().addAll(new Rotate(-45, 0, 0, 0, Rotate.Y_AXIS), new Rotate(-45, 0, 0, 0, Rotate.Z_AXIS),new Rotate(45, 0, 0, 0, Rotate.X_AXIS));*/
        getChildren().addAll(arms);
        //getTransforms().addAll(new Rotate(45, 0, 0 , 0, Rotate.Y_AXIS), new Rotate(45, 0, 0 , 0, Rotate.Z_AXIS));
    }

    /**
     * Sets material of the cross
     * @param material material to set
     */
    void setMaterial(Material material) {
        for(int i = 0; i < 3; ++i) {
            arms[i].setMaterial(material);
        }
    }
}
