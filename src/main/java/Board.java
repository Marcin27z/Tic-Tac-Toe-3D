package main.java;

import javafx.scene.Group;

class Board {
    final Field[] field;
    final Group board;
    Board (int y) {
        field = new Field[16];
        board = new Group();
        for (int i = 3; i >= 0; --i) {
            for (int j = 3; j >= 0; --j) {
                field[j + 4 * i] = new Field(150 * j, y, 150 * i);
                board.getChildren().add(field[j + 4 * i].field);
            }
        }
    }
}
