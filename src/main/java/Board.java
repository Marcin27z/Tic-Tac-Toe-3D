package main.java;

import javafx.scene.Group;

class Board extends Group {
    private final Field[] field;
    Board (int y) {
        field = new Field[16];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                field[j + 4 * i] = new Field(150 * j, y, 150 * i);
                getChildren().add(field[j + 4 * i]);
            }
        }
    }

    void clear() {
        for(Field f: field) {
            f.clearField();
        }
    }

    Field getField(int i) {
        return field[i];
    }
}
