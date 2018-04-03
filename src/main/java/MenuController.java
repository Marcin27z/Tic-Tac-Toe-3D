package main.java;

import javafx.fxml.FXML;
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


    public void startLocal() {
        model.setPlayersNames(localPlayerOne.getText(), localPlayerTwo.getText());
        startGame(Model.LOCAL);
        menuStage.close();
    }

    public void join() {
        model.player[1].setName(localOnlineGamePlayer.getText());
        model.me = 1;
        startGame(Model.ONLINE);
        try {
            masterController.joinServer(InetAddress.getByName(serverAddress.getText()), Integer.parseInt(serverJoinPort.getText()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void host() {
        model.player[0].setName(localOnlineGamePlayer.getText());
        model.me = 0;
        startGame(Model.ONLINE);
        Random random = new Random();
        if(random.nextBoolean() == true)
            model.changeTurn();
        masterController.startServer(Integer.parseInt(serverHostPort.getText()));
    }

    public void closeMenu() {
        menuStage.close();
    }

    void initModel(Model model) {
        this.model = model;
    }

    void initStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    private void startGame(boolean mode) {
        model.setMode(mode);
        model.restart();
        masterController.resetGameView();
    }
}
