package main.java.model;

public interface NetworkEventListener {

    void newMessageArrived(Model model);

    void clientConnected();

    void establishedConnection();

    void endDisconnected();
}