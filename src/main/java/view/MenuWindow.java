package main.java.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.MyEvent;
import java.util.concurrent.BlockingQueue;

public class MenuWindow {

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
            setLocalErrorLabel("");
            try {
                queue.put(new MyEvent(MyEvent.MyEventType.START_LOCAL));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            closeMenu();
        } else {
            setLocalErrorLabel("You need to enter both names!");
        }
    }

    @FXML
    void join() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            setOnlineErrorLabel(""); //
            try {
                queue.put(new MyEvent(MyEvent.MyEventType.JOIN_SERVER));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            setOnlineErrorLabel("You need to enter name!");
        }
    }

    @FXML
    void host() {
        if (!localOnlineGamePlayer.getText().equals("")) {
            setOnlineErrorLabel(""); //
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

    @FXML
    void onEnter() {
        startLocal();
    }

    public void initStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

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
