package main.java;

import javafx.scene.Group;

/**
 * Groups of fields on the same level
 */
class Board extends Group {

    private final Field[] field;

    /**
     * Creates Board containing 16 fields
     * @param y level of the board
     */
    Board (int y) {
        field = new Field[16];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                field[j + 4 * i] = new Field(150 * j, y, 150 * i);
                getChildren().add(field[j + 4 * i]);
            }
        }
    }

    /**
     * Clears all the spheres and crosses from the board
     */
    void clear() {
        for(Field f: field) {
            f.clearField();
        }
    }

    /**
     * @param i index of the field on the board
     * @return field with given index
     */
    Field getField(int i) {
        return field[i];
    }
}
