package main.java.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.MyEvent;
import main.java.controller.SlaveController;
import main.java.model.Model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class MenuController {

    //private Model model;
    private Stage menuStage;
    private BlockingQueue<MyEvent> queue;

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
        if (!localPlayerOne.getText().equals("") && !localPlayerTwo.getText().equals("")) {
            /*model.setPlayersNames(localPlayerOne.getText(), localPlayerTwo.getText());*/
            setLocalErrorLabel(""); //
            /*startGame(Model.Mode.LOCAL);
            masterController.resetGameView();
            masterController.setNickNameLabel("");
            masterController.disconnect();*/
            try {
                queue.put(new MyEvent(MyEvent.MyEventType.START_LOCAL));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            closeMenu(); //
        } else {
            setLocalErrorLabel("You need to enter both names!");
        }
    }

    @FXML
    void join() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            /*model.player[1].setName(localOnlineGamePlayer.getText());*/
            setOnlineErrorLabel(""); //
            try {
                queue.put(new MyEvent(MyEvent.MyEventType.JOIN_SERVER));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*model.setIdentity(Model.Identity.CLIENT);
            startGame(Model.Mode.ONLINE);
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            try {
                masterController.joinServer(InetAddress.getByName(serverAddress.getText()), Integer.parseInt(serverJoinPort.getText()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }*/
        } else {
            setOnlineErrorLabel("You need to enter name!");
        }
    }

    @FXML
    void host() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            /*model.player[0].setName(localOnlineGamePlayer.getText());*/
            setOnlineErrorLabel(""); //
            /*model.setIdentity(Model.Identity.SERVER);
            startGame(Model.Mode.ONLINE);
            Random random = new Random();
            if(random.nextBoolean())
                model.changeTurn();
            masterController.setNickNameLabel(localOnlineGamePlayer.getText());
            masterController.startServer(Integer.parseInt(serverHostPort.getText()));*/
            try {
                queue.put(new MyEvent(MyEvent.MyEventType.HOST));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            setOnlineErrorLabel("You need to enter name!");
        }

    }

    public void closeMenu() {
        menuStage.close();
    }

    /*public void initModel(Model model) {
        this.model = model;
    }*/

    @FXML
    void onEnter() {
        startLocal();
    }

    public void initStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    /*private void startGame(Model.Mode mode) {
        model.setMode(mode);
        model.restart();
    }*/

    private void setOnlineErrorLabel(String errorText) {
        onlineErrorLabel.setText(errorText);
    }

    private void setLocalErrorLabel(String errorText) {
        localErrorLabel.setText(errorText);
    }

    public String getServerAddress() {
        return serverAddress.getText();
    }

    public String getLocalPlayerOne() {
        return localPlayerOne.getText();
    }

    public String getLocalPlayerTwo() {
        return localPlayerTwo.getText();
    }

    public String getLocalOnlineGamePlayer() {
        return localOnlineGamePlayer.getText();
    }

    public String getServerHostPort() {
        return serverHostPort.getText();
    }

    public String getServerJoinPort() {
        return serverJoinPort.getText();
    }

    public void initQueue(BlockingQueue<MyEvent> queue) {
        this.queue = queue;
    }
}
