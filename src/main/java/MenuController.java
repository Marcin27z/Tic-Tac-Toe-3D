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
    Label onlineErrorLabel;
    @FXML
    Label localErrorLabel;

    @FXML
    void startLocal() {
        if(!localPlayerOne.getText().equals("") && !localPlayerTwo.getText().equals("")) {
            model.setPlayersNames(localPlayerOne.getText(), localPlayerTwo.getText());
            setLocalErrorLabel("");
            startGame(Model.LOCAL);
            masterController.resetGameView();
            masterController.setNickNameLabel("");
            masterController.disconnect();
            closeMenu();
        } else {
            setLocalErrorLabel("You need to enter both names!");
        }
    }

    @FXML
    void join() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            model.player[1].setName(localOnlineGamePlayer.getText());
            setOnlineErrorLabel("");
            model.setIdentity(Model.CLIENT);
            startGame(Model.ONLINE);
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            try {
                masterController.joinServer(InetAddress.getByName(serverAddress.getText()), Integer.parseInt(serverJoinPort.getText()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            setOnlineErrorLabel("You need to enter name!");
        }
    }

    @FXML
    void host() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            model.player[0].setName(localOnlineGamePlayer.getText());
            setOnlineErrorLabel("");
            model.setIdentity(Model.SERVER);
            startGame(Model.ONLINE);
            Random random = new Random();
            if(random.nextBoolean())
                model.changeTurn();
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            masterController.startServer(Integer.parseInt(serverHostPort.getText()));
        } else {
            setOnlineErrorLabel("You need to enter name!");
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

    private void setOnlineErrorLabel(String errorText) {
        onlineErrorLabel.setText(errorText);
    }

    private void setLocalErrorLabel(String errorText) {
        localErrorLabel.setText(errorText);
    }
}
