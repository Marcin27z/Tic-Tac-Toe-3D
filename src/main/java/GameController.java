package main.java;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.stage.StageStyle;

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
                Field finalField = view.boards[i].getField(j);

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
                        if (((model.getTurn() != 4 && model.getIdentity() == Model.SERVER) || (model.getTurn() != 3 && model.getIdentity() == Model.CLIENT)) && model.getTurn() != 5) {
                            boolean result = model.makeMove(model.getCurrentPlayer(), finalI, finalJ);
                            if (result) {
                                makeBoardMove(finalField, model.getCurrentPlayer());
                                if (model.getMode() == Model.ONLINE)
                                    masterController.sendPlayerStatus();
                                if (!localCheckWin()) {
                                    model.changeTurn();
                                    masterController.updateTurnLabel();
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
            String winner = model.player[model.getCurrentPlayer()].getName();
            model.setEndGame();
            if(model.getMode() == Model.ONLINE)
                masterController.disconnect();
            showWinAlert(winner);
            return true;
        }
        return false;
    }

    private void checkWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            String winner = model.player[model.getCurrentPlayer()].getName();
            Platform.runLater(() -> showWinAlert(winner));
        }
    }

    private void makeBoardMove(Field field, int player) {
        if (player == 0) field.addSphere();
        else field.addCross();
    }

    void updateBoard(int i, int j) {
        //model.player[model.getCurrentPlayer()].makeMove(i, j);
        if (model.getIdentity() == Model.SERVER)
            Platform.runLater(() -> view.boards[i].getField(j).addCross());
        else
            Platform.runLater(() -> view.boards[i].getField(j).addSphere());
        checkWin();
    }

    private void showWinAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("");
        alert.setContentText(name + " won!!!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait().ifPresent(result -> {
            if(result == ButtonType.OK) {
                masterController.invokeMenu();
            }
        });
    }

    void resetView() {
        view.reset();
    }
}
