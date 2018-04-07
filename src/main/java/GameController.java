package main.java;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;

class GameController extends SlaveController {

    private final GameView view;
    private final Model model;

    GameController(GameView gameView, Model model) {
        this.model = model;
        this.view = gameView;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                int finalI = i;
                int finalJ = j;
                Field finalField = view.boards[i].field[j];

                finalField.setOnMouseEntered(event -> {
                    finalField.setHovered();
                    event.consume();
                });
                finalField.setOnMouseExited(event -> {
                    finalField.setUnHovered();
                    event.consume();
                });
                finalField.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (((model.getTurn() != 4 && model.me == 0) || (model.getTurn() != 3 && model.me == 1)) && model.getTurn() != 5) {
                            boolean result = model.makeMove(model.getCurrentPlayer(), finalI, finalJ);
                            if (result) {
                                makeBoardMove(finalField, model.getCurrentPlayer());
                                if (model.getMode() == Model.ONLINE)
                                    masterController.sendPlayerStatus();
                                if (!localCheckWin()) {
                                    model.changeTurn();
                                    masterController.updateTurnLabel();
                                } else {
                                    model.setTurn(5);
                                }
                            }
                        }
                        event.consume();
                    }
                });
            }
        }
    }

    private boolean localCheckWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            System.out.println("Win");
            showWinAlert(model.player[model.getCurrentPlayer()].getName());
            return true;
        }
        return false;
    }

    private void checkWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            System.out.println("Win");
            Platform.runLater(() -> showWinAlert(model.player[model.getCurrentPlayer()].getName()));
        }
    }

    private void makeBoardMove(Field field, int player) {
        if(player == 0) field.addSphere();
        else field.addCube();
    }

    void updateBoard(int i, int j) {
        //model.player[model.getCurrentPlayer()].makeMove(i, j);
        if(model.me == 0)
            Platform.runLater(() -> view.boards[i].field[j].addCube());
        else
            Platform.runLater(() -> view.boards[i].field[j].addSphere());
        checkWin();
    }

    private void showWinAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("");
        alert.setHeaderText(name + " won!!!");
        alert.setTitle("");
        alert.showAndWait().ifPresent(response -> {
            while (response != ButtonType.OK) {

            }
            masterController.invokeMenu();
        });
    }

    void resetView() {
        view.reset();
    }
}
