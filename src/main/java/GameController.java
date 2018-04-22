package main.java;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.stage.StageStyle;

/**
 * Controls game state
 */
class GameController extends SlaveController {

    private final GameView view;
    private final Model model;

    /**
     * Creates controller of the gameView and model
     * @param gameView view to control
     * @param model model to control
     */
    GameController(GameView gameView, Model model) {
        this.model = model;
        this.view = gameView;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 16; j++) {
                int finalI = i;
                int finalJ = j;
                Field finalField = view.getBoard(i).getField(j);

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

    /**
     * Checks if move made by local player caused him to win and if so shows alert
     * @return true if player won, false otherwise
     */
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

    /**
     * Checks if move made by remote player caused him to win and if so shows alert
     */
    private void checkWin() {
        if (model.player[model.getCurrentPlayer()].checkWin()) {
            String winner = model.player[model.getCurrentPlayer()].getName();
            Platform.runLater(() -> showWinAlert(winner));
        }
    }

    /**
     * Places sphere or cross according to the given player id on the given field
     * @param field field to place sphere or cross
     * @param player id whether to place sphere or cross
     */
    private void makeBoardMove(Field field, int player) {
        if (player == 0) field.addSphere();
        else field.addCross();
    }

    /**
     * Places cross on the given field if the application is running as server or sphere if is running as client
     * @param i id of the board
     * @param j id of the field on the board
     */
    void updateBoard(int i, int j) {
        if (model.getIdentity() == Model.SERVER)
            Platform.runLater(() -> view.getBoard(i).getField(j).addCross());
        else
            Platform.runLater(() -> view.getBoard(i).getField(j).addSphere());
        checkWin();
    }

    /**
     * Creates alert, shows it and waits for OK button to be clicked, when clicked OK invokes menu
     * @param name name of the player who won
     */
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
