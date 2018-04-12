package main.java;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class MenuController extends SlaveController{

    private Model model;
    private Stage menuStage;

    @FXML
    TextField localPlayerOne;
    @FXML
    TextField localPlayerTwo;
    @FXML
    TextField localOnlineGamePlayer;
    @FXML
    TextField serverAddress;
    @FXML
    TextField serverJoinPort;
    @FXML
    TextField serverHostPort;
    @FXML
    Label errorLabel;

    @FXML
    void startLocal() {
        if(!localPlayerOne.getText().equals("") && !localPlayerTwo.getText().equals("")) {
            model.setPlayersNames(localPlayerOne.getText(), localPlayerTwo.getText());
            startGame(Model.LOCAL);
            masterController.resetGameView();
            closeMenu();
        } else {
            System.out.println("You need to enter both names");
            //show error window
        }
    }

    @FXML
    void join() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            model.player[1].setName(localOnlineGamePlayer.getText());
            setErrorLabel("");
            model.me = 1;
            startGame(Model.ONLINE);
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            try {
                masterController.joinServer(InetAddress.getByName(serverAddress.getText()), Integer.parseInt(serverJoinPort.getText()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            setErrorLabel("You need to enter name!");
        }
    }

    @FXML
    void host() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            model.player[0].setName(localOnlineGamePlayer.getText());
            setErrorLabel("");
            model.me = 0;
            startGame(Model.ONLINE);
            Random random = new Random();
            if(random.nextBoolean())
                model.changeTurn();
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            masterController.startServer(Integer.parseInt(serverHostPort.getText()));
        } else {
            setErrorLabel("You need to enter name!");
        }
    }

    void closeMenu() {
        menuStage.close();
    }

    void initModel(Model model) {
        this.model = model;
    }

    @FXML
    void onEnter(ActionEvent actionEvent) {
        startLocal();
    }

    void initStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    private void startGame(boolean mode) {
        model.setMode(mode);
        model.restart();
    }

    private void setErrorLabel(String errorText) {
        errorLabel.setText(errorText);
    }
}
